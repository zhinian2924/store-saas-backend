package com.example.storesaas.mini.dto;

import jakarta.validation.constraints.*;

public record AddressRequest(@NotBlank String consignee, @NotBlank String phone, String province, String city,
                             String district, @NotBlank String detail, Boolean isDefault) {
}
