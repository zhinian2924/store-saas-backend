package com.example.storesaas.product.dto;

import jakarta.validation.constraints.NotNull;

public record ProductStatusDTO(@NotNull(message = "商品状态不能为空") Integer status) {
}
