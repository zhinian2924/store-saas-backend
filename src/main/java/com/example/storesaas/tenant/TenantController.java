package com.example.storesaas.tenant;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform/tenants")
public class TenantController {
    private final TenantMapper tenantMapper;

    public TenantController(TenantMapper tenantMapper) {
        this.tenantMapper = tenantMapper;
    }

    @SaCheckPermission("tenant:view")
    @GetMapping
    public ApiResponse<List<Tenant>> list() {
        return ApiResponse.ok(tenantMapper.selectList(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getDeleted, 0).orderByDesc(Tenant::getId)));
    }
}
