package com.example.smarthealth.dto.common;

import lombok.Data;

@Data
public class ApiSuccess<T> {
    private int status;
    private String message;
    private T data;

    public ApiSuccess(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiSuccess<T> success(String message, T data) {
        return new ApiSuccess<>(200, message, data);
    }
}
