package com.example.storesaas.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record StoreProfileDTO(
        @NotBlank(message = "请输入门店名称")
        @Size(max = 128, message = "门店名称不能超过128个字符")
        String name,

        @Size(max = 255, message = "门店地址不能超过255个字符")
        String address,

        @Size(max = 64, message = "营业时间不能超过64个字符")
        String businessHours,

        @Size(max = 255, message = "LOGO地址不能超过255个字符")
        String logoUrl,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "主题色必须是#开头的六位十六进制颜色")
        String themeColor
) {
}
