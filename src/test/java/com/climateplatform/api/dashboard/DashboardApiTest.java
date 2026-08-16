package com.climateplatform.api.dashboard;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class DashboardApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HealthStatisticRepository healthStatisticRepository;

    @BeforeEach
    void setUp() {
        healthStatisticRepository.deleteAll();
    }

    @Test
    void getDashboardSummary_WhenDataExists_ShouldReturn200() throws Exception {
        HealthStatistic statistic = HealthStatistic.builder()
                .governorateId(UUID.randomUUID())
                .governorateName("Tunis")
                .statisticDate(LocalDateTime.now())
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .heatwaveRisk(1)
                .droughtRisk(0)
                .fireRisk(0)
                .climateRiskIndex(BigDecimal.valueOf(1.5))
                .populationAffected(100000L)
                .build();
        healthStatisticRepository.save(statistic);

        mockMvc.perform(get("/api/dashboard/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClimateDataPoints").value(1))
                .andExpect(jsonPath("$.avgTemperature").value(25.5));
    }

    @Test
    void getAllHealthStatistics_WhenDataExists_ShouldReturn200() throws Exception {
        HealthStatistic statistic = HealthStatistic.builder()
                .governorateId(UUID.randomUUID())
                .governorateName("Sousse")
                .statisticDate(LocalDateTime.now())
                .temperature(BigDecimal.valueOf(28.0))
                .humidity(BigDecimal.valueOf(60.0))
                .heatwaveRisk(2)
                .droughtRisk(1)
                .fireRisk(0)
                .climateRiskIndex(BigDecimal.valueOf(2.0))
                .build();
        healthStatisticRepository.save(statistic);

        mockMvc.perform(get("/api/dashboard/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].governorateName").value("Sousse"));
    }

    @Test
    void getRiskDistribution_WhenDataExists_ShouldReturn200() throws Exception {
        HealthStatistic statistic = HealthStatistic.builder()
                .governorateId(UUID.randomUUID())
                .governorateName("Tunis")
                .statisticDate(LocalDateTime.now())
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .heatwaveRisk(1)
                .droughtRisk(0)
                .fireRisk(0)
                .climateRiskIndex(BigDecimal.valueOf(1.5))
                .build();
        healthStatisticRepository.save(statistic);

        mockMvc.perform(get("/api/dashboard/risk-distribution")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].governorateName").value("Tunis"));
    }
}
