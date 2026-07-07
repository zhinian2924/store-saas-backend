package com.example.storesaas.common.constants;

public final class Permissions {
    // 门店权限
    public static final String STORE_VIEW = "store:view";
    public static final String STORE_UPDATE = "store:update";

    public static final String PRODUCT_VIEW = "product:view";
    public static final String PRODUCT_ADD = "product:add";
    public static final String PRODUCT_UPDATE = "product:update";

    public static final String INVENTORY_VIEW = "inventory:view";
    public static final String INVENTORY_ADJUST = "inventory:adjust";

    public static final String ORDER_VIEW = "order:view";

    public static final String STAFF_VIEW = "staff:view";

    public static final String STATISTICS_VIEW = "statistics:view";

    // 租户权限
    public static final String TENANT_VIEW = "tenant:view";
    public static final String TENANT_ADD = "tenant:add";
    public static final String TENANT_UPDATE = "tenant:update";

    private Permissions() {
    }
}
