package com.example.storesaas.product.vo;

import com.example.storesaas.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                        Long tenantId, Long categoryId, String name, String imageUrl,
                        BigDecimal price, Integer stock, Integer status) {
    public static ProductVO from(Product product) {
        return new ProductVO(product.getId(), product.getCreatedAt(), product.getUpdatedAt(), product.getDeleted(),
                product.getTenantId(), product.getCategoryId(), product.getName(), product.getImageUrl(),
                product.getPrice(), product.getStock(), product.getStatus());
    }
}
