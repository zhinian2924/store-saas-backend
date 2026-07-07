package com.example.storesaas.common.constants;

public final class ResultCode {
    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 400;// 请求错误
    public static final int UNAUTHORIZED = 401;// 未授权
    public static final int FORBIDDEN = 403;// 禁止访问
    public static final int CONFLICT = 409;// 冲突
    public static final int VALIDATION_ERROR = 422;// 验证错误
    public static final int INTERNAL_ERROR = 500;// 内部错误

    private ResultCode() {
    }
}
