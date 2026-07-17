package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.AddressDTO;
import com.example.storesaas.mini.service.AddressService;
import com.example.storesaas.mini.vo.AddressVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mini/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<AddressVO>> list() {
        CustomerContext.current();
        return ApiResponse.ok(service.list());
    }

    @PostMapping
    public ApiResponse<AddressVO> create(@Valid @RequestBody AddressDTO r) {
        CustomerContext.current();
        return ApiResponse.ok(service.create(r));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressVO> update(@PathVariable Long id, @Valid @RequestBody AddressDTO r) {
        CustomerContext.current();
        return ApiResponse.ok(service.update(id, r));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        CustomerContext.current();
        service.remove(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/default")
    public ApiResponse<AddressVO> setDefault(@PathVariable Long id) {
        CustomerContext.current();
        return ApiResponse.ok(service.setDefault(id));
    }
}
