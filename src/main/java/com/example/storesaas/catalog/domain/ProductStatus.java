package com.example.storesaas.catalog.domain;

public final class ProductStatus {
    public static final int OFF_SHELF = 0;// 下架
    public static final int ON_SALE = 1;// 上架
    public static final int SOLD_OUT = 2;// 卖完
    public static final int STOPPED = 3;// 停售

    private ProductStatus() {
    }

    public static boolean valid(int status) {
        return status == OFF_SHELF || status == ON_SALE || status == SOLD_OUT || status == STOPPED;
    }
}
