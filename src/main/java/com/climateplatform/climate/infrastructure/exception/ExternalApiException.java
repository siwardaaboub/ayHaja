package com.climateplatform.climate.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ExternalApiException extends RuntimeException {
    private final String apiName;
    private final String message;

    public ExternalApiException(String apiName, String message) {
        super(String.format("External API error - %s: %s", apiName, message));
        this.apiName = apiName;
        this.message = message;
    }
}
