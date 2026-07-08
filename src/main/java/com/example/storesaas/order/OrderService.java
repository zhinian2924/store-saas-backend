package com.example.storesaas.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.BusinessConstants;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.OrderStatus;
import com.example.storesaas.common.constants.PaymentStatus;
import com.example.storesaas.common.constants.ProductStatus;
import com.example.storesaas.order.dto.CreateOrderRequest;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.mapper.OrderItemMapper;
import com.example.storesaas.order.mapper.StoreOrderMapper;
import com.example.storesaas.payment.entity.PaymentOrder;
import com.example.storesaas.payment.mapper.PaymentOrderMapper;
import com.example.storesaas.product.ProductService;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final StoreOrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final ProductService productService;

    public OrderService(StoreOrderMapper orderMapper, OrderItemMapper itemMapper, PaymentOrderMapper paymentOrderMapper, ProductService productService) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.paymentOrderMapper = paymentOrderMapper;
        this.productService = productService;
    }

    @Transactional
    public StoreOrder create(CreateOrderRequest request) {
        Long tenantId = AuthContext.tenantId();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.Item item : request.items()) {
            Product product = productService.tenantProduct(tenantId, item.productId());
            if (product.getStatus() == null || product.getStatus() != ProductStatus.ON_SALE) {
                throw new BusinessException(product.getName() + "当前不可销售");
            }
            if (product.getStock() < item.quantity()) {
                throw new BusinessException(product.getName() + "库存不足");
            }
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }

        StoreOrder order = new StoreOrder();
        order.setTenantId(tenantId);
        order.setCustomerId(AuthContext.currentUser().userId());
        order.setOrderNo(no(BusinessConstants.ORDER_NO_PREFIX));
        order.setStatus(OrderStatus.PENDING_PAY);
        order.setTotalAmount(total);
        fill(order);
        orderMapper.insert(order);

        for (CreateOrderRequest.Item requestItem : request.items()) {
            Product product = productService.tenantProduct(tenantId, requestItem.productId());
            OrderItem item = new OrderItem();
            item.setTenantId(tenantId);
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(requestItem.quantity());
            item.setAmount(product.getPrice().multiply(BigDecimal.valueOf(requestItem.quantity())));
            fill(item);
            itemMapper.insert(item);
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setTenantId(tenantId);
        paymentOrder.setOrderId(order.getId());
        paymentOrder.setPayNo(no(BusinessConstants.PAY_NO_PREFIX));
        paymentOrder.setChannel(BusinessConstants.PAY_CHANNEL_MOCK);
        paymentOrder.setStatus(PaymentStatus.WAITING);
        paymentOrder.setAmount(total);
        fill(paymentOrder);
        paymentOrderMapper.insert(paymentOrder);
        return order;
    }

    public List<StoreOrder> list() {
        Long tenantId = AuthContext.tenantId();
        return orderMapper.selectList(new LambdaQueryWrapper<StoreOrder>()
                .eq(StoreOrder::getTenantId, tenantId)
                .eq(StoreOrder::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByDesc(StoreOrder::getId));
    }

    public List<OrderItem> items(Long orderId) {
        Long tenantId = AuthContext.tenantId();
        return itemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getTenantId, tenantId)
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getDeleted, DeleteStatus.NOT_DELETED));
    }

    private String no(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ThreadLocalRandom.current().nextInt(BusinessConstants.ORDER_NO_RANDOM_MIN, BusinessConstants.ORDER_NO_RANDOM_MAX);
    }

    private void fill(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity instanceof StoreOrder order) {
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            order.setDeleted(DeleteStatus.NOT_DELETED);
        }
        if (entity instanceof OrderItem item) {
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            item.setDeleted(DeleteStatus.NOT_DELETED);
        }
        if (entity instanceof PaymentOrder paymentOrder) {
            paymentOrder.setCreatedAt(now);
            paymentOrder.setUpdatedAt(now);
            paymentOrder.setDeleted(DeleteStatus.NOT_DELETED);
        }
    }
}
