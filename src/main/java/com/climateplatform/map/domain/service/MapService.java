package com.climateplatform.map.domain.service;

import com.climateplatform.climate.domain.service.ClimateDataService;
import com.climateplatform.climate.dto.request.ClimateDataQueryRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapService {
    private final GovernorateRepository governorateRepository;
    private final MapLayerRepository mapLayerRepository;
    private final MapMarkerRepository mapMarkerRepository;
    private final ClimateDataService climateDataService;

    @Cacheable("governorates")
    public List<GovernorateResponse> getAllGovernorates() {
        return governorateRepository.findAllOrderedByName().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable("governorates")
    public GovernorateResponse getGovernorateById(UUID id) {
        Governorate governorate = governorateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Governorate", "id", id));
        return toResponse(governorate);
    }

    @Cacheable("governorates")
    public GovernorateResponse getGovernorateByName(String name) {
        Governorate governorate = governorateRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Governorate", "name", name));
        return toResponse(governorate);
    }

    @Cacheable("governorates-with-geometry")
    public List<GovernorateResponse> getAllGovernoratesWithGeometry() {
        List<Object[]> results = governorateRepository.findAllWithGeometry();
        return results.stream()
                .map(result -> GovernorateResponse.builder()
                        .id((UUID) result[0])
                        .name((String) result[1])
                        .nameAr((String) result[2])
                        .centerLat((java.math.BigDecimal) result[3])
                        .centerLon((java.math.BigDecimal) result[4])
                        .geometry((String) result[5])
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable("governorates-with-geometry")
    public GovernorateResponse getGovernorateWithGeometryById(UUID id) {
        Object[] result = governorateRepository.findWithGeometryById(id);
        if (result == null) {
            throw new ResourceNotFoundException("Governorate", "id", id);
        }
        return GovernorateResponse.builder()
                .id((UUID) result[0])
                .name((String) result[1])
                .nameAr((String) result[2])
                .centerLat((java.math.BigDecimal) result[3])
                .centerLon((java.math.BigDecimal) result[4])
                .geometry((String) result[5])
                .build();
    }

    public GovernorateDetailResponse getGovernorateDetail(UUID id) {
        GovernorateResponse governorate = getGovernorateById(id);

        ClimateDataResponse currentWeather = climateDataService.getCurrentWeather(
                id, governorate.getName(), governorate.getCenterLat(), governorate.getCenterLon());

        List<ClimateForecastResponse> forecasts = climateDataService.getForecasts(
                id, governorate.getName(), governorate.getCenterLat(), governorate.getCenterLon(), 7);

        return GovernorateDetailResponse.builder()
                .governorate(governorate)
                .currentWeather(currentWeather)
                .forecasts(forecasts)
                .build();
    }

    public List<MapLayerResponse> getAllActiveLayers() {
        return mapLayerRepository.findByIsActiveTrueOrderByZIndex().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MapMarkerResponse> getMarkersByGovernorate(UUID governorateId) {
        return mapMarkerRepository.findByGovernorateId(governorateId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MapMarkerResponse> getAllActiveMarkers() {
        return mapMarkerRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private GovernorateResponse toResponse(Governorate governorate) {
        return GovernorateResponse.builder()
                .id(governorate.getId())
                .name(governorate.getName())
                .nameAr(governorate.getNameAr())
                .centerLat(governorate.getCenterLat())
                .centerLon(governorate.getCenterLon())
                .population(governorate.getPopulation())
                .areaKm2(governorate.getAreaKm2())
                .build();
    }

    private MapLayerResponse toResponse(MapLayer layer) {
        return MapLayerResponse.builder()
                .id(layer.getId())
                .name(layer.getName())
                .displayName(layer.getDisplayName())
                .layerType(layer.getLayerType())
                .urlTemplate(layer.getUrlTemplate())
                .attribution(layer.getAttribution())
                .isActive(layer.getIsActive())
                .zIndex(layer.getZIndex())
                .opacity(layer.getOpacity())
                .build();
    }

    private MapMarkerResponse toResponse(MapMarker marker) {
        return MapMarkerResponse.builder()
                .id(marker.getId())
                .governorateId(marker.getGovernorateId())
                .latitude(marker.getLatitude())
                .longitude(marker.getLongitude())
                .markerType(marker.getMarkerType())
                .title(marker.getTitle())
                .description(marker.getDescription())
                .popupContent(marker.getPopupContent())
                .iconUrl(marker.getIconUrl())
                .isActive(marker.getIsActive())
                .build();
    }
}
