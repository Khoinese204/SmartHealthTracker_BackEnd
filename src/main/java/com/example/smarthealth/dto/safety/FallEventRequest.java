package com.example.smarthealth.dto.safety;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FallEventRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime detectedAt;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String sensorData; 
    
    private Boolean confirmed; 
}