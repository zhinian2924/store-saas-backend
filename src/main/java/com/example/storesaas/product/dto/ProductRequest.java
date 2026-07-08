package com.example.storesaas.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
        Long categoryId,
        @NotBlank String name,
        String imageUrl,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) Integer stock,
        Integer status
) {
}
