package com.example.storesaas.mini.vo;

import com.example.storesaas.mini.entity.CustomerAddress;

import java.time.LocalDateTime;

public record AddressVO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted,
                        Long tenantId, Long customerId, String consignee, String phone,
                        String province, String city, String district, String detail, Integer isDefault) {
    public static AddressVO from(CustomerAddress address) {
        return new AddressVO(address.getId(), address.getCreatedAt(), address.getUpdatedAt(), address.getDeleted(),
                address.getTenantId(), address.getCustomerId(), address.getConsignee(), address.getPhone(),
                address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail(), address.getIsDefault());
    }
}
