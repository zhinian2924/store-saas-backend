package com.example.storesaas.auth.dto;

public record LoginDTO(String username, String mobile, String password, String code, String loginType) {
}
