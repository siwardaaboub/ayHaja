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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthProfileServiceTest {

    @Mock
    private HealthProfileRepository healthProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private HealthProfileService healthProfileService;

    private User testUser;
    private HealthProfile testHealthProfile;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testHealthProfile = HealthProfile.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .age(30)
                .hypertension(true)
                .asthma(false)
                .diabetes(false)
                .heartDisease(false)
                .pregnancy(false)
                .elderlyPerson(false)
                .childrenAtHome(false)
                .build();
    }

    @Test
    void getHealthProfileByUserId_WhenProfileExists_ShouldReturnProfile() {
        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.of(testHealthProfile));

        HealthProfileResponse response = healthProfileService.getHealthProfileByUserId(userId);

        assertNotNull(response);
        assertEquals(30, response.getAge());
        assertTrue(response.getHypertension());
    }

    @Test
    void getHealthProfileByUserId_WhenProfileNotFound_ShouldThrowException() {
        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> healthProfileService.getHealthProfileByUserId(userId)
        );

        assertEquals("HealthProfile not found with userId : '" + userId + "'", exception.getMessage());
    }

    @Test
    void createHealthProfile_WhenValidRequest_ShouldCreateProfile() {
        CreateHealthProfileRequest request = CreateHealthProfileRequest.builder()
                .age(30)
                .hypertension(true)
                .asthma(false)
                .diabetes(false)
                .heartDisease(false)
                .pregnancy(false)
                .elderlyPerson(false)
                .childrenAtHome(false)
                .build();

        when(healthProfileRepository.existsByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userProfileMapper.toEntity(any(CreateHealthProfileRequest.class), any(User.class)))
                .thenReturn(testHealthProfile);
        when(healthProfileRepository.save(any(HealthProfile.class))).thenReturn(testHealthProfile);

        HealthProfileResponse response = healthProfileService.createHealthProfile(userId, request);

        assertNotNull(response);
        assertEquals(30, response.getAge());
        verify(healthProfileRepository).save(any(HealthProfile.class));
    }

    @Test
    void createHealthProfile_WhenProfileAlreadyExists_ShouldThrowException() {
        CreateHealthProfileRequest request = CreateHealthProfileRequest.builder()
                .age(30)
                .hypertension(true)
                .asthma(false)
                .diabetes(false)
                .heartDisease(false)
                .pregnancy(false)
                .elderlyPerson(false)
                .childrenAtHome(false)
                .build();

        when(healthProfileRepository.existsByUserId(userId)).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> healthProfileService.createHealthProfile(userId, request)
        );

        assertEquals("Health profile already exists with userId : '" + userId + "'", exception.getMessage());
        verify(healthProfileRepository, never()).save(any(HealthProfile.class));
    }

    @Test
    void createHealthProfile_WhenUserNotFound_ShouldThrowException() {
        CreateHealthProfileRequest request = CreateHealthProfileRequest.builder()
                .age(30)
                .hypertension(true)
                .asthma(false)
                .diabetes(false)
                .heartDisease(false)
                .pregnancy(false)
                .elderlyPerson(false)
                .childrenAtHome(false)
                .build();

        when(healthProfileRepository.existsByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> healthProfileService.createHealthProfile(userId, request)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
    }

    @Test
    void updateHealthProfile_WhenValidRequest_ShouldUpdateProfile() {
        UpdateHealthProfileRequest request = UpdateHealthProfileRequest.builder()
                .age(35)
                .build();

        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.of(testHealthProfile));
        when(healthProfileRepository.save(any(HealthProfile.class))).thenReturn(testHealthProfile);

        HealthProfileResponse response = healthProfileService.updateHealthProfile(userId, request);

        assertNotNull(response);
        verify(healthProfileRepository).save(any(HealthProfile.class));
    }

    @Test
    void updateHealthProfile_WhenProfileNotFound_ShouldThrowException() {
        UpdateHealthProfileRequest request = UpdateHealthProfileRequest.builder()
                .age(35)
                .build();

        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> healthProfileService.updateHealthProfile(userId, request)
        );

        assertEquals("HealthProfile not found with userId : '" + userId + "'", exception.getMessage());
    }

    @Test
    void deleteHealthProfile_WhenProfileExists_ShouldDeleteProfile() {
        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.of(testHealthProfile));

        healthProfileService.deleteHealthProfile(userId);

        verify(healthProfileRepository).delete(testHealthProfile);
    }

    @Test
    void deleteHealthProfile_WhenProfileNotFound_ShouldThrowException() {
        when(healthProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> healthProfileService.deleteHealthProfile(userId)
        );

        assertEquals("HealthProfile not found with userId : '" + userId + "'", exception.getMessage());
        verify(healthProfileRepository, never()).delete(any(HealthProfile.class));
    }
}
