package com.example.storesaas.mini.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemDTO(@NotNull @Min(1) Integer quantity) {
}
