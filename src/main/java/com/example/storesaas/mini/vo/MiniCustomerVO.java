package com.example.storesaas.mini.vo;

import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.LoginUser;

import java.util.List;

public record MiniCustomerVO(Long userId, Long tenantId, AccountType accountType, String username,
                             String staffRole, List<String> permissions) {
    public static MiniCustomerVO from(LoginUser user) {
        return new MiniCustomerVO(user.userId(), user.tenantId(), user.accountType(), user.username(),
                user.staffRole(), user.permissions());
    }
}
