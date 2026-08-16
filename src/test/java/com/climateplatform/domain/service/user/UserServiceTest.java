package com.climateplatform.domain.service.user;

import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.dto.request.user.UpdateUserRequest;
import com.climateplatform.dto.response.user.UserResponse;
import com.climateplatform.exception.ResourceNotFoundException;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .build();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
    }

    @Test
    void updateUserProfile_WhenValidRequest_ShouldReturnUpdatedUser() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role(User.Role.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userService.updateUserProfile(userId, request);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfile_WhenUserNotFound_ShouldThrowException() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUserProfile(userId, request)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(userId);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(userId)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }
}
