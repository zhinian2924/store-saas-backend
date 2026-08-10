package com.example.storesaas.inventory.vo;

import com.example.storesaas.inventory.entity.InventoryFlow;

import java.time.LocalDateTime;

/**
 * 库存流水VO
 * @param id          ID
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
 * @param deleted     删除标志
 * @param tenantId    租户ID
 * @param productId   商品ID
 * @param flowType    流水类型
 * @param quantity    流水数量
 * @param beforeStock  变动前库存
 * @param afterStock  变动后库存
 * @param remark      备注
 * @param productName 商品名称
 */
public record InventoryFlowVO(
        Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
        Long tenantId, Long productId, String flowType, Integer quantity,
        Integer beforeStock, Integer afterStock, String remark, String productName) {

    public static InventoryFlowVO from(InventoryFlow flow) {
        return from(flow, null);
    }

    public static InventoryFlowVO from(InventoryFlow flow, String productName) {
        return new InventoryFlowVO(
                flow.getId(), flow.getCreatedAt(), flow.getUpdatedAt(), flow.getDeleted(),
                flow.getTenantId(), flow.getProductId(), flow.getFlowType(), flow.getQuantity(),
                flow.getBeforeStock(), flow.getAfterStock(), flow.getRemark(), productName);
    }
}
