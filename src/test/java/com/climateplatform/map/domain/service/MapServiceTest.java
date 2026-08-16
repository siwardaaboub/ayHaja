package com.climateplatform.map.domain.service;

import com.climateplatform.climate.domain.service.ClimateDataService;
import com.climateplatform.climate.dto.response.ClimateDataResponse;
import com.climateplatform.climate.dto.response.ClimateForecastResponse;
import com.climateplatform.map.domain.model.Governorate;
import com.climateplatform.map.domain.model.MapLayer;
import com.climateplatform.map.domain.model.MapMarker;
import com.climateplatform.map.domain.repository.GovernorateRepository;
import com.climateplatform.map.domain.repository.MapLayerRepository;
import com.climateplatform.map.domain.repository.MapMarkerRepository;
import com.climateplatform.map.dto.response.GovernorateDetailResponse;
import com.climateplatform.map.dto.response.GovernorateResponse;
import com.climateplatform.map.dto.response.MapLayerResponse;
import com.climateplatform.map.dto.response.MapMarkerResponse;
import com.climateplatform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private GovernorateRepository governorateRepository;

    @Mock
    private MapLayerRepository mapLayerRepository;

    @Mock
    private MapMarkerRepository mapMarkerRepository;

    @Mock
    private ClimateDataService climateDataService;

    @InjectMocks
    private MapService mapService;

    private Governorate testGovernorate;
    private UUID governorateId;

    @BeforeEach
    void setUp() {
        governorateId = UUID.randomUUID();
        testGovernorate = Governorate.builder()
                .id(governorateId)
                .name("Tunis")
                .nameAr("تونس")
                .centerLat(BigDecimal.valueOf(36.8025))
                .centerLon(BigDecimal.valueOf(10.1839))
                .population(1150000L)
                .areaKm2(BigDecimal.valueOf(1965.0))
                .build();
    }

    @Test
    void getAllGovernorates_WhenGovernoratesExist_ShouldReturnResponses() {
        List<Governorate> governorates = Arrays.asList(testGovernorate);
        when(governorateRepository.findAllOrderedByName()).thenReturn(governorates);

        List<GovernorateResponse> result = mapService.getAllGovernorates();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tunis", result.get(0).getName());
    }

    @Test
    void getGovernorateById_WhenExists_ShouldReturnResponse() {
        when(governorateRepository.findById(governorateId)).thenReturn(Optional.of(testGovernorate));

        GovernorateResponse result = mapService.getGovernorateById(governorateId);

        assertNotNull(result);
        assertEquals(governorateId, result.getId());
        assertEquals("Tunis", result.getName());
    }

    @Test
    void getGovernorateById_WhenNotFound_ShouldThrowException() {
        when(governorateRepository.findById(governorateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> mapService.getGovernorateById(governorateId)
        );

        assertEquals("Governorate not found with id : '" + governorateId + "'", exception.getMessage());
    }

    @Test
    void getGovernorateByName_WhenExists_ShouldReturnResponse() {
        when(governorateRepository.findByName("Tunis")).thenReturn(Optional.of(testGovernorate));

        GovernorateResponse result = mapService.getGovernorateByName("Tunis");

        assertNotNull(result);
        assertEquals("Tunis", result.getName());
    }

    @Test
    void getAllActiveLayers_WhenLayersExist_ShouldReturnResponses() {
        MapLayer layer = MapLayer.builder()
                .id(UUID.randomUUID())
                .name("satellite")
                .displayName("Satellite View")
                .layerType("tile")
                .isActive(true)
                .zIndex(1)
                .build();

        when(mapLayerRepository.findByIsActiveTrueOrderByZIndex()).thenReturn(Arrays.asList(layer));

        List<MapLayerResponse> result = mapService.getAllActiveLayers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("satellite", result.get(0).getName());
    }

    @Test
    void getMarkersByGovernorate_WhenMarkersExist_ShouldReturnResponses() {
        MapMarker marker = MapMarker.builder()
                .id(UUID.randomUUID())
                .governorateId(governorateId)
                .latitude(BigDecimal.valueOf(36.8025))
                .longitude(BigDecimal.valueOf(10.1839))
                .markerType("climate")
                .title("Tunis Marker")
                .isActive(true)
                .build();

        when(mapMarkerRepository.findByGovernorateId(governorateId)).thenReturn(Arrays.asList(marker));

        List<MapMarkerResponse> result = mapService.getMarkersByGovernorate(governorateId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tunis Marker", result.get(0).getTitle());
    }

    @Test
    void getGovernorateDetail_WhenValidId_ShouldReturnDetail() {
        when(governorateRepository.findById(governorateId)).thenReturn(Optional.of(testGovernorate));

        ClimateDataResponse mockWeather = ClimateDataResponse.builder()
                .governorateId(governorateId)
                .temperature(BigDecimal.valueOf(25.5))
                .build();

        ClimateForecastResponse mockForecast = ClimateForecastResponse.builder()
                .governorateId(governorateId)
                .temperatureMax(BigDecimal.valueOf(28.0))
                .build();

        when(climateDataService.getCurrentWeather(any(UUID.class), any(String.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockWeather);
        when(climateDataService.getForecasts(any(UUID.class), any(String.class), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                .thenReturn(Arrays.asList(mockForecast));

        GovernorateDetailResponse result = mapService.getGovernorateDetail(governorateId);

        assertNotNull(result);
        assertNotNull(result.getGovernorate());
        assertNotNull(result.getCurrentWeather());
        assertNotNull(result.getForecasts());
        assertEquals("Tunis", result.getGovernorate().getName());
    }
}
