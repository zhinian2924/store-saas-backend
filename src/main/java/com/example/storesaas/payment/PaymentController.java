package com.example.storesaas.payment;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.order.vo.OrderVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @SaCheckPermission(Permissions.ORDER_UPDATE)
    @PostMapping("/mock/{orderId}")
    public ApiResponse<OrderVO> mockPay(@PathVariable Long orderId) {
        return ApiResponse.ok(OrderVO.from(paymentService.mockPay(orderId)));
    }
}
