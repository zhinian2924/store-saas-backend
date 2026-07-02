package com.example.storesaas.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import java.math.BigDecimal;

@TableName("biz_payment_order")
public class PaymentOrder extends BaseEntity {
    private Long tenantId;
    private Long orderId;
    private String payNo;
    private String channel;
    private String status;
    private BigDecimal amount;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
