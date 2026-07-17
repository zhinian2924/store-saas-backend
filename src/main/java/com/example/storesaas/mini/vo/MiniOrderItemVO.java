package com.example.storesaas.mini.vo;

import com.example.storesaas.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MiniOrderItemVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                              Long tenantId, Long orderId, Long productId, String productName,
                              String imageUrl, BigDecimal price, Integer quantity, BigDecimal amount) {
    public static MiniOrderItemVO from(OrderItem item) {
        return new MiniOrderItemVO(item.getId(), item.getCreatedAt(), item.getUpdatedAt(), item.getDeleted(),
                item.getTenantId(), item.getOrderId(), item.getProductId(), item.getProductName(),
                item.getImageUrl(), item.getPrice(), item.getQuantity(), item.getAmount());
    }
}
