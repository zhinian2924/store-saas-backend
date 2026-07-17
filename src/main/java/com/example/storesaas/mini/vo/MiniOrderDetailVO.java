package com.example.storesaas.mini.vo;

import java.util.List;

public record MiniOrderDetailVO(MiniOrderVO order, List<MiniOrderItemVO> items) {
}
