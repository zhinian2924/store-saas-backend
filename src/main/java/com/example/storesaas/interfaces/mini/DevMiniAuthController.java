package com.example.storesaas.interfaces.mini;

import com.example.storesaas.platform.web.ApiResponse;
import com.example.storesaas.identity.auth.dto.MockLoginDTO;
import com.example.storesaas.identity.auth.MiniAuthService;
import com.example.storesaas.identity.auth.vo.MiniLoginVO;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/mini/auth")
public class DevMiniAuthController {
    private final MiniAuthService service;

    public DevMiniAuthController(MiniAuthService service) {
        this.service = service;
    }

    @PostMapping("/mock-login")
    public ApiResponse<MiniLoginVO> login(@Valid @RequestBody MockLoginDTO request) {
        return ApiResponse.ok(service.mockLogin(request));
    }
}
