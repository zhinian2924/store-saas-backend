package com.example.storesaas.mini.dto;

public record PublicStoreResponse(
        String name,
        String logoUrl,
        String themeColor,
        String businessHours
) {
}
