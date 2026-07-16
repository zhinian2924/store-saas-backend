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
    private String address;// 地址
    private String businessHours;// 营业时间
    private String logoUrl;// logo图片地址
    private String themeColor;// 小程序主题色
}
