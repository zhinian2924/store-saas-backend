package com.example.storesaas.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
@Data
public class Tenant extends BaseEntity {
    private String tenantCode;
    private String name;
    private Integer status;
}
