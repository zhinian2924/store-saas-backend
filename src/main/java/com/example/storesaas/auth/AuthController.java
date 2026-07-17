package com.example.storesaas.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.example.storesaas.auth.dto.LoginDTO;
import com.example.storesaas.auth.vo.LoginVO;
import com.example.storesaas.auth.vo.AccountProfileVO;
import com.example.storesaas.auth.dto.AccountProfileUpdateDTO;
import com.example.storesaas.auth.dto.RegisterTenantDTO;
import com.example.storesaas.auth.dto.SmsCodeDTO;
import com.example.storesaas.auth.vo.SmsCodeVO;
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
    public ApiResponse<Void> registerTenant(@Valid @RequestBody RegisterTenantDTO request) {
        authService.registerTenant(request);
        return ApiResponse.ok();
    }

    @PostMapping("/store/login")
    public ApiResponse<LoginVO> storeLogin(@Valid @RequestBody LoginDTO request) {
        return ApiResponse.ok(authService.login(request, AccountType.STORE));
    }

    @PostMapping("/store/sms-code")
    public ApiResponse<SmsCodeVO> storeSmsCode(@Valid @RequestBody SmsCodeDTO request) {
        return ApiResponse.ok(authService.sendStoreSmsCode(request));
    }

    @PostMapping("/platform/login")
    public ApiResponse<LoginVO> platformLogin(@Valid @RequestBody LoginDTO request) {
        return ApiResponse.ok(authService.login(request, AccountType.PLATFORM));
    }

    @GetMapping("/me")
    public ApiResponse<AccountProfileVO> me() {
        return ApiResponse.ok(authService.me());
    }

    @PutMapping("/me")
    public ApiResponse<AccountProfileVO> updateMe(@Valid @RequestBody AccountProfileUpdateDTO request) {
        return ApiResponse.ok(authService.updateMe(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok();
    }
}
