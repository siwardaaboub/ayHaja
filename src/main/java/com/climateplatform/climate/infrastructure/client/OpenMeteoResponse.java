package com.climateplatform.climate.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponse {
    private Current current;
    private Daily daily;
    private List<Hourly> hourly;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        private String time;

        @JsonProperty("temperature_2m")
        private Double temperature;

        @JsonProperty("apparent_temperature")
        private Double feelsLike;

        @JsonProperty("relative_humidity_2m")
        private Double humidity;

        @JsonProperty("pressure_msl")
        private Double pressure;

        @JsonProperty("wind_speed_10m")
        private Double windSpeed;

        @JsonProperty("wind_direction_10m")
        private Integer windDirection;

        @JsonProperty("uv_index")
        private Double uvIndex;

        @JsonProperty("cloud_cover")
        private Double cloudCover;

        @JsonProperty("precipitation")
        private Double precipitation;
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

        @JsonProperty("wind_direction_10m_dominant")
        private List<Integer> windDirectionDominant;

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