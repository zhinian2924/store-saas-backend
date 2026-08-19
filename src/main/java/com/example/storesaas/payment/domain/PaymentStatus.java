package com.example.storesaas.payment.domain;

/**
 * 支付状态
 */
public final class PaymentStatus {
    public static final String WAITING = "WAITING";// 待支付
    public static final String SUCCESS = "SUCCESS";// 支付成功

    private PaymentStatus() {
    }
}
