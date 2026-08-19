package com.example.storesaas.catalog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.platform.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_product_category")
@Data
public class ProductCategory extends BaseEntity {
    private Long tenantId;
    private String name;
    private Integer sortNo;// 排序
    private Integer status;
}
