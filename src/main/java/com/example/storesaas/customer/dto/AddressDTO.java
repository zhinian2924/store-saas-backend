package com.example.storesaas.customer.dto;

import jakarta.validation.constraints.*;

public record AddressDTO(
        @NotBlank String consignee, // 收货人
        @NotBlank String phone,
        String province,
        String city,
        String district,
        @NotBlank String detail, // 详细地址
        Boolean isDefault
) {
}
