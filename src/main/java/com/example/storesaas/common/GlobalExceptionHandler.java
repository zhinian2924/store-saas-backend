package com.example.storesaas.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.example.storesaas.common.constants.ResultCode;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLogin(NotLoginException ex) {
        return ApiResponse.fail(ResultCode.UNAUTHORIZED, "请先登录");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<Void> handleNoPermission(NotPermissionException ex) {
        return ApiResponse.fail(ResultCode.FORBIDDEN, "无权限访问");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidation(Exception ex) {
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR, "参数校验失败");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ApiResponse<Void> handleDuplicateEntry(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            return ApiResponse.fail(ResultCode.CONFLICT, "数据已存在，请勿重复添加");
        }
        return ApiResponse.fail(ResultCode.CONFLICT, "数据冲突");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnknown(Exception ex) {
        return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "系统异常");
    }
}
