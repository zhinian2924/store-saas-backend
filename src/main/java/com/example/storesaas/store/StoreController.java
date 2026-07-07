package com.example.storesaas.store;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.store.dto.StoreProfileRequest;
import com.example.storesaas.store.entity.Store;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/profile")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @SaCheckPermission(Permissions.STORE_VIEW)
    @GetMapping
    public ApiResponse<Store> profile() {
        return ApiResponse.ok(storeService.profile());
    }

    @SaCheckPermission(Permissions.STORE_UPDATE)
    @PutMapping
    public ApiResponse<Store> updateProfile(@Valid @RequestBody StoreProfileRequest request) {
        return ApiResponse.ok(storeService.updateProfile(request));
    }
}
