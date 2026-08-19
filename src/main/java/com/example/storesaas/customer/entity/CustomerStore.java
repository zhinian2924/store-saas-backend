package com.example.storesaas.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.platform.persistence.BaseEntity;
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
