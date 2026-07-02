package com.example.storesaas.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;

@TableName("biz_product_category")
public class ProductCategory extends BaseEntity {
    private Long tenantId;
    private String name;
    private Integer sortNo;
    private Integer status;

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

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
