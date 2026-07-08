package com.example.storesaas.common.constants;

public final class BusinessConstants {
    // 订单编号 / 支付编号前缀
    public static final String ORDER_NO_PREFIX = "SO";
    public static final String PAY_NO_PREFIX = "PAY";
    public static final String PAY_CHANNEL_MOCK = "MOCK";

    // 短信验证码
    public static final int SMS_CODE_RANGE_MIN = 100000;
    public static final int SMS_CODE_RANGE_MAX = 1000000;

    // 租户编号
    public static final int TENANT_CODE_RETRY_LIMIT = 10;
    public static final int TENANT_CODE_MAX_PREFIX_LENGTH = 24;
    public static final String DEFAULT_TENANT_CODE_PREFIX = "store";

    // 门店店主用户名
    public static final String STORE_USERNAME_PREFIX = "user_";

    // 订单编号随机后缀范围
    public static final int ORDER_NO_RANDOM_MIN = 1000;
    public static final int ORDER_NO_RANDOM_MAX = 9999;

    private BusinessConstants() {
    }
}
