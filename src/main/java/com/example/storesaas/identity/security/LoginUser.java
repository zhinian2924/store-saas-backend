package com.example.storesaas.identity.security;

import java.io.Serializable;
import java.util.List;

public record LoginUser(
        Long userId,
        Long tenantId,
        AccountType accountType,
        String username,
        String staffRole,
        List<String> permissions
) implements Serializable {
}
