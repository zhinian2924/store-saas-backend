package com.example.storesaas.customer;

import com.example.storesaas.platform.error.BusinessException;
import com.example.storesaas.identity.security.AccountType;
import com.example.storesaas.identity.security.AuthContext;
import com.example.storesaas.identity.security.LoginUser;

/**
 * 客户上下文
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
