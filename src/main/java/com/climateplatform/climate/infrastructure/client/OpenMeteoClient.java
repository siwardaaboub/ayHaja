package com.climateplatform.climate.infrastructure.client;

import com.climateplatform.climate.infrastructure.exception.ExternalApiException;
import com.climateplatform.climate.infrastructure.exception.ExternalApiUnavailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpenMeteoClient {
    private static final String API_NAME = "Open-Meteo";
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final int timeoutSeconds;

    public OpenMeteoClient(RestTemplate restTemplate, ObjectMapper objectMapper,
                          @Value("${climate.api.timeout-seconds:10}") int timeoutSeconds) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.timeoutSeconds = timeoutSeconds;
    }

    public OpenMeteoResponse getCurrentWeather(BigDecimal latitude, BigDecimal longitude) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("current", "temperature_2m,apparent_temperature,relative_humidity_2m,pressure_msl,wind_speed_10m,wind_direction_10m,uv_index,cloud_cover,precipitation")
                    .queryParam("timezone", "auto")
                    .toUriString();

            log.debug("Calling Open-Meteo API for current weather: {}", url);
            String response = restTemplate.getForObject(url, String.class);

            return objectMapper.readValue(response, OpenMeteoResponse.class);
        } catch (RestClientException e) {
            log.error("Failed to call Open-Meteo API for current weather", e);
            throw new ExternalApiException(API_NAME, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to parse Open-Meteo API response", e);
            throw new ExternalApiException(API_NAME, "Failed to parse response");
        }
    }

    public OpenMeteoResponse getForecast(BigDecimal latitude, BigDecimal longitude, int days) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,wind_direction_10m_dominant,uv_index_max")
                    .queryParam("forecast_days", days)
                    .queryParam("timezone", "auto")
                    .toUriString();

            log.debug("Calling Open-Meteo API for forecast: {}", url);
            String response = restTemplate.getForObject(url, String.class);

            return objectMapper.readValue(response, OpenMeteoResponse.class);
        } catch (RestClientException e) {
            log.error("Failed to call Open-Meteo API for forecast", e);
            throw new ExternalApiException(API_NAME, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to parse Open-Meteo API response", e);
            throw new ExternalApiException(API_NAME, "Failed to parse response");
        }
    }

    public List<HistoricalDataPoint> getHistoricalData(BigDecimal latitude, BigDecimal longitude,
                                                       LocalDate startDate, LocalDate endDate) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://archive-api.open-meteo.com/v1/archive")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("start_date", startDate)
                    .queryParam("end_date", endDate)
                    .queryParam("daily", "temperature_2m_max,temperature_2m_min,temperature_2m_mean,precipitation_sum,wind_speed_10m_max,relative_humidity_2m_mean,uv_index_max")
                    .queryParam("timezone", "auto")
                    .toUriString();

            log.debug("Calling Open-Meteo Archive API: {}", url);
            String response = restTemplate.getForObject(url, String.class);

            return parseHistoricalResponse(response, startDate, endDate);
        } catch (RestClientException e) {
            log.error("Failed to call Open-Meteo Archive API", e);
            throw new ExternalApiException(API_NAME, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to parse Open-Meteo Archive API response", e);
            throw new ExternalApiException(API_NAME, "Failed to parse response");
        }
    }

    private List<HistoricalDataPoint> parseHistoricalResponse(String response, LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Object> map = objectMapper.readValue(response, Map.class);
            Map<String, Object> daily = (Map<String, Object>) map.get("daily");

            List<String> dates = (List<String>) daily.get("time");
            List<Double> tempMax = (List<Double>) daily.get("temperature_2m_max");
            List<Double> tempMin = (List<Double>) daily.get("temperature_2m_min");
            List<Double> tempMean = (List<Double>) daily.get("temperature_2m_mean");
            List<Double> precipitation = (List<Double>) daily.get("precipitation_sum");
            List<Double> windSpeed = (List<Double>) daily.get("wind_speed_10m_max");
            List<Double> humidity = (List<Double>) daily.get("relative_humidity_2m_mean");
            List<Double> uvIndex = (List<Double>) daily.get("uv_index_max");

            List<HistoricalDataPoint> result = new ArrayList<>();
            for (int i = 0; i < dates.size(); i++) {
                result.add(HistoricalDataPoint.builder()
                        .date(LocalDate.parse(dates.get(i)))
                        .temperatureAvg(BigDecimal.valueOf(tempMean.get(i)))
                        .temperatureMax(BigDecimal.valueOf(tempMax.get(i)))
                        .temperatureMin(BigDecimal.valueOf(tempMin.get(i)))
                        .precipitation(BigDecimal.valueOf(precipitation.get(i)))
                        .windSpeed(BigDecimal.valueOf(windSpeed.get(i)))
                        .humidity(BigDecimal.valueOf(humidity.get(i)))
                        .uvIndex(BigDecimal.valueOf(uvIndex.get(i)))
                        .build());
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to parse historical data response", e);
            throw new ExternalApiException(API_NAME, "Failed to parse historical data");
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricalDataPoint {
        private LocalDate date;
        private BigDecimal temperatureAvg;
        private BigDecimal temperatureMax;
        private BigDecimal temperatureMin;
        private BigDecimal precipitation;
        private BigDecimal windSpeed;
        private BigDecimal humidity;
        private BigDecimal uvIndex;
    }
}