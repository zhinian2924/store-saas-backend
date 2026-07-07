package com.example.storesaas.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Data
public class SysUser extends BaseEntity {
    private Long tenantId;
    private String username;
    private String mobile;
    private String password;
    private String nickname;
    private String accountType;
    private String staffRole;// 员工角色
    private String permissions;// 权限
    private Integer status;
}
