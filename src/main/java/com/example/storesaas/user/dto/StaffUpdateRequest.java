package com.example.storesaas.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record StaffUpdateRequest(
        String password,
        String nickname,
        @NotBlank String staffRole,
        List<String> permissions,
        Integer status
) {
}
