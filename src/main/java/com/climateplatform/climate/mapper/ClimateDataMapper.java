package com.climateplatform.climate.mapper;

import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.model.ClimateForecast;
import com.climateplatform.climate.domain.model.HistoricalClimateData;
import com.climateplatform.climate.dto.response.ClimateDataResponse;
import com.climateplatform.climate.dto.response.ClimateForecastResponse;
import com.climateplatform.climate.dto.response.HistoricalClimateDataResponse;
import org.springframework.stereotype.Component;

@Component
public class ClimateDataMapper {

    public ClimateDataResponse toResponse(ClimateData climateData) {
        return ClimateDataResponse.builder()
                .id(climateData.getId())
                .governorateId(climateData.getGovernorateId())
                .governorateName(climateData.getGovernorateName())
                .latitude(climateData.getLatitude())
                .longitude(climateData.getLongitude())
                .dateRecorded(climateData.getDateRecorded())
                .temperature(climateData.getTemperature())
                .feelsLike(climateData.getFeelsLike())
                .humidity(climateData.getHumidity())
                .pressure(climateData.getPressure())
                .windSpeed(climateData.getWindSpeed())
                .windDirection(climateData.getWindDirection())
                .uvIndex(climateData.getUvIndex())
                .precipitation(climateData.getPrecipitation())
                .cloudCover(climateData.getCloudCover())
                .dataSource(climateData.getDataSource())
                .createdAt(climateData.getCreatedAt())
                .build();
    }

    public ClimateForecastResponse toResponse(ClimateForecast forecast) {
        return ClimateForecastResponse.builder()
                .id(forecast.getId())
                .governorateId(forecast.getGovernorateId())
                .governorateName(forecast.getGovernorateName())
                .forecastDate(forecast.getForecastDate())
                .temperatureMax(forecast.getTemperatureMax())
                .temperatureMin(forecast.getTemperatureMin())
                .precipitation(forecast.getPrecipitation())
                .windSpeed(forecast.getWindSpeed())
                .windDirection(forecast.getWindDirection())
                .uvIndex(forecast.getUvIndex())
                .confidence(forecast.getConfidence())
                .dataSource(forecast.getDataSource())
                .createdAt(forecast.getCreatedAt())
                .build();
    }

    public HistoricalClimateDataResponse toResponse(HistoricalClimateData historicalData) {
        return HistoricalClimateDataResponse.builder()
                .id(historicalData.getId())
                .governorateId(historicalData.getGovernorateId())
                .governorateName(historicalData.getGovernorateName())
                .latitude(historicalData.getLatitude())
                .longitude(historicalData.getLongitude())
                .dateRecorded(historicalData.getDateRecorded())
                .temperatureAvg(historicalData.getTemperatureAvg())
                .temperatureMax(historicalData.getTemperatureMax())
                .temperatureMin(historicalData.getTemperatureMin())
                .humidity(historicalData.getHumidity())
                .precipitation(historicalData.getPrecipitation())
                .windSpeed(historicalData.getWindSpeed())
                .pressure(historicalData.getPressure())
                .uvIndex(historicalData.getUvIndex())
                .dataSource(historicalData.getDataSource())
                .createdAt(historicalData.getCreatedAt())
                .build();
    }
}
