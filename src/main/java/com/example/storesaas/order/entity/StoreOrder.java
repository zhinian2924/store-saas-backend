package com.example.storesaas.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.storesaas.common.BaseEntity;
import java.math.BigDecimal;

@TableName("biz_order")
public class StoreOrder extends BaseEntity {
    private Long tenantId;
    private Long customerId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
