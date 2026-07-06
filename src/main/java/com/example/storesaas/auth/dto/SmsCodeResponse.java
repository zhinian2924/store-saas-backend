package com.example.storesaas.auth.dto;

public record SmsCodeResponse(String mobile, Integer expireSeconds, String debugCode) {
}
