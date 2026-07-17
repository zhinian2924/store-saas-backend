package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.CartItemDTO;
import com.example.storesaas.mini.service.CartService;
import com.example.storesaas.mini.vo.CartItemVO;
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
    public ApiResponse<List<CartItemVO>> list() {
        CustomerContext.current();
        return ApiResponse.ok(service.list());
    }

    @PostMapping("/{productId}")
    public ApiResponse<CartItemVO> add(@PathVariable Long productId, @Valid @RequestBody CartItemDTO request) {
        CustomerContext.current();
        return ApiResponse.ok(service.add(productId, request));
    }

    @PutMapping("/{productId}")
    public ApiResponse<CartItemVO> update(@PathVariable Long productId, @Valid @RequestBody CartItemDTO request) {
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
