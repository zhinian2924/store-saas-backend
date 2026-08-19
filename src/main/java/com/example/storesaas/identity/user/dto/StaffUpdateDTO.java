package com.example.storesaas.identity.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record StaffUpdateDTO(
        String password,
        String nickname,
        @NotBlank String staffRole,
        List<String> permissions,
        Integer status
) {
}
