package com.example.storesaas.mini.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.OrderStatus;
import com.example.storesaas.mini.CustomerContext;
import com.example.storesaas.mini.dto.MiniOrderRequest;
import com.example.storesaas.mini.entity.CustomerAddress;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.mapper.OrderItemMapper;
import com.example.storesaas.order.mapper.StoreOrderMapper;
import com.example.storesaas.product.ProductService;
import com.example.storesaas.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MiniOrderService {
    private final StoreOrderMapper orders;
    private final OrderItemMapper items;
    private final ProductService products;
    private final AddressService addresses;

    public MiniOrderService(StoreOrderMapper o, OrderItemMapper i, ProductService p, AddressService a) {
        orders = o;
        items = i;
        products = p;
        addresses = a;
    }

    public Map<String, Object> preview(MiniOrderRequest r) {
        Calculation c = calculate(r);
        BigDecimal deliveryFee = deliveryFee(r);
        return Map.of("items", c.items, "totalAmount", c.total, "deliveryFee", deliveryFee, "payAmount", c.total.add(deliveryFee));
    }

    @Transactional
    public StoreOrder create(MiniOrderRequest r) {
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
        orders.insert(o);
        for (OrderItem i : c.items) {
            i.setOrderId(o.getId());
            fill(i);
            items.insert(i);
        }
        return o;
    }

    public List<StoreOrder> list() {
        return orders.selectList(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getTenantId, CustomerContext.tenantId()).eq(StoreOrder::getCustomerId, CustomerContext.customerId()).eq(StoreOrder::getDeleted, 0).orderByDesc(StoreOrder::getId));
    }

    public Map<String, Object> detail(Long id) {
        StoreOrder o = owned(id);
        return Map.of("order", o, "items", items.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getTenantId, CustomerContext.tenantId()).eq(OrderItem::getOrderId, id).eq(OrderItem::getDeleted, 0)));
    }

    @Transactional
    public StoreOrder cancel(Long id) {
        StoreOrder o = owned(id);
        if (!OrderStatus.PENDING_PAY.equals(o.getStatus())) throw new BusinessException("当前订单不可取消");
        o.setStatus(OrderStatus.CANCELLED);
        o.setUpdatedAt(LocalDateTime.now());
        orders.updateById(o);
        return o;
    }

    private StoreOrder owned(Long id) {
        StoreOrder o = orders.selectOne(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getTenantId, CustomerContext.tenantId()).eq(StoreOrder::getCustomerId, CustomerContext.customerId()).eq(StoreOrder::getId, id).eq(StoreOrder::getDeleted, 0));
        if (o == null) throw new BusinessException("订单不存在");
        return o;
    }

    private Calculation calculate(MiniOrderRequest r) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> result = new ArrayList<>();
        for (MiniOrderRequest.Item x : r.items()) {
            Product p = products.tenantProduct(CustomerContext.tenantId(), x.productId());
            if (p.getStatus() == null || p.getStatus() != 1) throw new BusinessException(p.getName() + "当前不可销售");
            if (p.getStock() < x.quantity()) throw new BusinessException(p.getName() + "库存不足");
            OrderItem i = new OrderItem();
            i.setTenantId(CustomerContext.tenantId());
            i.setProductId(p.getId());
            i.setProductName(p.getName());
            i.setImageUrl(p.getImageUrl());
            i.setPrice(p.getPrice());
            i.setQuantity(x.quantity());
            i.setAmount(p.getPrice().multiply(BigDecimal.valueOf(x.quantity())));
            total = total.add(i.getAmount());
            result.add(i);
        }
        return new Calculation(result, total);
    }

    private BigDecimal deliveryFee(MiniOrderRequest r) {
        if ("DELIVERY".equals(r.fulfillmentType())) {
            if (r.addressId() == null) throw new BusinessException("配送订单需要地址");
            return BigDecimal.valueOf(5);
        }
        if (!"SELF_PICKUP".equals(r.fulfillmentType())) throw new BusinessException("履约方式不支持");
        return BigDecimal.ZERO;
    }


    private String snapshot(MiniOrderRequest r) {
        if (r.addressId() == null) return null;
        CustomerAddress a = addresses.list().stream().filter(x -> x.getId().equals(r.addressId())).findFirst().orElseThrow(() -> new BusinessException("地址不存在"));
        return a.getConsignee() + " " + a.getPhone() + " " + a.getProvince() + a.getCity() + a.getDistrict() + a.getDetail();
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
