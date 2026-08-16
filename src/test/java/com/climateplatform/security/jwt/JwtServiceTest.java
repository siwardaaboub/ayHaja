package com.climateplatform.security.jwt;

import com.climateplatform.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the secret field using reflection
        try {
            java.lang.reflect.Field secretField = JwtService.class.getDeclaredField("jwtSecret");
            secretField.setAccessible(true);
            secretField.set(jwtService, "YourSecretKeyForJwtTokenGenerationMustBeAtLeast256BitsLong");
        } catch (Exception e) {
            e.printStackTrace();
        }

        testUser = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "password",
                Collections.emptyList()
        );
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldReturnEmail() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtService.generateToken(testUser);
        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidUsername_ShouldReturnFalse() {
        String token = jwtService.generateToken(testUser);
        UserDetails invalidUserDetails = new org.springframework.security.core.userdetails.User(
                "invalid@example.com",
                "password",
                Collections.emptyList()
        );

        boolean isValid = jwtService.validateToken(token, invalidUserDetails);
        assertFalse(isValid);
    }
}
