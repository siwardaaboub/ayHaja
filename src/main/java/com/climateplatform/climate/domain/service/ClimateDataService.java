package com.climateplatform.climate.domain.service;

import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.model.ClimateForecast;
import com.climateplatform.climate.domain.model.HistoricalClimateData;
import com.climateplatform.climate.domain.repository.ClimateDataRepository;
import com.climateplatform.climate.domain.repository.ClimateForecastRepository;
import com.climateplatform.climate.domain.repository.HistoricalClimateDataRepository;
import com.climateplatform.climate.dto.request.ClimateDataQueryRequest;
import com.climateplatform.climate.dto.response.ClimateDataResponse;
import com.climateplatform.climate.dto.response.ClimateForecastResponse;
import com.climateplatform.climate.dto.response.HistoricalClimateDataResponse;
import com.climateplatform.climate.infrastructure.client.OpenMeteoClient;
import com.climateplatform.climate.infrastructure.client.OpenMeteoResponse;
import com.climateplatform.climate.mapper.ClimateDataMapper;
import com.climateplatform.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClimateDataService {
    private final ClimateDataRepository climateDataRepository;
    private final ClimateForecastRepository climateForecastRepository;
    private final HistoricalClimateDataRepository historicalClimateDataRepository;
    private final OpenMeteoClient openMeteoClient;
    private final ClimateDataMapper climateDataMapper;

    @Cacheable(value = "current-weather", key = "#governorateId")
    @Transactional
    public ClimateDataResponse getCurrentWeather(UUID governorateId, String governorateName,
                                                  BigDecimal latitude, BigDecimal longitude) {
        log.debug("Fetching current weather for governorate: {}", governorateName);

        OpenMeteoResponse response = openMeteoClient.getCurrentWeather(latitude, longitude);

        ClimateData climateData = ClimateData.builder()
                .governorateId(governorateId)
                .governorateName(governorateName)
                .latitude(latitude)
                .longitude(longitude)
                .dateRecorded(LocalDate.now())
                .temperature(BigDecimal.valueOf(response.getCurrent().getTemperature()))
                .feelsLike(response.getCurrent().getFeelsLike() != null ?
                        BigDecimal.valueOf(response.getCurrent().getFeelsLike()) : null)
                .humidity(response.getCurrent().getHumidity() != null ?
                        BigDecimal.valueOf(response.getCurrent().getHumidity()) : null)
                .pressure(response.getCurrent().getPressure() != null ?
                        BigDecimal.valueOf(response.getCurrent().getPressure()) : null)
                .windSpeed(response.getCurrent().getWindSpeed() != null ?
                        BigDecimal.valueOf(response.getCurrent().getWindSpeed()) : null)
                .windDirection(response.getCurrent().getWindDirection())
                .uvIndex(response.getCurrent().getUvIndex() != null ?
                        BigDecimal.valueOf(response.getCurrent().getUvIndex()) : null)
                .cloudCover(response.getCurrent().getCloudCover() != null ?
                        BigDecimal.valueOf(response.getCurrent().getCloudCover()) : null)
                .precipitation(response.getCurrent().getPrecipitation() != null ?
                        BigDecimal.valueOf(response.getCurrent().getPrecipitation()) : null)
                .dataSource("Open-Meteo")
                .build();

        ClimateData savedData = climateDataRepository.saveAndFlush(climateData);
        return climateDataMapper.toResponse(savedData);
    }

    @Cacheable(value = "forecasts", key = "#governorateId + '_' + #days")
    @Transactional
    public List<ClimateForecastResponse> getForecasts(UUID governorateId, String governorateName,
                                                       BigDecimal latitude, BigDecimal longitude, int days) {
        log.debug("Fetching {}-day forecast for governorate: {}", days, governorateName);

        OpenMeteoResponse response = openMeteoClient.getForecast(latitude, longitude, days);

        List<ClimateForecast> forecasts = new ArrayList<>();
        List<String> dates = response.getDaily().getTime();
        List<Double> tempMax = response.getDaily().getTemperatureMax();
        List<Double> tempMin = response.getDaily().getTemperatureMin();
        List<Double> precipitation = response.getDaily().getPrecipitationSum();
        List<Double> windSpeed = response.getDaily().getWindSpeedMax();
        List<Integer> windDirection = response.getDaily().getWindDirectionDominant();
        List<Double> uvIndex = response.getDaily().getUvIndexMax();

        for (int i = 0; i < dates.size(); i++) {
            ClimateForecast forecast = ClimateForecast.builder()
                    .governorateId(governorateId)
                    .governorateName(governorateName)
                    .forecastDate(LocalDate.parse(dates.get(i)))
                    .temperatureMax(BigDecimal.valueOf(tempMax.get(i)))
                    .temperatureMin(BigDecimal.valueOf(tempMin.get(i)))
                    .precipitation(BigDecimal.valueOf(precipitation.get(i)))
                    .windSpeed(BigDecimal.valueOf(windSpeed.get(i)))
                    .windDirection(windDirection != null && windDirection.get(i) != null ?
                            windDirection.get(i) : null)
                    .uvIndex(BigDecimal.valueOf(uvIndex.get(i)))
                    .confidence(BigDecimal.valueOf(0.85))
                    .dataSource("Open-Meteo")
                    .build();
            forecasts.add(forecast);
        }

        climateForecastRepository.deleteByGovernorateIdAndForecastDateBefore(
                governorateId, LocalDate.now().plusDays(days));

        List<ClimateForecast> savedForecasts = climateForecastRepository.saveAllAndFlush(forecasts);
        return savedForecasts.stream()
                .map(climateDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "historical-data", key = "#governorateId + '_' + #startDate + '_' + #endDate")
    public Page<HistoricalClimateDataResponse> getHistoricalData(ClimateDataQueryRequest request, Pageable pageable) {
        LocalDate startDate = request.getStartDate() != null ?
                LocalDate.parse(request.getStartDate()) : LocalDate.of(1990, 1, 1);
        LocalDate endDate = request.getEndDate() != null ?
                LocalDate.parse(request.getEndDate()) : LocalDate.now();

        log.debug("Fetching historical data for governorate: {} from {} to {}",
                request.getGovernorateId(), startDate, endDate);

        List<HistoricalClimateData> historicalData = historicalClimateDataRepository
                .findByGovernorateAndDateRange(request.getGovernorateId(), startDate, endDate);

        List<HistoricalClimateDataResponse> responses = historicalData.stream()
                .map(climateDataMapper::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Cacheable(value = "historical-yearly", key = "#governorateId + '_' + #year")
    public List<HistoricalClimateDataResponse> getHistoricalDataByYear(UUID governorateId, int year) {
        log.debug("Fetching historical data for governorate: {} year: {}", governorateId, year);

        List<HistoricalClimateData> historicalData = historicalClimateDataRepository
                .findByGovernorateAndYear(governorateId, year);

        return historicalData.stream()
                .map(climateDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "historical-monthly", key = "#governorateId + '_' + #year + '_' + #month")
    public List<HistoricalClimateDataResponse> getHistoricalDataByMonth(UUID governorateId, int year, int month) {
        log.debug("Fetching historical data for governorate: {} year: {} month: {}", governorateId, year, month);

        List<HistoricalClimateData> historicalData = historicalClimateDataRepository
                .findByGovernorateYearAndMonth(governorateId, year, month);

        return historicalData.stream()
                .map(climateDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClimateDataResponse> getClimateDataByDate(LocalDate date) {
        log.debug("Fetching climate data for date: {}", date);

        List<ClimateData> climateData = climateDataRepository.findByDate(date);
        return climateData.stream()
                .map(climateDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void refreshHistoricalData(UUID governorateId, String governorateName,
                                      BigDecimal latitude, BigDecimal longitude,
                                      LocalDate startDate, LocalDate endDate) {
        log.info("Refreshing historical data for governorate: {} from {} to {}", governorateName, startDate, endDate);

        List<OpenMeteoClient.HistoricalDataPoint> dataPoints = openMeteoClient
                .getHistoricalData(latitude, longitude, startDate, endDate);

        List<HistoricalClimateData> historicalData = dataPoints.stream()
                .map(point -> HistoricalClimateData.builder()
                        .governorateId(governorateId)
                        .governorateName(governorateName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .dateRecorded(point.getDate())
                        .temperatureAvg(point.getTemperatureAvg())
                        .temperatureMax(point.getTemperatureMax())
                        .temperatureMin(point.getTemperatureMin())
                        .precipitation(point.getPrecipitation())
                        .windSpeed(point.getWindSpeed())
                        .humidity(point.getHumidity())
                        .uvIndex(point.getUvIndex())
                        .dataSource("Open-Meteo")
                        .build())
                .collect(Collectors.toList());

        historicalClimateDataRepository.deleteByGovernorateId(governorateId);
        historicalClimateDataRepository.saveAll(historicalData);

        log.info("Successfully refreshed {} historical data records for governorate: {}",
                historicalData.size(), governorateName);
    }
}