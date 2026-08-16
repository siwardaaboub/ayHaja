package com.climateplatform.dto.response.health;

import com.climateplatform.domain.model.health.HealthProfile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileResponse {
    private UUID id;
    private UUID userId;
    private Integer age;
    private Boolean hypertension;
    private Boolean asthma;
    private Boolean diabetes;
    private Boolean heartDisease;
    private Boolean pregnancy;
    private Boolean elderlyPerson;
    private Boolean childrenAtHome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HealthProfileResponse from(HealthProfile healthProfile) {
        return HealthProfileResponse.builder()
                .id(healthProfile.getId())
                .userId(healthProfile.getUser().getId())
                .age(healthProfile.getAge())
                .hypertension(healthProfile.getHypertension())
                .asthma(healthProfile.getAsthma())
                .diabetes(healthProfile.getDiabetes())
                .heartDisease(healthProfile.getHeartDisease())
                .pregnancy(healthProfile.getPregnancy())
                .elderlyPerson(healthProfile.getElderlyPerson())
                .childrenAtHome(healthProfile.getChildrenAtHome())
                .createdAt(healthProfile.getCreatedAt())
                .updatedAt(healthProfile.getUpdatedAt())
                .build();
    }
}
