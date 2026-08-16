package com.climateplatform.dto.request.health;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHealthProfileRequest {
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 150, message = "Age must not exceed 150")
    private Integer age;

    private Boolean hypertension;

    private Boolean asthma;

    private Boolean diabetes;

    private Boolean heartDisease;

    private Boolean pregnancy;

    private Boolean elderlyPerson;

    private Boolean childrenAtHome;
}
