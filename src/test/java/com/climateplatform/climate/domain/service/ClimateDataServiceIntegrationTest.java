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
class ClimateDataServiceIntegrationTest {

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

    @BeforeEach
    void setUp() {
        governorateId = UUID.randomUUID();
        governorateName = "Tunis";
    }

    @Test
    void getHistoricalData_WhenValidRequest_ShouldReturnPage() {
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

        when(historicalClimateDataRepository.findByGovernorateAndDateRange(
                governorateId, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)))
                .thenReturn(Arrays.asList(mockData));

        HistoricalClimateDataResponse mockResponse = HistoricalClimateDataResponse.builder()
                .id(mockData.getId())
                .governorateId(governorateId)
                .dateRecorded(LocalDate.of(2020, 6, 15))
                .temperatureAvg(BigDecimal.valueOf(28.5))
                .build();

        when(climateDataMapper.toResponse(mockData)).thenReturn(mockResponse);

        Page<HistoricalClimateDataResponse> result = climateDataService.getHistoricalData(request, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BigDecimal.valueOf(28.5), result.getContent().get(0).getTemperatureAvg());
    }

    @Test
    void getClimateDataByDate_WhenDataExists_ShouldReturnResponses() {
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

    @Test
    void refreshHistoricalData_WhenValidRequest_ShouldRefreshData() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 1, 31);

        OpenMeteoClient.HistoricalDataPoint dataPoint = OpenMeteoClient.HistoricalDataPoint.builder()
                .date(LocalDate.of(2020, 1, 15))
                .temperatureAvg(BigDecimal.valueOf(20.0))
                .temperatureMax(BigDecimal.valueOf(25.0))
                .temperatureMin(BigDecimal.valueOf(15.0))
                .precipitation(BigDecimal.valueOf(0.0))
                .windSpeed(BigDecimal.valueOf(10.0))
                .humidity(BigDecimal.valueOf(60.0))
                .uvIndex(BigDecimal.valueOf(5.0))
                .build();

        when(openMeteoClient.getHistoricalData(any(BigDecimal.class), any(BigDecimal.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(dataPoint));

        climateDataService.refreshHistoricalData(governorateId, governorateName,
                BigDecimal.valueOf(36.8025), BigDecimal.valueOf(10.1839), startDate, endDate);

        verify(historicalClimateDataRepository).deleteByGovernorateId(governorateId);
        verify(historicalClimateDataRepository).saveAll(anyList());
    }
}
