package com.example.storesaas.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.inventory.InventoryService;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.mapper.OrderItemMapper;
import com.example.storesaas.order.mapper.StoreOrderMapper;
import com.example.storesaas.payment.entity.PaymentOrder;
import com.example.storesaas.payment.mapper.PaymentOrderMapper;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentOrderMapper paymentOrderMapper;
    private final StoreOrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    public PaymentService(PaymentOrderMapper paymentOrderMapper, StoreOrderMapper orderMapper, OrderItemMapper itemMapper, ProductMapper productMapper, InventoryService inventoryService) {
        this.paymentOrderMapper = paymentOrderMapper;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.productMapper = productMapper;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public StoreOrder mockPay(Long orderId) {
        Long tenantId = AuthContext.tenantId();
        StoreOrder order = orderMapper.selectOne(new LambdaQueryWrapper<StoreOrder>()
                .eq(StoreOrder::getTenantId, tenantId)
                .eq(StoreOrder::getId, orderId)
                .eq(StoreOrder::getDeleted, 0));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if ("PAID".equals(order.getStatus())) {
            return order;
        }
        if (!"PENDING_PAY".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }

        List<OrderItem> items = itemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getTenantId, tenantId)
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getDeleted, 0));
        for (OrderItem item : items) {
            Product before = productMapper.selectById(item.getProductId());
            if (before == null || !tenantId.equals(before.getTenantId())) {
                throw new BusinessException("商品不存在");
            }
            int affected = productMapper.deductStock(tenantId, item.getProductId(), item.getQuantity());
            if (affected != 1) {
                throw new BusinessException(before.getName() + "库存不足");
            }
            Product after = productMapper.selectById(item.getProductId());
            inventoryService.createFlow(tenantId, item.getProductId(), "ORDER_OUT", -item.getQuantity(), before.getStock(), after.getStock(), "订单支付扣减:" + order.getOrderNo());
        }

        PaymentOrder paymentOrder = paymentOrderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>()
                .eq(PaymentOrder::getTenantId, tenantId)
                .eq(PaymentOrder::getOrderId, orderId)
                .eq(PaymentOrder::getDeleted, 0)
                .last("limit 1"));
        if (paymentOrder != null) {
            paymentOrder.setStatus("SUCCESS");
            paymentOrder.setUpdatedAt(LocalDateTime.now());
            paymentOrderMapper.updateById(paymentOrder);
        }
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return order;
    }
}
