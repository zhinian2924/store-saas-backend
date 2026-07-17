package com.example.storesaas.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.ProductStatus;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.entity.ProductCategory;
import com.example.storesaas.product.mapper.ProductCategoryMapper;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.mini.vo.PublicStoreVO;
import com.example.storesaas.mini.vo.PublicCategoryVO;
import com.example.storesaas.mini.vo.PublicProductVO;
import com.example.storesaas.miniappconfig.MiniappConfigService;
import com.example.storesaas.store.entity.Store;
import com.example.storesaas.store.mapper.StoreMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mini/public")
public class MiniPublicController {
    private final StoreMapper stores;
    private final ProductCategoryMapper categories;
    private final ProductMapper products;
    private final MiniappConfigService configs;

    public MiniPublicController(StoreMapper s, ProductCategoryMapper c, ProductMapper p, MiniappConfigService configs) {
        stores = s;
        categories = c;
        products = p;
        this.configs = configs;
    }

    @GetMapping("/store")
    public ApiResponse<PublicStoreVO> store(@RequestParam String appId) {
        Long tenantId = tenantId(appId);
        Store store = stores.selectOne(new LambdaQueryWrapper<Store>().eq(Store::getTenantId, tenantId)
                .eq(Store::getDeleted, DeleteStatus.NOT_DELETED).last("limit 1"));
        if (store == null) return ApiResponse.ok(null);
        return ApiResponse.ok(new PublicStoreVO(store.getName(), store.getLogoUrl(),
                store.getThemeColor(), store.getBusinessHours()));
    }

    @GetMapping("/categories")
    public ApiResponse<List<PublicCategoryVO>> categories(@RequestParam String appId) {
        Long tenantId = tenantId(appId);
        return ApiResponse.ok(categories.selectList(new LambdaQueryWrapper<ProductCategory>().eq(ProductCategory::getTenantId, tenantId).eq(ProductCategory::getStatus, 1).eq(ProductCategory::getDeleted, 0).orderByAsc(ProductCategory::getSortNo))
                .stream().map(PublicCategoryVO::from).toList());
    }

    @GetMapping("/products")
    public ApiResponse<List<PublicProductVO>> products(@RequestParam String appId, @RequestParam(required = false) Long categoryId) {
        Long tenantId = tenantId(appId);
        var q = new LambdaQueryWrapper<Product>().eq(Product::getTenantId, tenantId).eq(Product::getStatus, ProductStatus.ON_SALE).gt(Product::getStock, 0).eq(Product::getDeleted, 0);
        if (categoryId != null) q.eq(Product::getCategoryId, categoryId);
        return ApiResponse.ok(products.selectList(q).stream().map(PublicProductVO::from).toList());
    }

    private Long tenantId(String appId) {
        return configs.requireActiveTenantIdByAppId(appId);
    }
}
