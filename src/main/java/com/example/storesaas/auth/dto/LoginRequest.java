package com.example.storesaas.auth.dto;

public record LoginRequest(String username, String mobile, String password, String code, String loginType) {
}
