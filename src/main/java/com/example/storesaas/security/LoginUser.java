package com.example.storesaas.security;

import java.io.Serializable;
import java.util.List;

public record LoginUser(
        Long userId,
        Long tenantId,
        AccountType accountType,
        String username,
        List<String> permissions
) implements Serializable {
}
