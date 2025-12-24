package com.example.smarthealth.dto.health;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "GPS point recorded during a workout session")
public class GpsPointDto {

    @Schema(example = "1", description = "Sequence index of the GPS point in the workout route")
    private Integer sequenceIndex;

    @Schema(example = "10.762622", description = "Latitude in decimal degrees")
    private BigDecimal latitude;

    @Schema(example = "106.660172", description = "Longitude in decimal degrees")
    private BigDecimal longitude;

    @Schema(example = "12.5", nullable = true, description = "Altitude in meters (optional)")
    private BigDecimal altitude;

    @Schema(example = "2025-01-01T06:35:10", description = "Timestamp when this GPS point was recorded (ISO-8601)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
