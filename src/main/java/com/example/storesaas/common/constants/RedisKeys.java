package com.example.storesaas.common.constants;

public final class RedisKeys {
    public static final String STORE_SMS_CODE = "auth:sms:store:";

    public static String storeSmsCode(String mobile) {
        return STORE_SMS_CODE + mobile;
    }

    private RedisKeys() {
    }
}
