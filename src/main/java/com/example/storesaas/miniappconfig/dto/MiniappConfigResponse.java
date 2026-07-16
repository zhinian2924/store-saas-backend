package com.example.storesaas.miniappconfig.dto;

import java.time.LocalDateTime;

public record MiniappConfigResponse(
        Long tenantId,
        String appId,
        boolean secretConfigured,
        Integer status,
        Long updatedBy,
        LocalDateTime updatedAt
) {
}
