package com.example.storesaas.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.platform.error.BusinessException;
import com.example.storesaas.platform.persistence.DeleteStatus;
import com.example.storesaas.order.domain.OrderStatus;
import com.example.storesaas.payment.domain.PaymentStatus;
import com.example.storesaas.inventory.api.InventoryReservation;
import com.example.storesaas.inventory.api.InventoryReservation.ReservationItem;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.domain.OrderRepository;
import com.example.storesaas.payment.entity.PaymentOrder;
import com.example.storesaas.payment.mapper.PaymentOrderMapper;
import com.example.storesaas.identity.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentOrderMapper paymentOrderMapper;
    private final OrderRepository orderRepository;
    private final InventoryReservation inventoryReservation;

    public PaymentService(PaymentOrderMapper paymentOrderMapper, OrderRepository orderRepository,
                          InventoryReservation inventoryReservation) {
        this.paymentOrderMapper = paymentOrderMapper;
        this.orderRepository = orderRepository;
        this.inventoryReservation = inventoryReservation;
    }

    @Transactional
    public StoreOrder mockPay(Long orderId) {
        Long tenantId = AuthContext.tenantId();
        StoreOrder order = orderRepository.findTenantOrder(tenantId, orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (OrderStatus.PAID.equals(order.getStatus())) {
            return order;
        }
        if (!OrderStatus.PENDING_PAY.equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }

        List<OrderItem> items = orderRepository.findTenantItems(tenantId, orderId);
        inventoryReservation.reserve(
                tenantId,
                orderId,
                items.stream()
                        .map(item -> new ReservationItem(item.getProductId(), item.getQuantity()))
                        .toList()
        );

        PaymentOrder paymentOrder = paymentOrderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>()
                .eq(PaymentOrder::getTenantId, tenantId)
                .eq(PaymentOrder::getOrderId, orderId)
                .eq(PaymentOrder::getDeleted, DeleteStatus.NOT_DELETED)
                .last("limit 1"));
        if (paymentOrder != null) {
            paymentOrder.setStatus(PaymentStatus.SUCCESS);
            paymentOrder.setUpdatedAt(LocalDateTime.now());
            paymentOrderMapper.updateById(paymentOrder);
        }
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.updateOrder(order);
        return order;
    }
}
