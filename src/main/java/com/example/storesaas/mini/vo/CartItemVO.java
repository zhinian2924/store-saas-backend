package com.example.storesaas.mini.vo;

import com.example.storesaas.mini.entity.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                         Long tenantId, Long customerId, Long productId, Integer quantity, BigDecimal price) {
    public static CartItemVO from(CartItem item) {
        return new CartItemVO(item.getId(), item.getCreatedAt(), item.getUpdatedAt(), item.getDeleted(),
                item.getTenantId(), item.getCustomerId(), item.getProductId(), item.getQuantity(), item.getPrice());
    }
}
