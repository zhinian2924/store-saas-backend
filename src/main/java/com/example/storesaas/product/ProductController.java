package com.example.storesaas.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.product.dto.CategoryDTO;
import com.example.storesaas.product.dto.CategoryStatusDTO;
import com.example.storesaas.product.dto.ProductDTO;
import com.example.storesaas.product.dto.ProductStatusDTO;
import com.example.storesaas.product.vo.CategoryVO;
import com.example.storesaas.product.vo.ProductVO;
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

    @SaCheckPermission(Permissions.PRODUCT_VIEW)
    @GetMapping("/categories")
    public ApiResponse<List<CategoryVO>> categories() {
        return ApiResponse.ok(productService.categories());
    }

    @SaCheckPermission(Permissions.PRODUCT_ADD)
    @PostMapping("/categories")
    public ApiResponse<CategoryVO> createCategory(@Valid @RequestBody CategoryDTO request) {
        return ApiResponse.ok(productService.createCategory(request));
    }

    @SaCheckPermission(Permissions.PRODUCT_VIEW)
    @GetMapping("/products")
    public ApiResponse<List<ProductVO>> products() {
        return ApiResponse.ok(productService.products());
    }

    @SaCheckPermission(Permissions.PRODUCT_ADD)
    @PostMapping("/products")
    public ApiResponse<ProductVO> createProduct(@Valid @RequestBody ProductDTO request) {
        return ApiResponse.ok(productService.createProduct(request));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/products/{id}")
    public ApiResponse<ProductVO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO request) {
        return ApiResponse.ok(productService.updateProduct(id, request));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/products/{id}/status")
    public ApiResponse<ProductVO> updateProductStatus(@PathVariable Long id, @Valid @RequestBody ProductStatusDTO request) {
        return ApiResponse.ok(productService.setProductStatus(id, request.status()));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @PutMapping("/categories/{id}/status")
    public ApiResponse<CategoryVO> updateCategoryStatus(@PathVariable Long id, @Valid @RequestBody CategoryStatusDTO request) {
        return ApiResponse.ok(productService.setCategoryStatus(id, request.status()));
    }

    @SaCheckPermission(Permissions.PRODUCT_UPDATE)
    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.ok(null);
    }
}
