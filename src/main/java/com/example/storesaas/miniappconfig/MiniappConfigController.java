package com.example.storesaas.miniappconfig;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.miniappconfig.dto.MiniappConfigRequest;
import com.example.storesaas.miniappconfig.dto.MiniappConfigResponse;
import com.example.storesaas.miniappconfig.dto.MiniappConfigStatusRequest;
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
    public ApiResponse<MiniappConfigResponse> get(@PathVariable Long tenantId) {
        return ApiResponse.ok(service.get(tenantId));
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping
    public ApiResponse<MiniappConfigResponse> save(@PathVariable Long tenantId,
                                                   @Valid @RequestBody MiniappConfigRequest request) {
        return ApiResponse.ok(service.save(tenantId, request));
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping("/status")
    public ApiResponse<MiniappConfigResponse> setStatus(@PathVariable Long tenantId,
                                                        @Valid @RequestBody MiniappConfigStatusRequest request) {
        return ApiResponse.ok(service.setStatus(tenantId, request.status()));
    }
}
