package com.example.storesaas.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.constants.Permissions;
import com.example.storesaas.user.dto.StaffCreateDTO;
import com.example.storesaas.user.dto.StaffStatusDTO;
import com.example.storesaas.user.vo.StaffVO;
import com.example.storesaas.user.dto.StaffUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @SaCheckPermission(Permissions.STAFF_VIEW)
    @GetMapping
    public ApiResponse<List<StaffVO>> list() {
        return ApiResponse.ok(staffService.list());
    }

    @SaCheckPermission(Permissions.STAFF_ADD)
    @PostMapping
    public ApiResponse<StaffVO> create(@Valid @RequestBody StaffCreateDTO request) {
        return ApiResponse.ok(staffService.create(request));
    }

    @SaCheckPermission(Permissions.STAFF_UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<StaffVO> update(@PathVariable Long id, @Valid @RequestBody StaffUpdateDTO request) {
        return ApiResponse.ok(staffService.update(id, request));
    }

    @SaCheckPermission(Permissions.STAFF_DISABLE)
    @PutMapping("/{id}/status")
    public ApiResponse<StaffVO> setStatus(@PathVariable Long id, @Valid @RequestBody StaffStatusDTO request) {
        return ApiResponse.ok(staffService.setStatus(id, request.status()));
    }
}
