package com.example.storesaas.tenant;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.tenant.dto.TenantUpdateDTO;
import com.example.storesaas.tenant.dto.TenantStatusDTO;
import com.example.storesaas.tenant.vo.TenantVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform/tenants")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @SaCheckPermission(Permissions.TENANT_VIEW)
    @GetMapping
    public ApiResponse<List<TenantVO>> list(@RequestParam(required = false) Integer status) {
        return ApiResponse.ok(tenantService.list(status));
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody TenantUpdateDTO request) {
        tenantService.update(id, request);
        return ApiResponse.ok();
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PutMapping("/{id}/status")
    public ApiResponse<Void> setStatus(@PathVariable Long id, @Valid @RequestBody TenantStatusDTO request) {
        tenantService.setStatus(id, request.status());
        return ApiResponse.ok();
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return ApiResponse.ok();
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        tenantService.approve(id);
        return ApiResponse.ok();
    }

    @SaCheckPermission(Permissions.TENANT_UPDATE)
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id) {
        tenantService.reject(id);
        return ApiResponse.ok();
    }
}
