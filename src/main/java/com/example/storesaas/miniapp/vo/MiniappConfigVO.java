package com.example.storesaas.miniapp.vo;

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
