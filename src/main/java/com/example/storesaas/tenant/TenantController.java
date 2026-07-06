package com.example.storesaas.tenant;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import com.example.storesaas.user.entity.SysUser;
import com.example.storesaas.user.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/platform/tenants")
public class TenantController {
    private final TenantMapper tenantMapper;
    private final SysUserMapper sysUserMapper;

    public TenantController(TenantMapper tenantMapper, SysUserMapper sysUserMapper) {
        this.tenantMapper = tenantMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @SaCheckPermission("tenant:view")
    @GetMapping
    public ApiResponse<List<Tenant>> list(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Tenant> query = new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getDeleted, 0)
                .orderByDesc(Tenant::getId);
        if (status != null) {
            query.eq(Tenant::getStatus, status);
        }
        return ApiResponse.ok(tenantMapper.selectList(query));
    }

    @SaCheckPermission("tenant:update")
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        Tenant tenant = requireTenant(id);
        LocalDateTime now = LocalDateTime.now();
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setUpdatedAt(now);
        tenantMapper.updateById(tenant);

        List<SysUser> storeUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getTenantId, tenant.getId())
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getDeleted, 0));
        for (SysUser user : storeUsers) {
            user.setStatus(1);
            user.setUpdatedAt(now);
            sysUserMapper.updateById(user);
        }
        return ApiResponse.ok();
    }

    @SaCheckPermission("tenant:update")
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id) {
        Tenant tenant = requireTenant(id);
        tenant.setStatus(TenantStatus.REJECTED);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
        return ApiResponse.ok();
    }

    private Tenant requireTenant(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null || Integer.valueOf(1).equals(tenant.getDeleted())) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }
}
