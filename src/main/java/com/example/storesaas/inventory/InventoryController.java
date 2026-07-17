package com.example.storesaas.inventory;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.inventory.dto.StockAdjustDTO;
import com.example.storesaas.inventory.vo.InventoryFlowVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @SaCheckPermission(Permissions.INVENTORY_ADJUST)
    @PostMapping("/adjust")
    public ApiResponse<InventoryFlowVO> adjust(@Valid @RequestBody StockAdjustDTO request) {
        return ApiResponse.ok(inventoryService.adjust(request));
    }

    @SaCheckPermission(Permissions.INVENTORY_VIEW)
    @GetMapping("/flows")
    public ApiResponse<List<InventoryFlowVO>> flows() {
        return ApiResponse.ok(inventoryService.flows());
    }
}
