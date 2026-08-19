package com.example.storesaas.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountProfileUpdateDTO(
        @NotBlank String nickname,
        String password
) {
}
