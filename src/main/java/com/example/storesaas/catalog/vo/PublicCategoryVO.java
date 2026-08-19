package com.example.storesaas.catalog.vo;

import com.example.storesaas.catalog.entity.ProductCategory;

import java.time.LocalDateTime;

public record PublicCategoryVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                               Long tenantId, String name, Integer sortNo, Integer status) {
    public static PublicCategoryVO from(ProductCategory category) {
        return new PublicCategoryVO(category.getId(), category.getCreatedAt(), category.getUpdatedAt(), category.getDeleted(),
                category.getTenantId(), category.getName(), category.getSortNo(), category.getStatus());
    }
}
