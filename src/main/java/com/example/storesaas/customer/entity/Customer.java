package com.example.storesaas.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer")
@Data
public class Customer extends BaseEntity {
    private Long tenantId;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private Integer status;
}
