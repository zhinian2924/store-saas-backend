package com.example.storesaas.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import org.springframework.validation.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return fail(HttpStatus.valueOf(ex.getCode()), ex.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotLogin(NotLoginException ex) {
        return fail(HttpStatus.UNAUTHORIZED, "请先登录");
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoPermission(NotPermissionException ex) {
        return fail(HttpStatus.FORBIDDEN, "无权限访问");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception ex) {
        return fail(HttpStatus.UNPROCESSABLE_ENTITY, "参数校验失败");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEntry(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            return fail(HttpStatus.CONFLICT, "数据已存在，请勿重复添加");
        }
        return fail(HttpStatus.CONFLICT, "数据冲突");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }

    /**
     * 统一返回失败
     * @param status 状态码
     * @param message 提示信息
     * @return 统一返回结果
     */
    private ResponseEntity<ApiResponse<Void>> fail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.fail(status, message));
    }
}
