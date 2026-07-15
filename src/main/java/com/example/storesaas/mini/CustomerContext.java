package com.example.storesaas.mini;

import com.example.storesaas.common.BusinessException;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.AuthContext;
import com.example.storesaas.security.LoginUser;

/**
 * Central guard for every mini-program endpoint.
 */
public final class CustomerContext {
    private CustomerContext() {
    }

    public static LoginUser current() {
        LoginUser user = AuthContext.currentUser();
        if (user.accountType() != AccountType.CUSTOMER || user.userId() == null || user.tenantId() == null) {
            throw new BusinessException("仅消费者可访问");
        }
        return user;
    }

    public static Long customerId() {
        return current().userId();
    }

    public static Long tenantId() {
        return current().tenantId();
    }
}
