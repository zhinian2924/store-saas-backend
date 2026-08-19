package com.example.storesaas.interfaces.mini;

import com.example.storesaas.customer.CustomerContext;
import com.example.storesaas.platform.web.ApiResponse;
import com.example.storesaas.payment.application.MiniPaymentService;
import com.example.storesaas.order.vo.MiniOrderVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mini/payments")
public class MiniPaymentController {
    private final MiniPaymentService service;

    public MiniPaymentController(MiniPaymentService service) {
        this.service = service;
    }

    @PostMapping("/mock/{orderId}")
    public ApiResponse<MiniOrderVO> mock(@PathVariable Long orderId) {
        CustomerContext.current();
        return ApiResponse.ok(service.mock(orderId));
    }
}
