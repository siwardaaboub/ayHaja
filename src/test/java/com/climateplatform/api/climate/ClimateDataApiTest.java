package com.climateplatform.api.climate;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.repository.ClimateDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ClimateDataApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClimateDataRepository climateDataRepository;

    @BeforeEach
    void setUp() {
        climateDataRepository.deleteAll();
    }

    @Test
    void getCurrentWeather_WhenValidRequest_ShouldReturn200() throws Exception {
        UUID governorateId = UUID.randomUUID();
        ClimateData data = ClimateData.builder()
                .governorateId(governorateId)
                .governorateName("Tunis")
                .dateRecorded(LocalDate.now())
                .temperature(BigDecimal.valueOf(25.5))
                .humidity(BigDecimal.valueOf(65.0))
                .build();
        climateDataRepository.save(data);

        mockMvc.perform(get("/api/climate/current")
                        .param("governorateId", governorateId.toString())
                        .param("governorateName", "Tunis")
                        .param("latitude", "36.8025")
                        .param("longitude", "10.1839")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.governorateId").value(governorateId.toString()))
                .andExpect(jsonPath("$.temperature").value(25.5));
    }

    @Test
    void getCurrentWeather_WhenMissingParameters_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/climate/current")
                        .param("governorateName", "Tunis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
