package com.climateplatform.map.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GovernorateResponse {
    private UUID id;
    private String name;
    private String nameAr;
    private BigDecimal centerLat;
    private BigDecimal centerLon;
    private Long population;
    private BigDecimal areaKm2;
    private String geometry;
}
