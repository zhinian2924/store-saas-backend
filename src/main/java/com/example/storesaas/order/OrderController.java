package com.example.storesaas.order;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.order.dto.CreateOrderRequest;
import com.example.storesaas.order.entity.OrderItem;
import com.example.storesaas.order.entity.StoreOrder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @SaCheckPermission(Permissions.ORDER_UPDATE)
    @PostMapping
    public ApiResponse<StoreOrder> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.create(request));
    }

    @SaCheckPermission(Permissions.ORDER_VIEW)
    @GetMapping
    public ApiResponse<List<StoreOrder>> list() {
        return ApiResponse.ok(orderService.list());
    }

    @SaCheckPermission(Permissions.ORDER_VIEW)
    @GetMapping("/{orderId}/items")
    public ApiResponse<List<OrderItem>> items(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.items(orderId));
    }
}
