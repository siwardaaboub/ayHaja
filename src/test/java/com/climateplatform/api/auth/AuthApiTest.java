package com.climateplatform.api.auth;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_WhenValidRequest_ShouldReturn200AndToken() throws Exception {
        String request = """
                {
                    "email": "newuser@example.com",
                    "password": "password123",
                    "firstName": "Jane",
                    "lastName": "Doe"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Jane"));
    }

    @Test
    void registerUser_WhenEmailAlreadyExists_ShouldReturn400() throws Exception {
        User existingUser = User.builder()
                .email("existing@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Existing")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        userRepository.save(existingUser);

        String request = """
                {
                    "email": "existing@example.com",
                    "password": "password123",
                    "firstName": "New",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void registerUser_WhenInvalidEmail_ShouldReturn400() throws Exception {
        String request = """
                {
                    "email": "invalid-email",
                    "password": "password123",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WhenShortPassword_ShouldReturn400() throws Exception {
        String request = """
                {
                    "email": "test@example.com",
                    "password": "short",
                    "firstName": "Test",
                    "lastName": "User"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_WhenValidCredentials_ShouldReturn200AndToken() throws Exception {
        User user = User.builder()
                .email("login@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Login")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        String request = """
                {
                    "email": "login@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("login@example.com"));
    }

    @Test
    void loginUser_WhenInvalidCredentials_ShouldReturn401() throws Exception {
        String request = """
                {
                    "email": "nonexistent@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUser_WhenInvalidEmail_ShouldReturn400() throws Exception {
        String request = """
                {
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
