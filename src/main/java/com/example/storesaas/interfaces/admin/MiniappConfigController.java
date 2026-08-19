package com.example.storesaas.interfaces.admin;

import com.example.storesaas.miniapp.MiniappConfigService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.platform.web.ApiResponse;
import com.example.storesaas.identity.api.Permissions;
import com.example.storesaas.miniapp.dto.MiniappConfigDTO;
import com.example.storesaas.miniapp.vo.MiniappConfigVO;
import com.example.storesaas.miniapp.dto.MiniappConfigStatusDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform/tenants/{tenantId}/miniapp-config")
public class MiniappConfigController {
    private final MiniappConfigService service;

    public MiniappConfigController(MiniappConfigService service) {
        this.service = service;
    }

    @SaCheckPermission(Permissions.TENANT_VIEW)
    @GetMapping
    public ApiResponse<MiniappConfigVO> get(@PathVariable Long tenantId) {
        return ApiResponse.ok(service.get(tenantId));
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping
    public ApiResponse<MiniappConfigVO> save(@PathVariable Long tenantId,
                                                   @Valid @RequestBody MiniappConfigDTO request) {
        return ApiResponse.ok(service.save(tenantId, request));
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping("/status")
    public ApiResponse<MiniappConfigVO> setStatus(@PathVariable Long tenantId,
                                                        @Valid @RequestBody MiniappConfigStatusDTO request) {
        return ApiResponse.ok(service.setStatus(tenantId, request.status()));
    }
}
