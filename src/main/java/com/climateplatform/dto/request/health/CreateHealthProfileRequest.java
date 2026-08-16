package com.climateplatform.dto.request.health;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHealthProfileRequest {
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 150, message = "Age must not exceed 150")
    private Integer age;

    @NotNull(message = "Hypertension status is required")
    private Boolean hypertension;

    @NotNull(message = "Asthma status is required")
    private Boolean asthma;

    @NotNull(message = "Diabetes status is required")
    private Boolean diabetes;

    @NotNull(message = "Heart disease status is required")
    private Boolean heartDisease;

    @NotNull(message = "Pregnancy status is required")
    private Boolean pregnancy;

    @NotNull(message = "Elderly person status is required")
    private Boolean elderlyPerson;

    @NotNull(message = "Children at home status is required")
    private Boolean childrenAtHome;
}
