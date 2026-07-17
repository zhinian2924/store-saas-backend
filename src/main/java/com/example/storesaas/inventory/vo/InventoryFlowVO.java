package com.example.storesaas.inventory.vo;

import com.example.storesaas.inventory.entity.InventoryFlow;

import java.time.LocalDateTime;

public record InventoryFlowVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                              Long tenantId, Long productId, String flowType, Integer quantity,
                              Integer beforeStock, Integer afterStock, String remark) {
    public static InventoryFlowVO from(InventoryFlow flow) {
        return new InventoryFlowVO(flow.getId(), flow.getCreatedAt(), flow.getUpdatedAt(), flow.getDeleted(),
                flow.getTenantId(), flow.getProductId(), flow.getFlowType(), flow.getQuantity(),
                flow.getBeforeStock(), flow.getAfterStock(), flow.getRemark());
    }
}
