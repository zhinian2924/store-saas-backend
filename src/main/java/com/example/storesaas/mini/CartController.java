package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.CartItemRequest;
import com.example.storesaas.mini.entity.CartItem;
import com.example.storesaas.mini.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mini/cart")
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<CartItem>> list() {
        CustomerContext.current();
        return ApiResponse.ok(service.list());
    }

    @PostMapping("/{productId}")
    public ApiResponse<CartItem> add(@PathVariable Long productId, @Valid @RequestBody CartItemRequest request) {
        CustomerContext.current();
        return ApiResponse.ok(service.add(productId, request));
    }

    @PutMapping("/{productId}")
    public ApiResponse<CartItem> update(@PathVariable Long productId, @Valid @RequestBody CartItemRequest request) {
        CustomerContext.current();
        return ApiResponse.ok(service.update(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(@PathVariable Long productId) {
        CustomerContext.current();
        service.remove(productId);
        return ApiResponse.ok();
    }
}
