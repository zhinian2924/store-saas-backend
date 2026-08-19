package com.example.storesaas.order;

import com.example.storesaas.common.constants.BusinessConstants;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.OrderStatus;
import com.example.storesaas.order.application.OrderPricingService;
import com.example.storesaas.order.dto.CreateOrderDTO;
import com.example.storesaas.order.domain.OrderRepository;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.vo.OrderItemVO;
import com.example.storesaas.order.vo.OrderVO;
import com.example.storesaas.payment.api.PaymentOrderCreator;
import com.example.storesaas.identity.security.AuthContext;
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
    private final PaymentOrderCreator paymentOrderCreator;
    private final OrderPricingService pricingService;

    public OrderService(OrderRepository orderRepository, PaymentOrderCreator paymentOrderCreator, OrderPricingService pricingService) {
        this.orderRepository = orderRepository;
        this.paymentOrderCreator = paymentOrderCreator;
        this.pricingService = pricingService;
    }

    @Transactional
    public OrderVO create(CreateOrderDTO request) {
        Long tenantId = AuthContext.tenantId();
        OrderPricingService.PricingResult pricing = pricingService.price(tenantId,
                request.items().stream().map(item -> new OrderPricingService.OrderLine(item.productId(), item.quantity())).toList());
        BigDecimal total = pricing.total();

        StoreOrder order = new StoreOrder();
        order.setTenantId(tenantId);
        order.setCustomerId(AuthContext.currentUser().userId());
        order.setOrderNo(no(BusinessConstants.ORDER_NO_PREFIX));
        order.setStatus(OrderStatus.PENDING_PAY);
        order.setTotalAmount(total);
        fill(order);
        orderRepository.saveOrder(order);

        for (OrderItem item : pricing.items()) {
            item.setTenantId(tenantId);
            item.setOrderId(order.getId());
            fill(item);
            orderRepository.saveItem(item);
        }

        paymentOrderCreator.create(tenantId, order.getId(), total, BusinessConstants.PAY_CHANNEL_MOCK);
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
    }
}
