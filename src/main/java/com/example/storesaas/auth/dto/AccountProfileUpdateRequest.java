package com.example.storesaas.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountProfileUpdateRequest(
        @NotBlank String nickname,
        String password
) {
}
