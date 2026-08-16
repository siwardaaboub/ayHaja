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
public class ClimateDataResponse {
    private UUID id;
    private UUID governorateId;
    private String governorateName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate dateRecorded;
    private BigDecimal temperature;
    private BigDecimal feelsLike;
    private BigDecimal humidity;
    private BigDecimal pressure;
    private BigDecimal windSpeed;
    private Integer windDirection;
    private BigDecimal uvIndex;
    private BigDecimal precipitation;
    private BigDecimal cloudCover;
    private String dataSource;
    private LocalDateTime createdAt;
}
