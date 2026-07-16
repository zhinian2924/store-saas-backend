package com.example.storesaas.miniappconfig.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("sys_miniapp_config")
@Data
public class MiniappConfig extends BaseEntity {
    private Long tenantId;
    private String appId;
    private String appSecretCiphertext;
    private Integer status;
    private Long createdBy;
    private Long updatedBy;
}
