package com.example.storesaas.tenant.dto;

import jakarta.validation.constraints.NotNull;

public record TenantStatusDTO(@NotNull(message = "租户状态不能为空") Integer status) {
}
