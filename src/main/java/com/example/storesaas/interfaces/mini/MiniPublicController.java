package com.example.storesaas.interfaces.mini;

import com.example.storesaas.platform.web.ApiResponse;
import com.example.storesaas.catalog.application.ProductService;
import com.example.storesaas.catalog.vo.PublicCategoryVO;
import com.example.storesaas.catalog.vo.PublicProductVO;
import com.example.storesaas.miniapp.MiniappConfigService;
import com.example.storesaas.tenant.store.StoreService;
import com.example.storesaas.tenant.store.vo.PublicStoreVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mini/public")
public class MiniPublicController {
    private final StoreService stores;
    private final ProductService products;
    private final MiniappConfigService configs;

    public MiniPublicController(StoreService stores, ProductService products, MiniappConfigService configs) {
        this.stores = stores;
        this.products = products;
        this.configs = configs;
    }

    @GetMapping("/store")
    public ApiResponse<PublicStoreVO> store(@RequestParam String appId) {
        Long tenantId = tenantId(appId);
        return ApiResponse.ok(stores.publicStore(tenantId));
    }

    @GetMapping("/categories")
    public ApiResponse<List<PublicCategoryVO>> categories(@RequestParam String appId) {
        Long tenantId = tenantId(appId);
        return ApiResponse.ok(products.publicCategories(tenantId));
    }

    @GetMapping("/products")
    public ApiResponse<List<PublicProductVO>> products(@RequestParam String appId, @RequestParam(required = false) Long categoryId) {
        Long tenantId = tenantId(appId);
        return ApiResponse.ok(products.publicProducts(tenantId, categoryId));
    }

    private Long tenantId(String appId) {
        return configs.requireActiveTenantIdByAppId(appId);
    }
}
