package com.example.storesaas.auth.vo;

import com.example.storesaas.user.StaffPermissions;
import com.example.storesaas.user.entity.SysUser;

import java.util.Collections;
import java.util.List;

public record AccountProfileVO(
        Long id,
        String username,
        String mobile,
        String nickname,
        String staffRole,
        List<String> permissions
) {
    public static AccountProfileVO from(SysUser user) {
        return new AccountProfileVO(
                user.getId(),
                user.getUsername(),
                user.getMobile(),
                user.getNickname(),
                user.getStaffRole(),
                "STORE".equals(user.getAccountType()) ? StaffPermissions.parse(user.getPermissions(), user.getStaffRole()) : Collections.emptyList()
        );
    }
}
