package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.MockLoginRequest;
import com.example.storesaas.mini.service.MiniAuthService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/api/mini/auth")
public class DevMiniAuthController {
    private final MiniAuthService service;

    public DevMiniAuthController(MiniAuthService service) {
        this.service = service;
    }

    @PostMapping("/mock-login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody MockLoginRequest request) {
        return ApiResponse.ok(service.mockLogin(request));
    }
}
