package com.climateplatform.api.map;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.map.domain.model.Governorate;
import com.climateplatform.map.domain.repository.GovernorateRepository;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class MapApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GovernorateRepository governorateRepository;

    @BeforeEach
    void setUp() {
        governorateRepository.deleteAll();
    }

    @Test
    void getAllGovernorates_WhenGovernoratesExist_ShouldReturn200() throws Exception {
        Governorate governorate = Governorate.builder()
                .name("Tunis")
                .nameAr("تونس")
                .centerLat(BigDecimal.valueOf(36.8025))
                .centerLon(BigDecimal.valueOf(10.1839))
                .population(1150000L)
                .areaKm2(BigDecimal.valueOf(1965.0))
                .build();
        governorateRepository.save(governorate);

        mockMvc.perform(get("/api/map/governorates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tunis"));
    }

    @Test
    void getGovernorateById_WhenExists_ShouldReturn200() throws Exception {
        Governorate governorate = Governorate.builder()
                .name("Ariana")
                .nameAr("أريانة")
                .centerLat(BigDecimal.valueOf(36.8625))
                .centerLon(BigDecimal.valueOf(10.0925))
                .population(900000L)
                .areaKm2(BigDecimal.valueOf(480.0))
                .build();
        Governorate saved = governorateRepository.save(governorate);

        mockMvc.perform(get("/api/map/governorates/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ariana"));
    }

    @Test
    void getGovernorateById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/map/governorates/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllActiveLayers_WhenLayersExist_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/map/layers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
