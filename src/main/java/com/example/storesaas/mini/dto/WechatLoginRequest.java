package com.example.storesaas.mini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record WechatLoginRequest(
        @NotBlank(message = "缺少微信登录凭证") String code,
        @NotBlank(message = "缺少小程序AppID")
        @Pattern(regexp = "^wx[a-zA-Z0-9]{16}$", message = "小程序AppID格式不正确") String appId
) {
}
