package com.example.storesaas.common.constants;

public final class ProductStatus {
    public static final int OFF_SHELF = 0;
    public static final int ON_SALE = 1;
    public static final int SOLD_OUT = 2;
    public static final int STOPPED = 3;

    private ProductStatus() {
    }

    public static boolean valid(int status) {
        return status == OFF_SHELF || status == ON_SALE || status == SOLD_OUT || status == STOPPED;
    }
}
