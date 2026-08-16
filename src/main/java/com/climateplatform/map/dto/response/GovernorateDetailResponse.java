package com.climateplatform.map.dto.response;

import com.climateplatform.climate.dto.response.ClimateDataResponse;
import com.climateplatform.climate.dto.response.ClimateForecastResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GovernorateDetailResponse {
    private GovernorateResponse governorate;
    private ClimateDataResponse currentWeather;
    private List<ClimateForecastResponse> forecasts;
}
