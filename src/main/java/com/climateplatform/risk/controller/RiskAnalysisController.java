package com.climateplatform.risk.controller;

import com.climateplatform.risk.domain.service.RiskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskAnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, String>> calculateRisks() {
        riskAnalysisService.calculateRisksForToday();

        return ResponseEntity.ok(
                Map.of("message", "Risk analysis completed successfully")
        );
    }
}