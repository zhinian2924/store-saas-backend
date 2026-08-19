package com.example.storesaas.inventory.api;

import java.util.List;

/**
 * 库存预占边界。当前实现迁移完成前由库存模块内部逐步接入。
 */
public interface InventoryReservation {

    void reserve(Long tenantId, Long orderId, List<ReservationItem> items);

    void commit(Long tenantId, Long orderId);

    void release(Long tenantId, Long orderId);

    record ReservationItem(Long productId, Integer quantity) {
    }
}
