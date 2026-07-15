package com.example.storesaas.mini.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer_store")
@Data
public class CustomerStore extends BaseEntity {
    private Long customerId;
    private Long tenantId;
    private Long storeId;
}
