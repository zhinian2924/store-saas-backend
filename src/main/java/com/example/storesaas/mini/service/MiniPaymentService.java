package com.example.storesaas.mini.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.mini.CustomerContext;
import com.example.storesaas.order.entity.StoreOrder;
import com.example.storesaas.order.mapper.StoreOrderMapper;
import com.example.storesaas.mini.vo.MiniOrderVO;
import com.example.storesaas.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class MiniPaymentService {
    private final StoreOrderMapper orders;
    private final PaymentService payments;

    public MiniPaymentService(StoreOrderMapper o, PaymentService p) {
        orders = o;
        payments = p;
    }

    public MiniOrderVO mock(Long id) {
        StoreOrder o = orders.selectOne(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getTenantId, CustomerContext.tenantId()).eq(StoreOrder::getCustomerId, CustomerContext.customerId()).eq(StoreOrder::getId, id).eq(StoreOrder::getDeleted, DeleteStatus.NOT_DELETED));
        if (o == null) throw new BusinessException("订单不存在");
        return MiniOrderVO.from(payments.mockPay(id));
    }
}
