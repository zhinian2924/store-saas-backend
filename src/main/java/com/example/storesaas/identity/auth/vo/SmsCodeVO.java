package com.example.storesaas.identity.auth.vo;

public record SmsCodeVO(String mobile, Integer expireSeconds, String debugCode) {
}
