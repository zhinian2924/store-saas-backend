package com.example.storesaas.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.product.dto.CategoryRequest;
import com.example.storesaas.product.dto.ProductRequest;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.entity.ProductCategory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @SaCheckPermission("product:view")
    @GetMapping("/categories")
    public ApiResponse<List<ProductCategory>> categories() {
        return ApiResponse.ok(productService.categories());
    }

    @SaCheckPermission("product:add")
    @PostMapping("/categories")
    public ApiResponse<ProductCategory> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok(productService.createCategory(request));
    }

    @SaCheckPermission("product:view")
    @GetMapping("/products")
    public ApiResponse<List<Product>> products() {
        return ApiResponse.ok(productService.products());
    }

    @SaCheckPermission("product:add")
    @PostMapping("/products")
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.createProduct(request));
    }
}
