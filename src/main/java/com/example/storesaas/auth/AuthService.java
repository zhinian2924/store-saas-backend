package com.example.storesaas.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.auth.dto.LoginRequest;
import com.example.storesaas.auth.dto.LoginResponse;
import com.example.storesaas.auth.dto.RegisterTenantRequest;
import com.example.storesaas.auth.dto.SmsCodeRequest;
import com.example.storesaas.auth.dto.SmsCodeResponse;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.LoginUser;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import com.example.storesaas.tenant.TenantStatus;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import com.example.storesaas.user.entity.SysUser;
import com.example.storesaas.user.mapper.SysUserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private static final Duration SMS_CODE_TTL = Duration.ofMinutes(5);
    private static final String STORE_SMS_CODE_KEY_PREFIX = "auth:sms:store:";
    private static final String SMS_LOGIN_TYPE = "sms";
    private static final String PASSWORD_LOGIN_TYPE = "password";
    private static final List<String> STORE_ADMIN_PERMISSIONS = List.of(
            "store:view", "store:update",
            "product:view", "product:add", "product:update",
            "inventory:view", "inventory:adjust",
            "order:view", "staff:view", "statistics:view"
    );

    private final TenantMapper tenantMapper;
    private final StoreMapper storeMapper;
    private final SysUserMapper sysUserMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthService(TenantMapper tenantMapper, StoreMapper storeMapper, SysUserMapper sysUserMapper, StringRedisTemplate stringRedisTemplate) {
        this.tenantMapper = tenantMapper;
        this.storeMapper = storeMapper;
        this.sysUserMapper = sysUserMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional
    public void registerTenant(RegisterTenantRequest request) {
        Long mobileCount = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getMobile, request.mobile())
                .eq(SysUser::getDeleted, 0));
        if (mobileCount > 0) {
            throw new BusinessException("手机号已被商户账号使用");
        }

        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant();
        tenant.setTenantCode(generateTenantCode(request.storeName()));
        tenant.setName(request.storeName());
        tenant.setStatus(TenantStatus.PENDING);
        tenant.setCreatedAt(now);
        tenant.setUpdatedAt(now);
        tenant.setDeleted(0);
        tenantMapper.insert(tenant);

        Store store = new Store();
        store.setTenantId(tenant.getId());
        store.setName(request.storeName());
        store.setAddress(request.address());
        store.setBusinessHours(request.businessHours());
        store.setCreatedAt(now);
        store.setUpdatedAt(now);
        store.setDeleted(0);
        storeMapper.insert(store);

        SysUser owner = new SysUser();
        owner.setTenantId(tenant.getId());
        owner.setUsername(generateStoreOwnerUsername(tenant.getId()));
        owner.setMobile(request.mobile());
        owner.setPassword(request.password());
        owner.setNickname("店主");
        owner.setAccountType(AccountType.STORE.name());
        owner.setStatus(0);
        owner.setCreatedAt(now);
        owner.setUpdatedAt(now);
        owner.setDeleted(0);
        sysUserMapper.insert(owner);

    }

    public SmsCodeResponse sendStoreSmsCode(SmsCodeRequest request) {
        SysUser user = findStoreUserByMobile(request.mobile());
        ensureTenantCanLogin(user.getTenantId());
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        stringRedisTemplate.opsForValue().set(storeSmsCodeKey(request.mobile()), code, SMS_CODE_TTL);
        return new SmsCodeResponse(request.mobile(), (int) SMS_CODE_TTL.toSeconds(), code);
    }

    public LoginResponse login(LoginRequest request, AccountType accountType) {
        SysUser user = accountType == AccountType.STORE ? storeLogin(request) : platformLogin(request, accountType);
        if (accountType == AccountType.STORE) {
            ensureTenantCanLogin(user.getTenantId());
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        List<String> permissions = accountType == AccountType.PLATFORM
                ? List.of("tenant:view", "tenant:add", "tenant:update", "statistics:view")
                : STORE_ADMIN_PERMISSIONS;
        return doLogin(user, permissions);
    }

    private SysUser storeLogin(LoginRequest request) {
        if (!hasText(request.mobile())) {
            throw new BusinessException("请输入手机号");
        }
        SysUser user = findStoreUserByMobile(request.mobile());
        String loginType = hasText(request.loginType()) ? request.loginType() : (hasText(request.code()) ? SMS_LOGIN_TYPE : PASSWORD_LOGIN_TYPE);
        if (SMS_LOGIN_TYPE.equalsIgnoreCase(loginType)) {
            verifySmsCode(request.mobile(), request.code());
            return user;
        }
        if (!hasText(request.password()) || !request.password().equals(user.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        return user;
    }

    private void ensureTenantCanLogin(Long tenantId) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null || Integer.valueOf(1).equals(tenant.getDeleted())) {
            throw new BusinessException("门店不存在");
        }
        if (Integer.valueOf(TenantStatus.PENDING).equals(tenant.getStatus())) {
            throw new BusinessException("入驻申请待平台审核");
        }
        if (Integer.valueOf(TenantStatus.REJECTED).equals(tenant.getStatus())) {
            throw new BusinessException("入驻申请未通过审核");
        }
        if (!Integer.valueOf(TenantStatus.ACTIVE).equals(tenant.getStatus())) {
            throw new BusinessException("门店已停用");
        }
    }

    private SysUser platformLogin(LoginRequest request, AccountType accountType) {
        if (!hasText(request.username()) || !hasText(request.password())) {
            throw new BusinessException("请输入用户名和密码");
        }
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username())
                .eq(SysUser::getAccountType, accountType.name())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        if (user == null || !request.password().equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user;
    }

    private SysUser findStoreUserByMobile(String mobile) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getMobile, mobile)
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException("手机号未注册");
        }
        return user;
    }

    private void verifySmsCode(String mobile, String code) {
        if (!hasText(code)) {
            throw new BusinessException("请输入验证码");
        }
        String key = storeSmsCodeKey(mobile);
        String savedCode = stringRedisTemplate.opsForValue().get(key);
        if (savedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!savedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        stringRedisTemplate.delete(key);
    }

    private String storeSmsCodeKey(String mobile) {
        return STORE_SMS_CODE_KEY_PREFIX + mobile;
    }

    /**
     * 生成门店管理员用户名
     * @param tenantId 门店ID
     * @return 用户名
     */
    private String generateStoreOwnerUsername(Long tenantId) {
        return "store_" + tenantId;
    }

    /**
     * 生成门店编码
     * @param storeName 门店名称
     * @return 门店编码
     */
    private String generateTenantCode(String storeName) {
        String prefix = normalizeTenantCodePrefix(storeName);
        for (int i = 0; i < 10; i++) {
            String candidate = prefix + "-" + ThreadLocalRandom.current().nextInt(100000, 1000000);
            Long count = tenantMapper.selectCount(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantCode, candidate));
            if (count == 0) {
                return candidate;
            }
        }
        throw new BusinessException("门店编码生成失败，请稍后重试");
    }

    /**
     * 规范化门店编码前缀
     * @param storeName 门店名称
     * @return 规范化后的前缀
     */
    private String normalizeTenantCodePrefix(String storeName) {
        if (!hasText(storeName)) {
            return "store";
        }
        String normalized = storeName.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (!hasText(normalized)) {
            return "store";
        }
        return normalized.length() > 24 ? normalized.substring(0, 24).replaceAll("-+$", "") : normalized;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
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
