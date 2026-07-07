package com.example.storesaas.common;

import com.example.storesaas.common.constants.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        this(ResultCode.BAD_REQUEST, message);
    }

    public BusinessException(int code, String message) {
        super(message);// 调用父类构造方法，将message传递给父类
        this.code = code;
    }
}
