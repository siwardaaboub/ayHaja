package com.climateplatform.domain.service.health;

import com.climateplatform.domain.model.User;
import com.climateplatform.domain.model.health.HealthProfile;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.domain.repository.health.HealthProfileRepository;
import com.climateplatform.dto.request.health.CreateHealthProfileRequest;
import com.climateplatform.dto.request.health.UpdateHealthProfileRequest;
import com.climateplatform.dto.response.health.HealthProfileResponse;
import com.climateplatform.exception.ResourceNotFoundException;
import com.climateplatform.exception.ResourceAlreadyExistsException;
import com.climateplatform.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthProfileService {
    private final HealthProfileRepository healthProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    public HealthProfileResponse getHealthProfileByUserId(UUID userId) {
        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HealthProfile", "userId", userId));
        return HealthProfileResponse.from(healthProfile);
    }

    @Transactional
    public HealthProfileResponse createHealthProfile(UUID userId, CreateHealthProfileRequest request) {
        if (healthProfileRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Health profile", "userId", userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        HealthProfile healthProfile = userProfileMapper.toEntity(request, user);
        HealthProfile savedProfile = healthProfileRepository.save(healthProfile);

        return HealthProfileResponse.from(savedProfile);
    }

    @Transactional
    public HealthProfileResponse updateHealthProfile(UUID userId, UpdateHealthProfileRequest request) {
        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HealthProfile", "userId", userId));

        userProfileMapper.updateEntityFromDto(request, healthProfile);
        HealthProfile updatedProfile = healthProfileRepository.save(healthProfile);

        return HealthProfileResponse.from(updatedProfile);
    }

    @Transactional
    public void deleteHealthProfile(UUID userId) {
        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HealthProfile", "userId", userId));
        healthProfileRepository.delete(healthProfile);
    }
}
