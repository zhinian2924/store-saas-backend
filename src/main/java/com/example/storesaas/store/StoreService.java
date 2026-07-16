package com.example.storesaas.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.media.MinioStorageService;
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
    private final MinioStorageService storageService;

    public StoreService(StoreMapper storeMapper, TenantMapper tenantMapper, MinioStorageService storageService) {
        this.storeMapper = storeMapper;
        this.tenantMapper = tenantMapper;
        this.storageService = storageService;
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
        String oldLogoUrl = store.getLogoUrl();
        store.setLogoUrl(request.logoUrl());
        store.setThemeColor(request.themeColor() == null ? "#0F766E" : request.themeColor().toUpperCase());
        store.setUpdatedAt(now);
        storeMapper.updateById(store);

        Tenant tenant = tenantMapper.selectById(store.getTenantId());
        if (tenant != null && Integer.valueOf(DeleteStatus.NOT_DELETED).equals(tenant.getDeleted())) {
            tenant.setName(request.name());
            tenant.setUpdatedAt(now);
            tenantMapper.updateById(tenant);
        }
        if (!java.util.Objects.equals(oldLogoUrl, request.logoUrl())) {
            storageService.deleteUrl(oldLogoUrl);
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
