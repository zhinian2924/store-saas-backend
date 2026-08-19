package com.example.storesaas.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(@NotBlank String name, Integer sortNo, Integer status) {
}
