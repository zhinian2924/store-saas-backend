package com.example.storesaas.identity.auth.vo;

import java.util.List;

public record LoginVO(String tokenName, String tokenValue, Long tenantId, String username,
                            List<String> permissions) {
}
