package com.climateplatform.climate.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalApiUnavailableException extends RuntimeException {
    private final String apiName;
    private final String message;

    public ExternalApiUnavailableException(String apiName, String message) {
        super(String.format("External API unavailable - %s: %s", apiName, message));
        this.apiName = apiName;
        this.message = message;
    }
}
