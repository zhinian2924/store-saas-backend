package com.example.storesaas.store.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;

@TableName("biz_store")
public class Store extends BaseEntity {
    private Long tenantId;
    private String name;
    private String address;
    private String businessHours;
    private String logoUrl;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
