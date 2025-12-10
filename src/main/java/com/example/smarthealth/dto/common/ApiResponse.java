package com.example.smarthealth.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard API Response Wrapper")
public class ApiResponse<T> {

    @Schema(example = "200")
    private int status = 200;

    @Schema(example = "OK")
    private String message = "OK";

    private T data;
}
