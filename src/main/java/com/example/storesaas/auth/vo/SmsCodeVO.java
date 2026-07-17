package com.example.storesaas.auth.vo;

public record SmsCodeVO(String mobile, Integer expireSeconds, String debugCode) {
}
