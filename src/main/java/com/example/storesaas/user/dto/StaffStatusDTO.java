package com.example.storesaas.user.dto;

import jakarta.validation.constraints.NotNull;

public record StaffStatusDTO(@NotNull(message = "员工状态不能为空") Integer status) {
}
