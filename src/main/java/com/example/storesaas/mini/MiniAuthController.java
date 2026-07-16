package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.WechatLoginRequest;
import com.example.storesaas.mini.service.MiniAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/mini/auth")
public class MiniAuthController {
    private final MiniAuthService service;

    public MiniAuthController(MiniAuthService service) {
        this.service = service;
    }

    @PostMapping("/wechat-login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody WechatLoginRequest request) {
        return ApiResponse.ok(service.wechatLogin(request));
    }

    @GetMapping("/me")
    public ApiResponse<Object> me() {
        return ApiResponse.ok(com.example.storesaas.security.AuthContext.currentUser());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok();
    }
}
