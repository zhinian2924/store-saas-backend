package com.example.storesaas.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.user.dto.StaffCreateRequest;
import com.example.storesaas.user.dto.StaffResponse;
import com.example.storesaas.user.dto.StaffUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @SaCheckPermission(Permissions.STAFF_VIEW)
    @GetMapping
    public ApiResponse<List<StaffResponse>> list() {
        return ApiResponse.ok(staffService.list());
    }

    @SaCheckPermission(Permissions.STAFF_ADD)
    @PostMapping
    public ApiResponse<StaffResponse> create(@Valid @RequestBody StaffCreateRequest request) {
        return ApiResponse.ok(staffService.create(request));
    }

    @SaCheckPermission(Permissions.STAFF_UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> update(@PathVariable Long id, @Valid @RequestBody StaffUpdateRequest request) {
        return ApiResponse.ok(staffService.update(id, request));
    }

    @SaCheckPermission(Permissions.STAFF_DISABLE)
    @PutMapping("/{id}/status")
    public ApiResponse<StaffResponse> setStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        return ApiResponse.ok(staffService.setStatus(id, request.get("status")));
    }
}
