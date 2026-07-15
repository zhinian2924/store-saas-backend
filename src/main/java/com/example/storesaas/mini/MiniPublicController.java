package com.example.storesaas.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.ProductStatus;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.entity.ProductCategory;
import com.example.storesaas.product.mapper.ProductCategoryMapper;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mini/public")
public class MiniPublicController {
 private final StoreMapper stores; private final ProductCategoryMapper categories; private final ProductMapper products;
 public MiniPublicController(StoreMapper s,ProductCategoryMapper c,ProductMapper p){stores=s;categories=c;products=p;}
 @GetMapping("/store") public ApiResponse<Store> store(@RequestParam @NotNull Long tenantId){return ApiResponse.ok(stores.selectOne(new LambdaQueryWrapper<Store>().eq(Store::getTenantId,tenantId).eq(Store::getDeleted,0).last("limit 1")));}
 @GetMapping("/categories") public ApiResponse<List<ProductCategory>> categories(@RequestParam Long tenantId){return ApiResponse.ok(categories.selectList(new LambdaQueryWrapper<ProductCategory>().eq(ProductCategory::getTenantId,tenantId).eq(ProductCategory::getStatus,1).eq(ProductCategory::getDeleted,0).orderByAsc(ProductCategory::getSortNo)));}
 @GetMapping("/products") public ApiResponse<List<Product>> products(@RequestParam Long tenantId,@RequestParam(required=false) Long categoryId){var q=new LambdaQueryWrapper<Product>().eq(Product::getTenantId,tenantId).eq(Product::getStatus,ProductStatus.ON_SALE).gt(Product::getStock,0).eq(Product::getDeleted,0);if(categoryId!=null)q.eq(Product::getCategoryId,categoryId);return ApiResponse.ok(products.selectList(q));}
}
