package com.example.storesaas.miniappconfig;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.ResultCode;
import com.example.storesaas.miniappconfig.dto.MiniappConfigRequest;
import com.example.storesaas.miniappconfig.dto.MiniappConfigResponse;
import com.example.storesaas.miniappconfig.entity.MiniappConfig;
import com.example.storesaas.miniappconfig.mapper.MiniappConfigMapper;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.AuthContext;
import com.example.storesaas.tenant.TenantStatus;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MiniappConfigService {
    private final MiniappConfigMapper configMapper;
    private final TenantMapper tenantMapper;
    private final SecretCipher secretCipher;

    public MiniappConfigService(MiniappConfigMapper configMapper, TenantMapper tenantMapper, SecretCipher secretCipher) {
        this.configMapper = configMapper;
        this.tenantMapper = tenantMapper;
        this.secretCipher = secretCipher;
    }

    public MiniappConfigResponse get(Long tenantId) {
        requirePlatform();
        requireTenant(tenantId, false);
        MiniappConfig config = findByTenant(tenantId);
        if (config == null) return new MiniappConfigResponse(tenantId, null, false, null, null, null);
        return response(config);
    }

    @Transactional
    public MiniappConfigResponse save(Long tenantId, MiniappConfigRequest request) {
        Long operatorId = requirePlatform();
        requireTenant(tenantId, false);
        String appId = request.appId().trim();
        MiniappConfig duplicate = configMapper.selectOne(new LambdaQueryWrapper<MiniappConfig>()
                .eq(MiniappConfig::getAppId, appId)
                .eq(MiniappConfig::getDeleted, DeleteStatus.NOT_DELETED)
                .ne(MiniappConfig::getTenantId, tenantId));
        if (duplicate != null) throw new BusinessException(ResultCode.CONFLICT, "该AppID已绑定其他租户");

        MiniappConfig config = findByTenant(tenantId);
        LocalDateTime now = LocalDateTime.now();
        String secret = request.appSecret() == null ? "" : request.appSecret().trim();
        if (config == null) {
            if (secret.isEmpty()) throw new BusinessException("首次配置必须填写AppSecret");
            config = new MiniappConfig();
            config.setTenantId(tenantId);
            config.setStatus(CommonStatus.ENABLED);
            config.setCreatedBy(operatorId);
            config.setCreatedAt(now);
            config.setDeleted(DeleteStatus.NOT_DELETED);
        }
        config.setAppId(appId);
        if (!secret.isEmpty()) config.setAppSecretCiphertext(secretCipher.encrypt(secret));
        config.setUpdatedBy(operatorId);
        config.setUpdatedAt(now);
        if (config.getId() == null) configMapper.insert(config); else configMapper.updateById(config);
        return response(config);
    }

    @Transactional
    public MiniappConfigResponse setStatus(Long tenantId, Integer status) {
        Long operatorId = requirePlatform();
        if (!Integer.valueOf(CommonStatus.ENABLED).equals(status)
                && !Integer.valueOf(CommonStatus.DISABLED).equals(status)) {
            throw new BusinessException("小程序配置状态不合法");
        }
        MiniappConfig config = findByTenant(tenantId);
        if (config == null) throw new BusinessException("请先配置小程序AppID和AppSecret");
        config.setStatus(status);
        config.setUpdatedBy(operatorId);
        config.setUpdatedAt(LocalDateTime.now());
        configMapper.updateById(config);
        return response(config);
    }

    public ActiveMiniapp requireActiveByAppId(String appId) {
        MiniappConfig config = activeConfig(appId);
        Tenant tenant = requireTenant(config.getTenantId(), true);
        return new ActiveMiniapp(config.getTenantId(), config.getAppId(),
                secretCipher.decrypt(config.getAppSecretCiphertext()), tenant);
    }

    public Long requireActiveTenantIdByAppId(String appId) {
        return activeConfig(appId).getTenantId();
    }

    public void requireActiveTenantAccess(Long tenantId) {
        requireTenant(tenantId, true);
        MiniappConfig config = findByTenant(tenantId);
        if (config == null || !Integer.valueOf(CommonStatus.ENABLED).equals(config.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "小程序未配置或已停用");
        }
    }

    private MiniappConfig activeConfig(String appId) {
        if (appId == null || appId.isBlank()) throw new BusinessException("缺少小程序AppID");
        MiniappConfig config = configMapper.selectOne(new LambdaQueryWrapper<MiniappConfig>()
                .eq(MiniappConfig::getAppId, appId.trim())
                .eq(MiniappConfig::getStatus, CommonStatus.ENABLED)
                .eq(MiniappConfig::getDeleted, DeleteStatus.NOT_DELETED));
        if (config == null) throw new BusinessException(ResultCode.FORBIDDEN, "小程序未配置或已停用");
        requireTenant(config.getTenantId(), true);
        return config;
    }

    private MiniappConfig findByTenant(Long tenantId) {
        return configMapper.selectOne(new LambdaQueryWrapper<MiniappConfig>()
                .eq(MiniappConfig::getTenantId, tenantId)
                .eq(MiniappConfig::getDeleted, DeleteStatus.NOT_DELETED));
    }

    private Tenant requireTenant(Long tenantId, boolean requireActive) {
        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getId, tenantId).eq(Tenant::getDeleted, DeleteStatus.NOT_DELETED));
        if (tenant == null) throw new BusinessException("租户不存在");
        if (requireActive && !Integer.valueOf(TenantStatus.ACTIVE).equals(tenant.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "租户未启用");
        }
        return tenant;
    }

    private Long requirePlatform() {
        var user = AuthContext.currentUser();
        if (user.accountType() != AccountType.PLATFORM) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅平台账号可管理小程序配置");
        }
        return user.userId();
    }

    private MiniappConfigResponse response(MiniappConfig config) {
        return new MiniappConfigResponse(config.getTenantId(), config.getAppId(),
                config.getAppSecretCiphertext() != null && !config.getAppSecretCiphertext().isBlank(),
                config.getStatus(), config.getUpdatedBy(), config.getUpdatedAt());
    }

    public record ActiveMiniapp(Long tenantId, String appId, String appSecret, Tenant tenant) {
    }
}
