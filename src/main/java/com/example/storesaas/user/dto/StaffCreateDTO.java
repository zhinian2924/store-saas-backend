package com.example.storesaas.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record StaffCreateDTO(
        @NotBlank String mobile,
        @NotBlank String password,
        String nickname,
        @NotBlank String staffRole,
        List<String> permissions
) {
}
