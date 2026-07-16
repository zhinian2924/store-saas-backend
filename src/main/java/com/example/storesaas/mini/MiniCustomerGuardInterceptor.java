package com.example.storesaas.mini;

import com.example.storesaas.miniappconfig.MiniappConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MiniCustomerGuardInterceptor implements HandlerInterceptor {
    private final MiniappConfigService configService;

    public MiniCustomerGuardInterceptor(MiniappConfigService configService) {
        this.configService = configService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var customer = CustomerContext.current();
        configService.requireActiveTenantAccess(customer.tenantId());
        return true;
    }
}
