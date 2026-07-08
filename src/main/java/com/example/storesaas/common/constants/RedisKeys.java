package com.example.storesaas.common.constants;

public final class RedisKeys {
    // 存储短信验证码的Redis key前缀
    public static final String STORE_SMS_CODE = "auth:sms:store:";

    // 根据手机号生成存储短信验证码的Redis key
    public static String storeSmsCode(String mobile) {
        return STORE_SMS_CODE + mobile;
    }

    private RedisKeys() {
    }
}
