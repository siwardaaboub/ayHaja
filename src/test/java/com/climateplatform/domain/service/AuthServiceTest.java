package com.climateplatform.domain.service;

import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.dto.request.LoginRequest;
import com.climateplatform.dto.request.RegisterRequest;
import com.climateplatform.dto.response.AuthResponse;
import com.climateplatform.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        validLoginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .build();
    }

    @Test
    void register_WhenValidRequest_ShouldReturnAuthResponse() {
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        AuthResponse response = authService.register(validRegisterRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(validRegisterRequest)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        AuthResponse response = authService.login(validLoginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(validLoginRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }
}
