package com.example.storesaas.mini.service;

import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.OrderStatus;
import com.example.storesaas.mini.CustomerContext;
import com.example.storesaas.mini.dto.MiniOrderDTO;
import com.example.storesaas.customer.entity.CustomerAddress;
import com.example.storesaas.mini.vo.AddressVO;
import com.example.storesaas.customer.service.AddressService;
import com.example.storesaas.mini.vo.MiniOrderDetailVO;
import com.example.storesaas.mini.vo.MiniOrderItemVO;
import com.example.storesaas.mini.vo.MiniOrderVO;
import com.example.storesaas.mini.vo.OrderPreviewVO;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.domain.OrderRepository;
import com.example.storesaas.order.application.OrderPricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MiniOrderService {
    private final OrderRepository orderRepository;
    private final OrderPricingService pricingService;
    private final AddressService addresses;

    public MiniOrderService(OrderRepository orderRepository, OrderPricingService pricingService, AddressService a) {
        this.orderRepository = orderRepository;
        this.pricingService = pricingService;
        addresses = a;
    }

    public OrderPreviewVO preview(MiniOrderDTO r) {
        Calculation c = calculate(r);
        BigDecimal deliveryFee = deliveryFee(r);
        return new OrderPreviewVO(c.items.stream().map(MiniOrderItemVO::from).toList(), c.total,
                deliveryFee, c.total.add(deliveryFee));
    }

    @Transactional
    public MiniOrderVO create(MiniOrderDTO r) {
        Calculation c = calculate(r);
        BigDecimal deliveryFee = deliveryFee(r);
        StoreOrder o = new StoreOrder();
        o.setTenantId(CustomerContext.tenantId());
        o.setCustomerId(CustomerContext.customerId());
        o.setOrderNo("M" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ThreadLocalRandom.current().nextInt(1000, 9999));
        o.setStatus(OrderStatus.PENDING_PAY);
        o.setTotalAmount(c.total.add(deliveryFee));
        o.setDeliveryFee(deliveryFee);
        o.setFulfillmentType(r.fulfillmentType());
        o.setRemark(r.remark());
        o.setSource("MINI");
        o.setAddressSnapshot(snapshot(r));
        fill(o);
        orderRepository.saveOrder(o);
        for (OrderItem i : c.items) {
            i.setOrderId(o.getId());
            fill(i);
            orderRepository.saveItem(i);
        }
        return MiniOrderVO.from(o);
    }

    public List<MiniOrderVO> list() {
        return orderRepository.findTenantOrders(CustomerContext.tenantId()).stream()
                .filter(order -> Objects.equals(order.getCustomerId(), CustomerContext.customerId()))
                .map(MiniOrderVO::from).toList();
    }

    public MiniOrderDetailVO detail(Long id) {
        StoreOrder o = owned(id);
        List<MiniOrderItemVO> orderItems = orderRepository.findTenantItems(CustomerContext.tenantId(), id)
                .stream().map(MiniOrderItemVO::from).toList();
        return new MiniOrderDetailVO(MiniOrderVO.from(o), orderItems);
    }

    @Transactional
    public MiniOrderVO cancel(Long id) {
        StoreOrder o = owned(id);
        if (!OrderStatus.PENDING_PAY.equals(o.getStatus())) throw new BusinessException("当前订单不可取消");
        o.setStatus(OrderStatus.CANCELLED);
        o.setUpdatedAt(LocalDateTime.now());
        orderRepository.updateOrder(o);
        return MiniOrderVO.from(o);
    }

    private StoreOrder owned(Long id) {
        StoreOrder o = orderRepository.findCustomerOrder(CustomerContext.tenantId(), CustomerContext.customerId(), id);
        if (o == null) throw new BusinessException("订单不存在");
        return o;
    }

    private Calculation calculate(MiniOrderDTO r) {
        OrderPricingService.PricingResult pricing = pricingService.price(CustomerContext.tenantId(),
                r.items().stream().map(x -> new OrderPricingService.OrderLine(x.productId(), x.quantity())).toList());
        return new Calculation(pricing.items(), pricing.total());
    }

    private BigDecimal deliveryFee(MiniOrderDTO r) {
        if ("DELIVERY".equals(r.fulfillmentType())) {
            if (r.addressId() == null) throw new BusinessException("配送订单需要地址");
            return BigDecimal.valueOf(5);
        }
        if (!"SELF_PICKUP".equals(r.fulfillmentType())) throw new BusinessException("履约方式不支持");
        return BigDecimal.ZERO;
    }


    private String snapshot(MiniOrderDTO r) {
        if (r.addressId() == null) return null;
        AddressVO a = addresses.list().stream().filter(x -> x.id().equals(r.addressId())).findFirst()
                .orElseThrow(() -> new BusinessException("地址不存在"));
        return a.consignee() + " " + a.phone() + " " + a.province() + a.city() + a.district() + a.detail();
    }

    private void fill(Object x) {
        LocalDateTime n = LocalDateTime.now();
        if (x instanceof StoreOrder o) {
            o.setCreatedAt(n);
            o.setUpdatedAt(n);
            o.setDeleted(0);
        }
        if (x instanceof OrderItem i) {
            i.setCreatedAt(n);
            i.setUpdatedAt(n);
            i.setDeleted(0);
        }
    }

    private record Calculation(List<OrderItem> items, BigDecimal total) {
    }
}
