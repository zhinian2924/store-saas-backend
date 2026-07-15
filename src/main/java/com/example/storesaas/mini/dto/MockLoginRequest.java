package com.example.storesaas.mini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MockLoginRequest(@NotNull Long tenantId, @NotBlank String openid) {
}
