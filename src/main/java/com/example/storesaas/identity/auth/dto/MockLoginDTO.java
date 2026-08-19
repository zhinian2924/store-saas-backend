package com.example.storesaas.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MockLoginDTO(@NotNull Long tenantId, @NotBlank String openid) {
}
