package com.example.storesaas.mini.vo;

import com.example.storesaas.order.entity.StoreOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MiniOrderVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                          Long tenantId, Long customerId, String orderNo, String status,
                          BigDecimal totalAmount, String fulfillmentType, String addressSnapshot,
                          String remark, BigDecimal deliveryFee, String source) {
    public static MiniOrderVO from(StoreOrder order) {
        return new MiniOrderVO(order.getId(), order.getCreatedAt(), order.getUpdatedAt(), order.getDeleted(),
                order.getTenantId(), order.getCustomerId(), order.getOrderNo(), order.getStatus(),
                order.getTotalAmount(), order.getFulfillmentType(), order.getAddressSnapshot(),
                order.getRemark(), order.getDeliveryFee(), order.getSource());
    }
}
