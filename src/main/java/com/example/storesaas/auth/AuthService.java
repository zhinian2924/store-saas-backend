package com.example.storesaas.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.auth.dto.LoginRequest;
import com.example.storesaas.auth.dto.LoginResponse;
import com.example.storesaas.auth.dto.RegisterTenantRequest;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.LoginUser;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import com.example.storesaas.user.entity.SysUser;
import com.example.storesaas.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    private static final List<String> STORE_ADMIN_PERMISSIONS = List.of(
            "store:view", "store:update",
            "product:view", "product:add", "product:update",
            "inventory:view", "inventory:adjust",
            "order:view", "staff:view", "statistics:view"
    );

    private final TenantMapper tenantMapper;
    private final StoreMapper storeMapper;
    private final SysUserMapper sysUserMapper;

    public AuthService(TenantMapper tenantMapper, StoreMapper storeMapper, SysUserMapper sysUserMapper) {
        this.tenantMapper = tenantMapper;
        this.storeMapper = storeMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Transactional
    public LoginResponse registerTenant(RegisterTenantRequest request) {
        Long count = tenantMapper.selectCount(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantCode, request.tenantCode()));
        if (count > 0) {
            throw new BusinessException("租户编码已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant();
        tenant.setTenantCode(request.tenantCode());
        tenant.setName(request.storeName());
        tenant.setStatus(1);
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenant.setDeleted(0);
        tenantMapper.insert(tenant);

        Store store = new Store();
        store.setTenantId(tenant.getId());
        store.setName(request.storeName());
        store.setBusinessHours("09:00-22:00");
        store.setCreatedAt(now);
        store.setUpdatedAt(now);
        store.setDeleted(0);
        storeMapper.insert(store);

        SysUser owner = new SysUser();
        owner.setTenantId(tenant.getId());
        owner.setUsername(request.username());
        owner.setPassword(request.password());
        owner.setNickname("店主");
        owner.setAccountType(AccountType.STORE.name());
        owner.setStatus(1);
        owner.setCreatedAt(now);
        owner.setUpdatedAt(now);
        owner.setDeleted(0);
        sysUserMapper.insert(owner);

        return doLogin(owner, STORE_ADMIN_PERMISSIONS);
    }

    public LoginResponse login(LoginRequest request, AccountType accountType) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username())
                .eq(SysUser::getAccountType, accountType.name())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        if (user == null || !user.getPassword().equals(request.password())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        List<String> permissions = accountType == AccountType.PLATFORM
                ? List.of("tenant:view", "tenant:add", "tenant:update", "statistics:view")
                : STORE_ADMIN_PERMISSIONS;
        return doLogin(user, permissions);
    }

    /**
     * 登录
     * @param user 用户
     * @param permissions 权限列表
     * @return 登录响应
     */
    private LoginResponse doLogin(SysUser user, List<String> permissions) {
        StpUtil.login(user.getId());
        LoginUser loginUser = new LoginUser(user.getId(), user.getTenantId(), AccountType.valueOf(user.getAccountType()), user.getUsername(), permissions);
        StpUtil.getSession().set("loginUser", loginUser);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return new LoginResponse(tokenInfo.getTokenName(), tokenInfo.getTokenValue(), user.getTenantId(), user.getUsername(), permissions);
    }
}
