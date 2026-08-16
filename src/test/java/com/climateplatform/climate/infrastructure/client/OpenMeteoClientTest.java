package com.climateplatform.climate.infrastructure.client;

import com.climateplatform.climate.infrastructure.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenMeteoClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private OpenMeteoClient openMeteoClient;

    @BeforeEach
    void setUp() {
        openMeteoClient = new OpenMeteoClient(restTemplate, objectMapper, 10);
    }

    @Test
    void getCurrentWeather_WhenValidRequest_ShouldCallApi() throws Exception {
        BigDecimal latitude = BigDecimal.valueOf(36.8025);
        BigDecimal longitude = BigDecimal.valueOf(10.1839);

        String mockResponse = "{\"current\":{\"temperature\":25.5,\"feels_like\":27.0}}";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        OpenMeteoResponse.Current current = new OpenMeteoResponse.Current();
        current.setTemperature(25.5);
        current.setFeelsLike(27.0);

        OpenMeteoResponse response = new OpenMeteoResponse();
        response.setCurrent(current);

        when(objectMapper.readValue(mockResponse, OpenMeteoResponse.class))
                .thenReturn(response);

        OpenMeteoResponse result = openMeteoClient.getCurrentWeather(latitude, longitude);

        assertNotNull(result);
        assertEquals(25.5, result.getCurrent().getTemperature());
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getCurrentWeather_WhenApiFails_ShouldThrowExternalApiException() {
        BigDecimal latitude = BigDecimal.valueOf(36.8025);
        BigDecimal longitude = BigDecimal.valueOf(10.1839);

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> openMeteoClient.getCurrentWeather(latitude, longitude)
        );

        assertTrue(exception.getMessage().contains("Open-Meteo"));
    }

    @Test
    void getForecast_WhenValidRequest_ShouldCallApi() throws Exception {
        BigDecimal latitude = BigDecimal.valueOf(36.8025);
        BigDecimal longitude = BigDecimal.valueOf(10.1839);
        int days = 7;

        String mockResponse = "{\"daily\":{\"time\":[\"2024-01-01\"],\"temperature_2m_max\":[25.0]}}";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        OpenMeteoResponse.Daily daily = new OpenMeteoResponse.Daily();
        daily.setTime(java.util.Arrays.asList("2024-01-01"));
        daily.setTemperatureMax(java.util.Arrays.asList(25.0));

        OpenMeteoResponse response = new OpenMeteoResponse();
        response.setDaily(daily);

        when(objectMapper.readValue(mockResponse, OpenMeteoResponse.class))
                .thenReturn(response);

        OpenMeteoResponse result = openMeteoClient.getForecast(latitude, longitude, days);

        assertNotNull(result);
        assertEquals(1, result.getDaily().getTime().size());
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }
}
