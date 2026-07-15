package com.example.storesaas.mini.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(@NotNull @Min(1) Integer quantity) {}
