package com.example.storesaas.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_product_category")
@Data
public class ProductCategory extends BaseEntity {
    private Long tenantId;
    private String name;
    private Integer sortNo;
    private Integer status;
}
