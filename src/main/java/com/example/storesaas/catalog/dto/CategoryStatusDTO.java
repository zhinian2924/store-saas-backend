package com.example.storesaas.catalog.dto;

import jakarta.validation.constraints.NotNull;

public record CategoryStatusDTO(@NotNull(message = "分类状态不能为空") Integer status) {
}
