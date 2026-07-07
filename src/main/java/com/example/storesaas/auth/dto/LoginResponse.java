package com.example.storesaas.auth.dto;

import java.util.List;

public record LoginResponse(String tokenName, String tokenValue, Long tenantId, String username,
                            List<String> permissions) {
}
