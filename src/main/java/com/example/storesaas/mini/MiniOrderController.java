package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.MiniOrderDTO;
import com.example.storesaas.mini.service.MiniOrderService;
import com.example.storesaas.mini.vo.MiniOrderDetailVO;
import com.example.storesaas.mini.vo.MiniOrderVO;
import com.example.storesaas.mini.vo.OrderPreviewVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mini/orders")
public class MiniOrderController {
    private final MiniOrderService service;

    public MiniOrderController(MiniOrderService service) {
        this.service = service;
    }

    @PostMapping("/preview")
    public ApiResponse<OrderPreviewVO> preview(@Valid @RequestBody MiniOrderDTO r) {
        CustomerContext.current();
        return ApiResponse.ok(service.preview(r));
    }

    @PostMapping
    public ApiResponse<MiniOrderVO> create(@Valid @RequestBody MiniOrderDTO r) {
        CustomerContext.current();
        return ApiResponse.ok(service.create(r));
    }

    @GetMapping
    public ApiResponse<List<MiniOrderVO>> list() {
        CustomerContext.current();
        return ApiResponse.ok(service.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<MiniOrderDetailVO> detail(@PathVariable Long id) {
        CustomerContext.current();
        return ApiResponse.ok(service.detail(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<MiniOrderVO> cancel(@PathVariable Long id) {
        CustomerContext.current();
        return ApiResponse.ok(service.cancel(id));
    }
}
