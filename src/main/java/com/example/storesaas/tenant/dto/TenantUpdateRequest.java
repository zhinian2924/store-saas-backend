package com.example.storesaas.tenant.dto;

import jakarta.validation.constraints.NotBlank;

public record TenantUpdateRequest(@NotBlank(message = "租户名称不能为空") String name) {
}
