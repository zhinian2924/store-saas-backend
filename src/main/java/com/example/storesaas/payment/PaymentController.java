package com.example.storesaas.payment;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.order.entity.StoreOrder;
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

    @PostMapping("/mock/{orderId}")
    public ApiResponse<StoreOrder> mockPay(@PathVariable Long orderId) {
        return ApiResponse.ok(paymentService.mockPay(orderId));
    }
}
