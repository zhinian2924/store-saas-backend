package com.example.storesaas.interfaces.mini;

import com.example.storesaas.customer.CustomerContext;

import com.example.storesaas.miniapp.MiniappConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器，用于租户访问控制
 */
@Component
public class MiniCustomerGuardInterceptor implements HandlerInterceptor {
    private final MiniappConfigService configService;

    public MiniCustomerGuardInterceptor(MiniappConfigService configService) {
        this.configService = configService;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        var customer = CustomerContext.current();
        configService.requireActiveTenantAccess(customer.tenantId());
        return true;
    }
}
