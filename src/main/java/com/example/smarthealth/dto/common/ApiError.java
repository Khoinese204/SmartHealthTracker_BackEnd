package com.example.smarthealth.dto.common;

import lombok.Data;

@Data
public class ApiError {
    private int status;
    private String message;
    private Object error;

    public ApiError(int status, String message, Object error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public static ApiError of(int status, String message, Object error) {
        return new ApiError(status, message, error);
    }
}
