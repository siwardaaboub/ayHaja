package com.climateplatform.map.controller;

import com.climateplatform.map.domain.service.MapService;
import com.climateplatform.map.dto.response.GovernorateDetailResponse;
import com.climateplatform.map.dto.response.GovernorateResponse;
import com.climateplatform.map.dto.response.MapLayerResponse;
import com.climateplatform.map.dto.response.MapMarkerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @GetMapping("/governorates")
    public ResponseEntity<List<GovernorateResponse>> getAllGovernorates() {
        List<GovernorateResponse> governorates = mapService.getAllGovernorates();
        return ResponseEntity.ok(governorates);
    }

    @GetMapping("/governorates/with-geometry")
    public ResponseEntity<List<GovernorateResponse>> getAllGovernoratesWithGeometry() {
        List<GovernorateResponse> governorates = mapService.getAllGovernoratesWithGeometry();
        return ResponseEntity.ok(governorates);
    }

    @GetMapping("/governorates/{id}")
    public ResponseEntity<GovernorateResponse> getGovernorateById(@PathVariable UUID id) {
        GovernorateResponse governorate = mapService.getGovernorateById(id);
        return ResponseEntity.ok(governorate);
    }

    @GetMapping("/governorates/{id}/detail")
    public ResponseEntity<GovernorateDetailResponse> getGovernorateDetail(@PathVariable UUID id) {
        GovernorateDetailResponse detail = mapService.getGovernorateDetail(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/governorates/by-name/{name}")
    public ResponseEntity<GovernorateResponse> getGovernorateByName(@PathVariable String name) {
        GovernorateResponse governorate = mapService.getGovernorateByName(name);
        return ResponseEntity.ok(governorate);
    }

    @GetMapping("/layers")
    public ResponseEntity<List<MapLayerResponse>> getAllActiveLayers() {
        List<MapLayerResponse> layers = mapService.getAllActiveLayers();
        return ResponseEntity.ok(layers);
    }

    @GetMapping("/markers")
    public ResponseEntity<List<MapMarkerResponse>> getAllActiveMarkers() {
        List<MapMarkerResponse> markers = mapService.getAllActiveMarkers();
        return ResponseEntity.ok(markers);
    }

    @GetMapping("/markers/governorate/{governorateId}")
    public ResponseEntity<List<MapMarkerResponse>> getMarkersByGovernorate(@PathVariable UUID governorateId) {
        List<MapMarkerResponse> markers = mapService.getMarkersByGovernorate(governorateId);
        return ResponseEntity.ok(markers);
    }
}
