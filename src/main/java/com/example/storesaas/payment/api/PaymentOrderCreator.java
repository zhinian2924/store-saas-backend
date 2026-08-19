package com.example.storesaas.payment.api;

import java.math.BigDecimal;

public interface PaymentOrderCreator {
    void create(Long tenantId, Long orderId, BigDecimal amount, String channel);
}
