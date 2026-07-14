package com.example.storesaas.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import com.example.storesaas.tenant.dto.TenantUpdateRequest;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import com.example.storesaas.user.entity.SysUser;
import com.example.storesaas.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TenantService {
    private final TenantMapper tenantMapper;
    private final StoreMapper storeMapper;
    private final SysUserMapper sysUserMapper;

    public TenantService(TenantMapper tenantMapper, StoreMapper storeMapper, SysUserMapper sysUserMapper) {
        this.tenantMapper = tenantMapper;
        this.storeMapper = storeMapper;
        this.sysUserMapper = sysUserMapper;
    }

    public List<Tenant> list(Integer status) {
        LambdaQueryWrapper<Tenant> query = new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByDesc(Tenant::getId);
        if (status != null) {
            query.eq(Tenant::getStatus, status);
        }
        return tenantMapper.selectList(query);
    }

    @Transactional
    public void update(Long id, TenantUpdateRequest request) {
        Tenant tenant = requireTenant(id);
        String name = request.name().trim();
        tenant.setName(name);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);

        Store store = storeMapper.selectOne(new LambdaQueryWrapper<Store>()
                .eq(Store::getTenantId, id)
                .eq(Store::getDeleted, DeleteStatus.NOT_DELETED)
                .last("limit 1"));
        if (store != null) {
            store.setName(name);
            store.setUpdatedAt(tenant.getUpdatedAt());
            storeMapper.updateById(store);
        }
    }

    @Transactional
    public void setStatus(Long id, Integer status) {
        Tenant tenant = requireTenant(id);
        if (!Integer.valueOf(TenantStatus.ACTIVE).equals(status)
                && !Integer.valueOf(TenantStatus.DISABLED).equals(status)) {
            throw new BusinessException("租户状态不合法");
        }
        tenant.setStatus(status);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
    }

    @Transactional
    public void delete(Long id) {
        Tenant tenant = requireTenant(id);
        tenant.setDeleted(DeleteStatus.DELETED);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
    }

    @Transactional
    public void approve(Long id) {
        Tenant tenant = requireTenant(id);
        LocalDateTime now = LocalDateTime.now();
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setUpdatedAt(now);
        tenantMapper.updateById(tenant);

        List<SysUser> storeUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getTenantId, tenant.getId())
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getDeleted, DeleteStatus.NOT_DELETED));
        for (SysUser user : storeUsers) {
            user.setStatus(CommonStatus.ENABLED);
            user.setUpdatedAt(now);
            sysUserMapper.updateById(user);
        }
    }

    @Transactional
    public void reject(Long id) {
        Tenant tenant = requireTenant(id);
        tenant.setStatus(TenantStatus.REJECTED);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
    }

    private Tenant requireTenant(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null || Integer.valueOf(DeleteStatus.DELETED).equals(tenant.getDeleted())) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }
}
