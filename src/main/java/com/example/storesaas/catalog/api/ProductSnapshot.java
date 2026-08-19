package com.example.storesaas.catalog.api;

import java.math.BigDecimal;

/**
 * 商品对外提供的只读快照，避免业务模块直接依赖商品持久化实体。
 */
public record ProductSnapshot(
        Long productId,
        Long tenantId,
        String name,
        String imageUrl,
        BigDecimal price,
        Integer stock,
        Integer status
) {
}
