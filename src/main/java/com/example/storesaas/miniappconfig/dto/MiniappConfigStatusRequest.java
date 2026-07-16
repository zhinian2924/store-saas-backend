package com.example.storesaas.miniappconfig.dto;

import jakarta.validation.constraints.NotNull;

public record MiniappConfigStatusRequest(@NotNull(message = "请选择配置状态") Integer status) {
}
