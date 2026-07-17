package com.example.storesaas.user.vo;

import com.example.storesaas.user.StaffPermissions;
import com.example.storesaas.user.entity.SysUser;

import java.time.LocalDateTime;
import java.util.List;

public record StaffVO(
        Long id,
        String mobile,
        String nickname,
        String staffRole,
        List<String> permissions,
        Integer status,
        LocalDateTime createdAt
) {
    public static StaffVO from(SysUser user) {
        return new StaffVO(
                user.getId(),
                user.getMobile(),
                user.getNickname(),
                user.getStaffRole(),
                StaffPermissions.parse(user.getPermissions(), user.getStaffRole()),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
