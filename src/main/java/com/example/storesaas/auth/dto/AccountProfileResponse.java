package com.example.storesaas.auth.dto;

import com.example.storesaas.user.StaffPermissions;
import com.example.storesaas.user.entity.SysUser;

import java.util.Collections;
import java.util.List;

public record AccountProfileResponse(
        Long id,
        String username,
        String mobile,
        String nickname,
        String staffRole,
        List<String> permissions
) {
    public static AccountProfileResponse from(SysUser user) {
        return new AccountProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getMobile(),
                user.getNickname(),
                user.getStaffRole(),
                "STORE".equals(user.getAccountType()) ? StaffPermissions.parse(user.getPermissions(), user.getStaffRole()) : Collections.emptyList()
        );
    }
}
