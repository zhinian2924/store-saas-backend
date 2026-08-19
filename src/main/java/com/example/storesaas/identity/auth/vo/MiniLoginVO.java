package com.example.storesaas.identity.auth.vo;

public record MiniLoginVO(String tokenName, String tokenValue, Long customerId, Long tenantId,
                          String nickname, String avatarUrl) {
}
