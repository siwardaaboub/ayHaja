package com.climateplatform.climate.controller;

import com.climateplatform.climate.dto.request.ClimateDataQueryRequest;
import com.climateplatform.climate.dto.response.ClimateDataResponse;
import com.climateplatform.climate.dto.response.ClimateForecastResponse;
import com.climateplatform.climate.dto.response.HistoricalClimateDataResponse;
import com.climateplatform.climate.domain.service.ClimateDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/climate")
@RequiredArgsConstructor
public class ClimateDataController {
    private final ClimateDataService climateDataService;

    @GetMapping("/current")
    public ResponseEntity<ClimateDataResponse> getCurrentWeather(
            @RequestParam UUID governorateId,
            @RequestParam String governorateName,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        ClimateDataResponse response = climateDataService.getCurrentWeather(
                governorateId, governorateName, latitude, longitude);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forecasts")
    public ResponseEntity<List<ClimateForecastResponse>> getForecasts(
            @RequestParam UUID governorateId,
            @RequestParam String governorateName,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "7") int days) {
        List<ClimateForecastResponse> response = climateDataService.getForecasts(
                governorateId, governorateName, latitude, longitude, days);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historical")
    public ResponseEntity<Page<HistoricalClimateDataResponse>> getHistoricalData(
            @Valid ClimateDataQueryRequest request,
            @PageableDefault(size = 100) Pageable pageable) {
        Page<HistoricalClimateDataResponse> response = climateDataService.getHistoricalData(request, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historical/year/{year}")
    public ResponseEntity<List<HistoricalClimateDataResponse>> getHistoricalDataByYear(
            @RequestParam UUID governorateId,
            @PathVariable int year) {
        List<HistoricalClimateDataResponse> response = climateDataService.getHistoricalDataByYear(governorateId, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historical/month")
    public ResponseEntity<List<HistoricalClimateDataResponse>> getHistoricalDataByMonth(
            @RequestParam UUID governorateId,
            @RequestParam int year,
            @RequestParam int month) {
        List<HistoricalClimateDataResponse> response = climateDataService.getHistoricalDataByMonth(governorateId, year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<ClimateDataResponse>> getClimateDataByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ClimateDataResponse> response = climateDataService.getClimateDataByDate(date);
        return ResponseEntity.ok(response);
    }
}
