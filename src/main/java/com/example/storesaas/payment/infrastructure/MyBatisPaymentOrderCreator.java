package com.example.storesaas.payment.infrastructure;

import com.example.storesaas.platform.persistence.DeleteStatus;
import com.example.storesaas.payment.domain.PaymentNumberRules;
import com.example.storesaas.payment.domain.PaymentStatus;
import com.example.storesaas.payment.api.PaymentOrderCreator;
import com.example.storesaas.payment.entity.PaymentOrder;
import com.example.storesaas.payment.mapper.PaymentOrderMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MyBatisPaymentOrderCreator implements PaymentOrderCreator {
    private final PaymentOrderMapper paymentOrderMapper;

    public MyBatisPaymentOrderCreator(PaymentOrderMapper paymentOrderMapper) {
        this.paymentOrderMapper = paymentOrderMapper;
    }

    @Override
    public void create(Long tenantId, Long orderId, BigDecimal amount, String channel) {
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setTenantId(tenantId);
        paymentOrder.setOrderId(orderId);
        paymentOrder.setPayNo(PaymentNumberRules.PREFIX + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(PaymentNumberRules.RANDOM_MIN, PaymentNumberRules.RANDOM_MAX));
        paymentOrder.setChannel(channel);
        paymentOrder.setStatus(PaymentStatus.WAITING);
        paymentOrder.setAmount(amount);
        paymentOrder.setCreatedAt(now);
        paymentOrder.setUpdatedAt(now);
        paymentOrder.setDeleted(DeleteStatus.NOT_DELETED);
        paymentOrderMapper.insert(paymentOrder);
    }
}
