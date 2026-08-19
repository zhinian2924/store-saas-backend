package com.example.storesaas.catalog.api;

/**
 * 商品目录的只读业务接口。
 */
public interface ProductReader {

    ProductSnapshot getTenantProduct(Long tenantId, Long productId);
}
