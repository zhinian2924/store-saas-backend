package com.example.storesaas.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.example.storesaas.auth.dto.LoginRequest;
import com.example.storesaas.auth.dto.LoginResponse;
import com.example.storesaas.auth.dto.AccountProfileResponse;
import com.example.storesaas.auth.dto.AccountProfileUpdateRequest;
import com.example.storesaas.auth.dto.RegisterTenantRequest;
import com.example.storesaas.auth.dto.SmsCodeRequest;
import com.example.storesaas.auth.dto.SmsCodeResponse;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.security.AccountType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/tenant/register")
    public ApiResponse<Void> registerTenant(@Valid @RequestBody RegisterTenantRequest request) {
        authService.registerTenant(request);
        return ApiResponse.ok();
    }

    @PostMapping("/store/login")
    public ApiResponse<LoginResponse> storeLogin(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request, AccountType.STORE));
    }

    @PostMapping("/store/sms-code")
    public ApiResponse<SmsCodeResponse> storeSmsCode(@Valid @RequestBody SmsCodeRequest request) {
        return ApiResponse.ok(authService.sendStoreSmsCode(request));
    }

    @PostMapping("/platform/login")
    public ApiResponse<LoginResponse> platformLogin(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request, AccountType.PLATFORM));
    }

    @GetMapping("/me")
    public ApiResponse<AccountProfileResponse> me() {
        return ApiResponse.ok(authService.me());
    }

    @PutMapping("/me")
    public ApiResponse<AccountProfileResponse> updateMe(@Valid @RequestBody AccountProfileUpdateRequest request) {
        return ApiResponse.ok(authService.updateMe(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok();
    }
}
