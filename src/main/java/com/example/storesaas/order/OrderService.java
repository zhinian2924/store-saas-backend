package com.example.storesaas.order;

import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.BusinessConstants;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.OrderStatus;
import com.example.storesaas.common.constants.PaymentStatus;
import com.example.storesaas.common.constants.ProductStatus;
import com.example.storesaas.catalog.api.ProductReader;
import com.example.storesaas.catalog.api.ProductSnapshot;
import com.example.storesaas.order.dto.CreateOrderDTO;
import com.example.storesaas.order.domain.OrderRepository;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.vo.OrderItemVO;
import com.example.storesaas.order.vo.OrderVO;
import com.example.storesaas.payment.entity.PaymentOrder;
import com.example.storesaas.payment.mapper.PaymentOrderMapper;
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
    private final OrderRepository orderRepository;
    private final PaymentOrderMapper paymentOrderMapper;
    private final ProductReader productReader;

    public OrderService(OrderRepository orderRepository, PaymentOrderMapper paymentOrderMapper, ProductReader productReader) {
        this.orderRepository = orderRepository;
        this.paymentOrderMapper = paymentOrderMapper;
        this.productReader = productReader;
    }

    @Transactional
    public OrderVO create(CreateOrderDTO request) {
        Long tenantId = AuthContext.tenantId();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderDTO.Item item : request.items()) {
            ProductSnapshot product = productReader.getTenantProduct(tenantId, item.productId());
            if (product.status() == null || product.status() != ProductStatus.ON_SALE) {
                throw new BusinessException(product.name() + "当前不可销售");
            }
            if (product.stock() < item.quantity()) {
                throw new BusinessException(product.name() + "库存不足");
            }
            total = total.add(product.price().multiply(BigDecimal.valueOf(item.quantity())));
        }

        StoreOrder order = new StoreOrder();
        order.setTenantId(tenantId);
        order.setCustomerId(AuthContext.currentUser().userId());
        order.setOrderNo(no(BusinessConstants.ORDER_NO_PREFIX));
        order.setStatus(OrderStatus.PENDING_PAY);
        order.setTotalAmount(total);
        fill(order);
        orderRepository.saveOrder(order);

        for (CreateOrderDTO.Item requestItem : request.items()) {
            ProductSnapshot product = productReader.getTenantProduct(tenantId, requestItem.productId());
            OrderItem item = new OrderItem();
            item.setTenantId(tenantId);
            item.setOrderId(order.getId());
            item.setProductId(product.productId());
            item.setProductName(product.name());
            item.setPrice(product.price());
            item.setQuantity(requestItem.quantity());
            item.setAmount(product.price().multiply(BigDecimal.valueOf(requestItem.quantity())));
            fill(item);
            orderRepository.saveItem(item);
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
        return OrderVO.from(order);
    }

    public List<OrderVO> list() {
        Long tenantId = AuthContext.tenantId();
        return orderRepository.findTenantOrders(tenantId).stream().map(OrderVO::from).toList();
    }

    public List<OrderItemVO> items(Long orderId) {
        Long tenantId = AuthContext.tenantId();
        return orderRepository.findTenantItems(tenantId, orderId).stream().map(OrderItemVO::from).toList();
    }

    private String no(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                ThreadLocalRandom.current().nextInt(BusinessConstants.ORDER_NO_RANDOM_MIN, BusinessConstants.ORDER_NO_RANDOM_MAX);
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
