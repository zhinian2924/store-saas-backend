package com.example.storesaas.mini.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record MiniOrderRequest(@NotEmpty List<@Valid Item> items, @NotBlank String fulfillmentType, Long addressId,
                               String remark) {
    public record Item(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {
    }
}
