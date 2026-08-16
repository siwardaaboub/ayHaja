package com.climateplatform.climate.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenMeteoResponse {
    private Current current;
    private Daily daily;
    private List<Hourly> hourly;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Current {
        private Double temperature;
        
        @JsonProperty("feels_like")
        private Double feelsLike;
        
        private Double humidity;
        private Double pressure;
        
        @JsonProperty("wind_speed")
        private Double windSpeed;
        
        @JsonProperty("wind_direction")
        private Integer windDirection;
        
        @JsonProperty("uv_index")
        private Double uvIndex;
        
        @JsonProperty("cloud_cover")
        private Double cloudCover;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Daily {
        @JsonProperty("time")
        private List<String> time;
        
        @JsonProperty("temperature_2m_max")
        private List<Double> temperatureMax;
        
        @JsonProperty("temperature_2m_min")
        private List<Double> temperatureMin;
        
        @JsonProperty("precipitation_sum")
        private List<Double> precipitationSum;
        
        @JsonProperty("wind_speed_10m_max")
        private List<Double> windSpeedMax;
        
        @JsonProperty("uv_index_max")
        private List<Double> uvIndexMax;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hourly {
        private String time;
        private Double temperature;
        private Double humidity;
    }
}
