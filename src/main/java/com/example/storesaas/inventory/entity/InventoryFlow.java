package com.example.storesaas.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_inventory_flow")
@Data
public class InventoryFlow extends BaseEntity {
    private Long tenantId;
    private Long productId;
    private String flowType;
    private Integer quantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String remark;
}
