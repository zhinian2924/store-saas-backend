package com.example.storesaas.common.constants;

public final class ApiRoutes {
    public static final String AUTH = "/api/auth";
    public static final String MINI_AUTH = "/api/mini/auth/**";
    public static final String MINI_PUBLIC = "/api/mini/public/**";
    public static final String STORE = "/api/store";
    public static final String INVENTORY = "/api/store/inventory";
    public static final String ORDERS = "/api/store/orders";
    public static final String PAYMENTS = "/api/store/payments";
    public static final String PLATFORM_TENANTS = "/api/platform/tenants";

    private ApiRoutes() {
    }
}
