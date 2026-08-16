package com.climateplatform.integration;

import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.alert.domain.service.AlertService;
import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.repository.ClimateDataRepository;
import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.map.domain.model.Governorate;
import com.climateplatform.map.domain.repository.GovernorateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class DatabaseIntegrationTest {

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @Autowired
    private ClimateDataRepository climateDataRepository;

    @Autowired
    private HealthStatisticRepository healthStatisticRepository;

    @Autowired
    private GovernorateRepository governorateRepository;

    @BeforeEach
    void setUp() {
        alertRuleRepository.deleteAll();
        climateDataRepository.deleteAll();
        healthStatisticRepository.deleteAll();
        governorateRepository.deleteAll();
    }

    @Test
    void saveAndRetrieveAlertRule_ShouldPersistAndRetrieve() {
        AlertRule rule = AlertRule.builder()
                .ruleName("Heatwave Alert")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .conditionField("temperature")
                .operator(">")
                .thresholdValue(38.0)
                .durationMinutes(60)
                .severity(AlertRule.Severity.HIGH)
                .isActive(true)
                .cooldownMinutes(60)
                .build();

        AlertRule saved = alertRuleRepository.save(rule);
        assertNotNull(saved.getId());

        var retrieved = alertRuleRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Heatwave Alert", retrieved.get().getRuleName());
    }

    @Test
    void saveAndRetrieveClimateData_ShouldPersistAndRetrieve() {
        UUID governorateId = UUID.randomUUID();

        ClimateData data = ClimateData.builder()
                .governorateId(governorateId)
                .governorateName("Tunis")
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .build();

        ClimateData saved = climateDataRepository.save(data);
        assertNotNull(saved.getId());

        var retrieved = climateDataRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Tunis", retrieved.get().getGovernorateName());
    }

    @Test
    void saveAndRetrieveHealthStatistic_ShouldPersistAndRetrieve() {
        UUID governorateId = UUID.randomUUID();

        HealthStatistic statistic = HealthStatistic.builder()
                .governorateId(governorateId)
                .governorateName("Tunis")
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .heatwaveRisk(1)
                .droughtRisk(0)
                .fireRisk(0)
                .climateRiskIndex(BigDecimal.valueOf(1.5))
                .build();

        HealthStatistic saved = healthStatisticRepository.save(statistic);
        assertNotNull(saved.getId());

        var retrieved = healthStatisticRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Tunis", retrieved.get().getGovernorateName());
    }

    @Test
    void saveAndRetrieveGovernorate_ShouldPersistAndRetrieve() {
        Governorate governorate = Governorate.builder()
                .name("Tunis")
                .nameAr("تونس")
                .centerLat(BigDecimal.valueOf(36.8025))
                .centerLon(BigDecimal.valueOf(10.1839))
                .population(1150000L)
                .areaKm2(BigDecimal.valueOf(1965.0))
                .build();

        Governorate saved = governorateRepository.save(governorate);
        assertNotNull(saved.getId());

        var retrieved = governorateRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Tunis", retrieved.get().getName());
    }
}
