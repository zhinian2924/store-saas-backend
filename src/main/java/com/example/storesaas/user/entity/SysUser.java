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
    private String password;
    private String nickname;
    private String accountType;
    private Integer status;
}
