package com.example.storesaas.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLogin(NotLoginException ex) {
        return ApiResponse.fail(401, "请先登录");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<Void> handleNoPermission(NotPermissionException ex) {
        return ApiResponse.fail(403, "无权限访问");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidation(Exception ex) {
        return ApiResponse.fail(422, "参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknown(Exception ex) {
        return ApiResponse.fail(500, "系统异常");
    }
}
