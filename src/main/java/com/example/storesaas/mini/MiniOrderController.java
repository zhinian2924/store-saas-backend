package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.MiniOrderRequest;
import com.example.storesaas.mini.entity.CustomerAddress;
import com.example.storesaas.mini.service.MiniOrderService;
import com.example.storesaas.order.entity.StoreOrder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mini/orders")
public class MiniOrderController {
    private final MiniOrderService service;
    public MiniOrderController(MiniOrderService service){this.service=service;}
    @PostMapping("/preview") public ApiResponse<Map<String,Object>> preview(@Valid @RequestBody MiniOrderRequest r){CustomerContext.current();return ApiResponse.ok(service.preview(r));}
    @PostMapping public ApiResponse<StoreOrder> create(@Valid @RequestBody MiniOrderRequest r){CustomerContext.current();return ApiResponse.ok(service.create(r));}
    @GetMapping public ApiResponse<List<StoreOrder>> list(){CustomerContext.current();return ApiResponse.ok(service.list());}
    @GetMapping("/{id}") public ApiResponse<Map<String,Object>> detail(@PathVariable Long id){CustomerContext.current();return ApiResponse.ok(service.detail(id));}
    @PostMapping("/{id}/cancel") public ApiResponse<StoreOrder> cancel(@PathVariable Long id){CustomerContext.current();return ApiResponse.ok(service.cancel(id));}
}
