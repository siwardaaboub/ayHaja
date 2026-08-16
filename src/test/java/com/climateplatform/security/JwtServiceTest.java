package com.climateplatform.security;

import com.climateplatform.domain.model.User;
import com.climateplatform.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

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
                .firstName("Test")
                .lastName("User")
                .passwordHash("password")
                .role(User.Role.USER)
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void extractUsername_WhenValidToken_ShouldReturnUsername() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_WhenValidToken_ShouldReturnTrue() {
        String token = jwtService.generateToken(testUser);
        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_WhenInvalidUsername_ShouldReturnFalse() {
        String token = jwtService.generateToken(testUser);
        UserDetails invalidUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("invalid@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        boolean isValid = jwtService.validateToken(token, invalidUserDetails);

        assertFalse(isValid);
    }

    @Test
    void extractClaim_WhenValidToken_ShouldExtractClaim() {
        String token = jwtService.generateToken(testUser);
        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        assertEquals("test@example.com", subject);
    }
}
