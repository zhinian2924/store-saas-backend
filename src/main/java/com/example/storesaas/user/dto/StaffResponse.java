package com.example.storesaas.user.dto;

import com.example.storesaas.user.StaffPermissions;
import com.example.storesaas.user.entity.SysUser;

import java.time.LocalDateTime;
import java.util.List;

public record StaffResponse(
        Long id,
        String mobile,
        String nickname,
        String staffRole,
        List<String> permissions,
        Integer status,
        LocalDateTime createdAt
) {
    public static StaffResponse from(SysUser user) {
        return new StaffResponse(
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
