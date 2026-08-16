package com.climateplatform.mapper;

import com.climateplatform.domain.model.User;
import com.climateplatform.domain.model.health.HealthProfile;
import com.climateplatform.dto.request.health.CreateHealthProfileRequest;
import com.climateplatform.dto.request.health.UpdateHealthProfileRequest;
import com.climateplatform.dto.request.user.UpdateUserRequest;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public void updateUserFromDto(UpdateUserRequest dto, User user) {
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
    }

    public HealthProfile toEntity(CreateHealthProfileRequest request, User user) {
        return HealthProfile.builder()
                .user(user)
                .age(request.getAge())
                .hypertension(request.getHypertension())
                .asthma(request.getAsthma())
                .diabetes(request.getDiabetes())
                .heartDisease(request.getHeartDisease())
                .pregnancy(request.getPregnancy())
                .elderlyPerson(request.getElderlyPerson())
                .childrenAtHome(request.getChildrenAtHome())
                .build();
    }

    public void updateEntityFromDto(UpdateHealthProfileRequest dto, HealthProfile healthProfile) {
        if (dto.getAge() != null) {
            healthProfile.setAge(dto.getAge());
        }
        if (dto.getHypertension() != null) {
            healthProfile.setHypertension(dto.getHypertension());
        }
        if (dto.getAsthma() != null) {
            healthProfile.setAsthma(dto.getAsthma());
        }
        if (dto.getDiabetes() != null) {
            healthProfile.setDiabetes(dto.getDiabetes());
        }
        if (dto.getHeartDisease() != null) {
            healthProfile.setHeartDisease(dto.getHeartDisease());
        }
        if (dto.getPregnancy() != null) {
            healthProfile.setPregnancy(dto.getPregnancy());
        }
        if (dto.getElderlyPerson() != null) {
            healthProfile.setElderlyPerson(dto.getElderlyPerson());
        }
        if (dto.getChildrenAtHome() != null) {
            healthProfile.setChildrenAtHome(dto.getChildrenAtHome());
        }
    }
}
