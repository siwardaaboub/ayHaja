package com.climateplatform.climate.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateDataRequest {
    @NotNull(message = "Governorate ID is required")
    private UUID governorateId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    @NotNull(message = "Temperature is required")
    private BigDecimal temperature;

    private BigDecimal feelsLike;

    @DecimalMin(value = "0.0", message = "Humidity must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Humidity must be between 0 and 100")
    private BigDecimal humidity;

    private BigDecimal pressure;

    private BigDecimal windSpeed;

    private Integer windDirection;

    private BigDecimal uvIndex;

    private BigDecimal precipitation;

    private BigDecimal cloudCover;
}
