package com.example.storesaas.miniappconfig.vo;

import java.time.LocalDateTime;

public record MiniappConfigVO(
        Long tenantId,
        String appId,
        boolean secretConfigured,
        Integer status,
        Long updatedBy,
        LocalDateTime updatedAt
) {
}
