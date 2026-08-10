package com.example.storesaas.common;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public record ApiResponse<T>(int code, String message, T data) {

    private static final String SUCCESS_MESSAGE = "ok";

    public ApiResponse {
        if (HttpStatus.resolve(code) == null) {
            throw new IllegalArgumentException("Unsupported HTTP status code: " + code);
        }
        Objects.requireNonNull(message, "message must not be null");
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, data);
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return fail(HttpStatus.valueOf(code), message);
    }

    public static ApiResponse<Void> fail(HttpStatus status, String message) {
        Objects.requireNonNull(status, "status must not be null");
        return new ApiResponse<>(status.value(), message, null);
    }
}
