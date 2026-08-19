package com.example.storesaas.order.domain;

import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;

import java.util.List;

public interface OrderRepository {
    void saveOrder(StoreOrder order);

    void saveItem(OrderItem item);

    void updateOrder(StoreOrder order);

    List<StoreOrder> findTenantOrders(Long tenantId);

    List<OrderItem> findTenantItems(Long tenantId, Long orderId);

    StoreOrder findCustomerOrder(Long tenantId, Long customerId, Long orderId);
}
