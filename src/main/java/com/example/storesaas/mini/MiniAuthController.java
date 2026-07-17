package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.WechatLoginDTO;
import com.example.storesaas.mini.service.MiniAuthService;
import com.example.storesaas.mini.vo.MiniCustomerVO;
import com.example.storesaas.mini.vo.MiniLoginVO;
import com.example.storesaas.security.AuthContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/api/mini/auth")
public class MiniAuthController {
    private final MiniAuthService service;

    public MiniAuthController(MiniAuthService service) {
        this.service = service;
    }

    @PostMapping("/wechat-login")
    public ApiResponse<MiniLoginVO> login(@Valid @RequestBody WechatLoginDTO request) {
        return ApiResponse.ok(service.wechatLogin(request));
    }

    @GetMapping("/me")
    public ApiResponse<MiniCustomerVO> me() {
        return ApiResponse.ok(MiniCustomerVO.from(AuthContext.currentUser()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok();
    }
}
