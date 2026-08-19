package com.example.storesaas.catalog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.platform.persistence.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_product")
@Data
public class Product extends BaseEntity {
    private Long tenantId;
    private Long categoryId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
}
