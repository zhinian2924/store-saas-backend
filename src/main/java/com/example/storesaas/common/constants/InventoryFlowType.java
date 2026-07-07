package com.example.storesaas.common.constants;

/**
 * 库存流水类型
 */
public final class InventoryFlowType {

    public static final String PURCHASE_IN = "PURCHASE_IN";// 采购入库
    public static final String DAMAGE_OUT = "DAMAGE_OUT";// 损耗出库
    public static final String CHECK_GAIN = "CHECK_GAIN";// 盘点增
    public static final String CHECK_LOSS = "CHECK_LOSS";// 盘点减
    public static final String ORDER_OUT = "ORDER_OUT";// 订单出库

    private InventoryFlowType() {
    }
}
