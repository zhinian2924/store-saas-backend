package com.example.storesaas.miniappconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MiniappConfigRequest(
        @NotBlank(message = "请输入小程序AppID")
        @Pattern(regexp = "^wx[a-zA-Z0-9]{16}$", message = "小程序AppID格式不正确")
        String appId,

        @Size(max = 128, message = "AppSecret长度不能超过128个字符")
        String appSecret
) {
}
