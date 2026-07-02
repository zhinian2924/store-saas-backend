package com.example.storesaas.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;

@TableName("sys_user")
public class SysUser extends BaseEntity {
    private Long tenantId;
    private String username;
    private String password;
    private String nickname;
    private String accountType;
    private Integer status;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
