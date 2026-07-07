package com.example.storesaas.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.AuthContext;
import com.example.storesaas.user.dto.StaffCreateRequest;
import com.example.storesaas.user.dto.StaffResponse;
import com.example.storesaas.user.dto.StaffUpdateRequest;
import com.example.storesaas.user.entity.SysUser;
import com.example.storesaas.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class StaffService {
    private final SysUserMapper sysUserMapper;

    public StaffService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public List<StaffResponse> list() {
        ensureOwner();
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getTenantId, AuthContext.tenantId())
                        .eq(SysUser::getAccountType, AccountType.STORE.name())
                        .ne(SysUser::getStaffRole, StaffRole.OWNER.name())
                        .eq(SysUser::getDeleted, DeleteStatus.NOT_DELETED)
                        .orderByDesc(SysUser::getCreatedAt))
                .stream()
                .map(StaffResponse::from)
                .toList();
    }

    @Transactional
    public StaffResponse create(StaffCreateRequest request) {
        ensureOwner();
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getMobile, request.mobile())
                .eq(SysUser::getDeleted, DeleteStatus.NOT_DELETED));
        if (count > 0) {
            throw new BusinessException("手机号已被门店账号使用");
        }

        LocalDateTime now = LocalDateTime.now();
        StaffRole role = staffRole(request.staffRole());
        SysUser user = new SysUser();
        user.setTenantId(AuthContext.tenantId());
        user.setUsername("staff_" + user.getTenantId() + "_" + ThreadLocalRandom.current().nextInt(100000, 999999));
        user.setMobile(request.mobile());
        user.setPassword(request.password());
        user.setNickname(hasText(request.nickname()) ? request.nickname().trim() : "店员");
        user.setAccountType(AccountType.STORE.name());
        user.setStaffRole(role.name());
        user.setPermissions(StaffPermissions.joinGrantable(request.permissions()));
        user.setStatus(CommonStatus.ENABLED);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setDeleted(DeleteStatus.NOT_DELETED);
        sysUserMapper.insert(user);
        return StaffResponse.from(user);
    }

    @Transactional
    public StaffResponse update(Long id, StaffUpdateRequest request) {
        ensureOwner();
        SysUser user = staff(id);
        StaffRole role = staffRole(request.staffRole());
        if (hasText(request.password())) {
            user.setPassword(request.password());
        }
        if (request.nickname() != null) {
            user.setNickname(request.nickname().trim());
        }
        user.setStaffRole(role.name());
        user.setPermissions(StaffPermissions.joinGrantable(request.permissions()));
        if (request.status() != null) {
            user.setStatus(Integer.valueOf(CommonStatus.DISABLED).equals(request.status()) ? CommonStatus.DISABLED : CommonStatus.ENABLED);
        }
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return StaffResponse.from(user);
    }

    @Transactional
    public StaffResponse setStatus(Long id, Integer status) {
        ensureOwner();
        SysUser user = staff(id);
        user.setStatus(Integer.valueOf(CommonStatus.DISABLED).equals(status) ? CommonStatus.DISABLED : CommonStatus.ENABLED);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return StaffResponse.from(user);
    }

    private SysUser staff(Long id) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getTenantId, AuthContext.tenantId())
                .eq(SysUser::getAccountType, AccountType.STORE.name())
                .eq(SysUser::getDeleted, DeleteStatus.NOT_DELETED)
                .last("limit 1"));
        if (user == null || StaffPermissions.isOwner(user.getStaffRole())) {
            throw new BusinessException("员工不存在");
        }
        return user;
    }

    private void ensureOwner() {
        if (!StaffPermissions.isOwner(AuthContext.currentUser().staffRole())) {
            throw new BusinessException("只有店主可以管理员工");
        }
    }

    private StaffRole staffRole(String role) {
        StaffRole staffRole;
        try {
            staffRole = StaffRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            throw new BusinessException("员工岗位不正确");
        }
        if (staffRole == StaffRole.OWNER) {
            throw new BusinessException("员工不能设置为店主");
        }
        return staffRole;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
