package com.climateplatform.climate.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "climate_forecasts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "governorate_id", nullable = false)
    private UUID governorateId;

    @Column(name = "governorate_name", nullable = false, length = 100)
    private String governorateName;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "temperature_max", precision = 5, scale = 2)
    private BigDecimal temperatureMax;

    @Column(name = "temperature_min", precision = 5, scale = 2)
    private BigDecimal temperatureMin;

    @Column(name = "precipitation", precision = 6, scale = 2)
    private BigDecimal precipitation;

    @Column(name = "wind_speed", precision = 6, scale = 2)
    private BigDecimal windSpeed;

    @Column(name = "wind_direction")
    private Integer windDirection;

    @Column(name = "uv_index", precision = 4, scale = 2)
    private BigDecimal uvIndex;

    @Column(name = "confidence", precision = 4, scale = 2)
    private BigDecimal confidence;

    @Column(name = "data_source", length = 50)
    private String dataSource;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
