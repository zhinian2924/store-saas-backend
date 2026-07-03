package com.example.storesaas.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true) // 自动继承父类的属性，避免重复代码
@TableName("biz_order")
@Data
public class StoreOrder extends BaseEntity {
    private Long tenantId;
    private Long customerId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
}
