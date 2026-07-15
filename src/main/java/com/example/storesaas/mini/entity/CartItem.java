package com.example.storesaas.mini.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_cart_item")
@Data
public class CartItem extends BaseEntity {
    private Long tenantId;
    private Long customerId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
