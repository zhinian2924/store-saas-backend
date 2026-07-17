package com.example.storesaas.tenant.vo;

import com.example.storesaas.tenant.entity.Tenant;

import java.time.LocalDateTime;

public record TenantVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                       String tenantCode, String name, Integer status) {
    public static TenantVO from(Tenant tenant) {
        return new TenantVO(tenant.getId(), tenant.getCreatedAt(), tenant.getUpdatedAt(), tenant.getDeleted(),
                tenant.getTenantCode(), tenant.getName(), tenant.getStatus());
    }
}
