package com.example.storesaas.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.product.dto.CategoryRequest;
import com.example.storesaas.product.dto.ProductRequest;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.entity.ProductCategory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @SaCheckPermission(Permissions.PRODUCT_VIEW)
    @GetMapping("/categories")
    public ApiResponse<List<ProductCategory>> categories() {
        return ApiResponse.ok(productService.categories());
    }

    @SaCheckPermission(Permissions.PRODUCT_ADD)
    @PostMapping("/categories")
    public ApiResponse<ProductCategory> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok(productService.createCategory(request));
    }

    @SaCheckPermission(Permissions.PRODUCT_VIEW)
    @GetMapping("/products")
    public ApiResponse<List<Product>> products() {
        return ApiResponse.ok(productService.products());
    }

    @SaCheckPermission(Permissions.PRODUCT_ADD)
    @PostMapping("/products")
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.createProduct(request));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/products/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok(productService.updateProduct(id, request));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/products/{id}/status")
    public ApiResponse<Product> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        return ApiResponse.ok(productService.setProductStatus(id, request.get("status")));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/categories/{id}/status")
    public ApiResponse<ProductCategory> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        return ApiResponse.ok(productService.setCategoryStatus(id, request.get("status")));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.ok(null);
    }
}
