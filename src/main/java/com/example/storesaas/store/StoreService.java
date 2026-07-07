package com.example.storesaas.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.security.AuthContext;
import com.example.storesaas.store.dto.StoreProfileRequest;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StoreService {
    private final StoreMapper storeMapper;
    private final TenantMapper tenantMapper;

    public StoreService(StoreMapper storeMapper, TenantMapper tenantMapper) {
        this.storeMapper = storeMapper;
        this.tenantMapper = tenantMapper;
    }

    public Store profile() {
        return currentStore();
    }

    @Transactional
    public Store updateProfile(StoreProfileRequest request) {
        Store store = currentStore();
        LocalDateTime now = LocalDateTime.now();
        store.setName(request.name());
        store.setAddress(request.address());
        store.setBusinessHours(request.businessHours());
        store.setLogoUrl(request.logoUrl());
        store.setUpdatedAt(now);
        storeMapper.updateById(store);

        Tenant tenant = tenantMapper.selectById(store.getTenantId());
        if (tenant != null && Integer.valueOf(DeleteStatus.NOT_DELETED).equals(tenant.getDeleted())) {
            tenant.setName(request.name());
            tenant.setUpdatedAt(now);
            tenantMapper.updateById(tenant);
        }
        return store;
    }

    private Store currentStore() {
        Store store = storeMapper.selectOne(new LambdaQueryWrapper<Store>()
                .eq(Store::getTenantId, AuthContext.tenantId())
                .eq(Store::getDeleted, DeleteStatus.NOT_DELETED)
                .last("limit 1"));
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        return store;
    }
}
