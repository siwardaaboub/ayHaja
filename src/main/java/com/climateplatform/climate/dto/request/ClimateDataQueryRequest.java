package com.climateplatform.climate.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateDataQueryRequest {
    @NotNull(message = "Governorate ID is required")
    private UUID governorateId;

    private String startDate;
    private String endDate;
    private Integer limit = 100;
}
