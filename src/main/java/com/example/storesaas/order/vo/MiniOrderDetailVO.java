package com.example.storesaas.order.vo;

import java.util.List;

public record MiniOrderDetailVO(MiniOrderVO order, List<MiniOrderItemVO> items) {
}
