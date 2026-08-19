package com.example.storesaas.identity.security;

import cn.dev33.satoken.stp.StpUtil;
import com.example.storesaas.platform.error.BusinessException;
import com.example.storesaas.platform.error.ResultCode;

public final class AuthContext {
    private AuthContext() {
    }

    public static LoginUser currentUser() {
        Object value = StpUtil.getSession().get("loginUser");
        if (value instanceof LoginUser loginUser) {
            return loginUser;
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED, "登录会话已失效");
    }

    public static Long tenantId() {
        Long tenantId = currentUser().tenantId();
        if (tenantId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前账号没有租户上下文");
        }
        return tenantId;
    }
}
