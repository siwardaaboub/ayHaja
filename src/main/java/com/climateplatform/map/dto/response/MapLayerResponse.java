package com.climateplatform.map.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerResponse {
    private UUID id;
    private String name;
    private String displayName;
    private String layerType;
    private String urlTemplate;
    private String attribution;
    private Boolean isActive;
    private Integer zIndex;
    private BigDecimal opacity;
}
