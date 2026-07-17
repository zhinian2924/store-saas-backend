package com.example.storesaas.product.vo;

import com.example.storesaas.product.entity.ProductCategory;

import java.time.LocalDateTime;

public record CategoryVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                         Long tenantId, String name, Integer sortNo, Integer status) {
    public static CategoryVO from(ProductCategory category) {
        return new CategoryVO(category.getId(), category.getCreatedAt(), category.getUpdatedAt(), category.getDeleted(),
                category.getTenantId(), category.getName(), category.getSortNo(), category.getStatus());
    }
}
