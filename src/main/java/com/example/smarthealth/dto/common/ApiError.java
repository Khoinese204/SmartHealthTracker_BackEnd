package com.example.smarthealth.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Error response wrapper")
public class ApiError {

    @Schema(example = "400")
    private int status;

    @Schema(example = "Bad request")
    private String message;
}
