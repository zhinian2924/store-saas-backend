package com.example.storesaas.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTenantRequest(
        @NotBlank String tenantCode,
        @NotBlank String storeName,
        @NotBlank String username,
        @NotBlank String password
) {
}
