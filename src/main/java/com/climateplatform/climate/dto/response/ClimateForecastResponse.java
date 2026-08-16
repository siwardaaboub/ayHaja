package com.climateplatform.climate.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateForecastResponse {
    private UUID id;
    private UUID governorateId;
    private String governorateName;
    private LocalDate forecastDate;
    private BigDecimal temperatureMax;
    private BigDecimal temperatureMin;
    private BigDecimal precipitation;
    private BigDecimal windSpeed;
    private Integer windDirection;
    private BigDecimal uvIndex;
    private BigDecimal confidence;
    private String dataSource;
    private LocalDateTime createdAt;
}
