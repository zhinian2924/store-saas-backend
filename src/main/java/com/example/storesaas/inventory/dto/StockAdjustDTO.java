package com.example.storesaas.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockAdjustDTO(
        @NotNull Long productId,
        @NotBlank String flowType,
        @NotNull @Min(1) Integer quantity,
        String remark
) {
}
