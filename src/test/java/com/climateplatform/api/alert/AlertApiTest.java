package com.climateplatform.api.alert;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.alert.dto.request.CreateAlertRuleRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AlertApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @BeforeEach
    void setUp() {
        alertRuleRepository.deleteAll();
    }

    @Test
    void getAllAlertRules_WhenRulesExist_ShouldReturn200AndList() throws Exception {
        AlertRule rule = AlertRule.builder()
                .ruleName("Heatwave Alert")
                .alertType(AlertRule.AlertType.HEATWAVE)
                .conditionField("temperature")
                .operator(">")
                .thresholdValue(38.0)
                .severity(AlertRule.Severity.HIGH)
                .isActive(true)
                .build();
        alertRuleRepository.save(rule);

        mockMvc.perform(get("/api/alerts/rules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleName").value("Heatwave Alert"));
    }

    @Test
    void createAlertRule_WhenValidRequest_ShouldReturn200() throws Exception {
        String request = """
                {
                    "ruleName": "Test Rule",
                    "alertType": "HEATWAVE",
                    "conditionField": "temperature",
                    "operator": ">",
                    "thresholdValue": 38.0,
                    "severity": "HIGH",
                    "isActive": true
                }
                """;

        mockMvc.perform(post("/api/alerts/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruleName").value("Test Rule"))
                .andExpect(jsonPath("$.alertType").value("HEATWAVE"));
    }

    @Test
    void createAlertRule_WhenInvalidRequest_ShouldReturn400() throws Exception {
        String request = """
                {
                    "ruleName": "",
                    "alertType": "HEATWAVE"
                }
                """;

        mockMvc.perform(post("/api/alerts/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
