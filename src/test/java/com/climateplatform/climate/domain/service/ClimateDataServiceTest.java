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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimateDataServiceTest {

    @Mock
    private ClimateDataRepository climateDataRepository;

    @Mock
    private ClimateForecastRepository climateForecastRepository;

    @Mock
    private HistoricalClimateDataRepository historicalClimateDataRepository;

    @Mock
    private OpenMeteoClient openMeteoClient;

    @Mock
    private ClimateDataMapper climateDataMapper;

    @InjectMocks
    private ClimateDataService climateDataService;

    private UUID governorateId;
    private String governorateName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @BeforeEach
    void setUp() {
        governorateId = UUID.randomUUID();
        governorateName = "Tunis";
        latitude = BigDecimal.valueOf(36.8025);
        longitude = BigDecimal.valueOf(10.1839);
    }

    @Test
    void getCurrentWeather_WhenValidRequest_ShouldReturnClimateDataResponse() {
        OpenMeteoResponse response = new OpenMeteoResponse();
        OpenMeteoResponse.Current current = new OpenMeteoResponse.Current();
        current.setTemperature(25.5);
        current.setFeelsLike(27.0);
        current.setHumidity(65.0);
        response.setCurrent(current);

        when(openMeteoClient.getCurrentWeather(latitude, longitude)).thenReturn(response);

        ClimateData mockClimateData = ClimateData.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .governorateName(governorateName)
                .temperature(BigDecimal.valueOf(25.5))
                .build();

        ClimateDataResponse mockResponse = ClimateDataResponse.builder()
                .id(mockClimateData.getId())
                .governorateId(governorateId)
                .governorateName(governorateName)
                .temperature(BigDecimal.valueOf(25.5))
                .build();

        when(climateDataRepository.save(any(ClimateData.class))).thenReturn(mockClimateData);
        when(climateDataMapper.toResponse(mockClimateData)).thenReturn(mockResponse);

        ClimateDataResponse result = climateDataService.getCurrentWeather(governorateId, governorateName, latitude, longitude);

        assertNotNull(result);
        assertEquals(governorateId, result.getGovernorateId());
        assertEquals(BigDecimal.valueOf(25.5), result.getTemperature());
        verify(climateDataRepository).save(any(ClimateData.class));
    }

    @Test
    void getForecasts_WhenValidRequest_ShouldReturnForecastResponses() {
        OpenMeteoResponse response = new OpenMeteoResponse();
        OpenMeteoResponse.Daily daily = new OpenMeteoResponse.Daily();
        daily.setTime(Arrays.asList("2024-01-01", "2024-01-02"));
        daily.setTemperatureMax(Arrays.asList(25.0, 26.0));
        daily.setTemperatureMin(Arrays.asList(15.0, 16.0));
        daily.setPrecipitationSum(Arrays.asList(0.0, 1.0));
        daily.setWindSpeedMax(Arrays.asList(10.0, 12.0));
        daily.setUvIndexMax(Arrays.asList(5.0, 6.0));
        response.setDaily(daily);

        when(openMeteoClient.getForecast(latitude, longitude, 2)).thenReturn(response);

        ClimateForecast mockForecast = ClimateForecast.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .forecastDate(LocalDate.of(2024, 1, 1))
                .temperatureMax(BigDecimal.valueOf(25.0))
                .build();

        ClimateForecastResponse mockResponse = ClimateForecastResponse.builder()
                .id(mockForecast.getId())
                .governorateId(governorateId)
                .forecastDate(LocalDate.of(2024, 1, 1))
                .temperatureMax(BigDecimal.valueOf(25.0))
                .build();

        when(climateForecastRepository.saveAll(any(List.class))).thenReturn(Arrays.asList(mockForecast));
        when(climateDataMapper.toResponse(mockForecast)).thenReturn(mockResponse);

        List<ClimateForecastResponse> result = climateDataService.getForecasts(governorateId, governorateName, latitude, longitude, 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(25.0), result.get(0).getTemperatureMax());
    }

    @Test
    void getHistoricalData_WhenValidRequest_ShouldReturnPageOfResponses() {
        ClimateDataQueryRequest request = ClimateDataQueryRequest.builder()
                .governorateId(governorateId)
                .startDate("2020-01-01")
                .endDate("2020-12-31")
                .build();

        Pageable pageable = Pageable.unpaged();

        HistoricalClimateData mockData = HistoricalClimateData.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .dateRecorded(LocalDate.of(2020, 6, 15))
                .temperatureAvg(BigDecimal.valueOf(28.5))
                .build();

        HistoricalClimateDataResponse mockResponse = HistoricalClimateDataResponse.builder()
                .id(mockData.getId())
                .governorateId(governorateId)
                .dateRecorded(LocalDate.of(2020, 6, 15))
                .temperatureAvg(BigDecimal.valueOf(28.5))
                .build();

        when(historicalClimateDataRepository.findByGovernorateAndDateRange(
                governorateId, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)))
                .thenReturn(Arrays.asList(mockData));
        when(climateDataMapper.toResponse(mockData)).thenReturn(mockResponse);

        Page<HistoricalClimateDataResponse> result = climateDataService.getHistoricalData(request, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BigDecimal.valueOf(28.5), result.getContent().get(0).getTemperatureAvg());
    }

    @Test
    void getHistoricalDataByYear_WhenValidRequest_ShouldReturnResponses() {
        List<HistoricalClimateData> mockDataList = Arrays.asList(
                HistoricalClimateData.builder()
                        .id(UUID.randomUUID())
                        .governorateId(governorateId)
                        .dateRecorded(LocalDate.of(2020, 1, 15))
                        .temperatureAvg(BigDecimal.valueOf(20.0))
                        .build()
        );

        HistoricalClimateDataResponse mockResponse = HistoricalClimateDataResponse.builder()
                .governorateId(governorateId)
                .dateRecorded(LocalDate.of(2020, 1, 15))
                .temperatureAvg(BigDecimal.valueOf(20.0))
                .build();

        when(historicalClimateDataRepository.findByGovernorateAndYear(governorateId, 2020))
                .thenReturn(mockDataList);
        when(climateDataMapper.toResponse(any(HistoricalClimateData.class))).thenReturn(mockResponse);

        List<HistoricalClimateDataResponse> result = climateDataService.getHistoricalDataByYear(governorateId, 2020);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(20.0), result.get(0).getTemperatureAvg());
    }

    @Test
    void getClimateDataByDate_WhenValidRequest_ShouldReturnResponses() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        ClimateData mockData = ClimateData.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .governorateName(governorateName)
                .dateRecorded(date)
                .temperature(BigDecimal.valueOf(22.5))
                .build();

        ClimateDataResponse mockResponse = ClimateDataResponse.builder()
                .id(mockData.getId())
                .governorateId(governorateId)
                .governorateName(governorateName)
                .dateRecorded(date)
                .temperature(BigDecimal.valueOf(22.5))
                .build();

        when(climateDataRepository.findByDate(date)).thenReturn(Arrays.asList(mockData));
        when(climateDataMapper.toResponse(mockData)).thenReturn(mockResponse);

        List<ClimateDataResponse> result = climateDataService.getClimateDataByDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(22.5), result.get(0).getTemperature());
    }
}
