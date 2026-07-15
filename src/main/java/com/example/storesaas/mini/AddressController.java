package com.example.storesaas.mini;

import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.mini.dto.AddressRequest;
import com.example.storesaas.mini.entity.CustomerAddress;
import com.example.storesaas.mini.service.AddressService;
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
    public ApiResponse<List<CustomerAddress>> list() {
        CustomerContext.current();
        return ApiResponse.ok(service.list());
    }

    @PostMapping
    public ApiResponse<CustomerAddress> create(@Valid @RequestBody AddressRequest r) {
        CustomerContext.current();
        return ApiResponse.ok(service.create(r));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerAddress> update(@PathVariable Long id, @Valid @RequestBody AddressRequest r) {
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
    public ApiResponse<CustomerAddress> setDefault(@PathVariable Long id) {
        CustomerContext.current();
        return ApiResponse.ok(service.setDefault(id));
    }
}
