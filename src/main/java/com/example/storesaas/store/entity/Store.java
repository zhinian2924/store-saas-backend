package com.example.storesaas.store.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_store")
@Data
public class Store extends BaseEntity {
    private Long tenantId;
    private String name;
    private String address;
    private String businessHours;
    private String logoUrl;
}
