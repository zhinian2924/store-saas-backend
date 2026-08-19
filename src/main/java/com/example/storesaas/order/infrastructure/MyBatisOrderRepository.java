package com.example.storesaas.order.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.order.domain.OrderRepository;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.mapper.OrderItemMapper;
import com.example.storesaas.order.mapper.StoreOrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisOrderRepository implements OrderRepository {
    private final StoreOrderMapper orderMapper;
    private final OrderItemMapper itemMapper;

    public MyBatisOrderRepository(StoreOrderMapper orderMapper, OrderItemMapper itemMapper) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public void saveOrder(StoreOrder order) {
        orderMapper.insert(order);
    }

    @Override
    public void saveItem(OrderItem item) {
        itemMapper.insert(item);
    }

    @Override
    public void updateOrder(StoreOrder order) {
        orderMapper.updateById(order);
    }

    @Override
    public List<StoreOrder> findTenantOrders(Long tenantId) {
        return orderMapper.selectList(new LambdaQueryWrapper<StoreOrder>()
                .eq(StoreOrder::getTenantId, tenantId)
                .eq(StoreOrder::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByDesc(StoreOrder::getId));
    }

    @Override
    public List<OrderItem> findTenantItems(Long tenantId, Long orderId) {
        return itemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getTenantId, tenantId)
                .eq(OrderItem::getOrderId, orderId)
                .eq(OrderItem::getDeleted, DeleteStatus.NOT_DELETED));
    }

    @Override
    public StoreOrder findCustomerOrder(Long tenantId, Long customerId, Long orderId) {
        return orderMapper.selectOne(new LambdaQueryWrapper<StoreOrder>()
                .eq(StoreOrder::getTenantId, tenantId)
                .eq(StoreOrder::getCustomerId, customerId)
                .eq(StoreOrder::getId, orderId)
                .eq(StoreOrder::getDeleted, DeleteStatus.NOT_DELETED));
    }
}
