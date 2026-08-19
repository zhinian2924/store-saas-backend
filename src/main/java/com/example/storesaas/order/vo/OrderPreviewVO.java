package com.example.storesaas.order.vo;

import java.math.BigDecimal;
import java.util.List;

public record OrderPreviewVO(List<MiniOrderItemVO> items, BigDecimal totalAmount,
                             BigDecimal deliveryFee, BigDecimal payAmount) {
}
