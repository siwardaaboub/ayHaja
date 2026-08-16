package com.climateplatform.map.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarkerResponse {
    private UUID id;
    private UUID governorateId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String markerType;
    private String title;
    private String description;
    private String popupContent;
    private String iconUrl;
    private Boolean isActive;
}
