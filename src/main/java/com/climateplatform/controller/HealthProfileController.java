package com.climateplatform.controller;

import com.climateplatform.domain.service.health.HealthProfileService;
import com.climateplatform.dto.request.health.CreateHealthProfileRequest;
import com.climateplatform.dto.request.health.UpdateHealthProfileRequest;
import com.climateplatform.dto.response.health.HealthProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/health-profiles")
@RequiredArgsConstructor
public class HealthProfileController {
    private final HealthProfileService healthProfileService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<HealthProfileResponse> getHealthProfileByUserId(@PathVariable UUID userId) {
        HealthProfileResponse healthProfile = healthProfileService.getHealthProfileByUserId(userId);
        return ResponseEntity.ok(healthProfile);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<HealthProfileResponse> createHealthProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateHealthProfileRequest request) {
        HealthProfileResponse healthProfile = healthProfileService.createHealthProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(healthProfile);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<HealthProfileResponse> updateHealthProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateHealthProfileRequest request) {
        HealthProfileResponse healthProfile = healthProfileService.updateHealthProfile(userId, request);
        return ResponseEntity.ok(healthProfile);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteHealthProfile(@PathVariable UUID userId) {
        healthProfileService.deleteHealthProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
