package com.example.smarthealth.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GpsPointDto {
    private Integer sequenceIndex;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}