package com.example.storesaas.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName("biz_payment_order")
@Data
public class PaymentOrder extends BaseEntity {
    private Long tenantId;
    private Long orderId;
    private String payNo;
    private String channel;
    private String status;
    private BigDecimal amount;
}
