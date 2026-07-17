package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.service.MiniPaymentService;
import com.example.storesaas.mini.vo.MiniOrderVO;
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
