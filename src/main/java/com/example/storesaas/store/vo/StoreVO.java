package com.example.storesaas.store.vo;

import com.example.storesaas.store.entity.Store;

import java.time.LocalDateTime;

public record StoreVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                      Long tenantId, String name, String address, String businessHours,
                      String logoUrl, String themeColor) {
    public static StoreVO from(Store store) {
        return new StoreVO(store.getId(), store.getCreatedAt(), store.getUpdatedAt(), store.getDeleted(),
                store.getTenantId(), store.getName(), store.getAddress(), store.getBusinessHours(),
                store.getLogoUrl(), store.getThemeColor());
    }
}
