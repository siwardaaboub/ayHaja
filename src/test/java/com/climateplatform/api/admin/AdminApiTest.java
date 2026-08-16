package com.climateplatform.api.admin;

import com.climateplatform.ClimatePlatformApplication;
import com.climateplatform.admin.domain.model.AdminLog;
import com.climateplatform.admin.domain.repository.AdminLogRepository;
import com.climateplatform.admin.domain.service.AdminService;
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

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClimatePlatformApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AdminApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminLogRepository adminLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        adminLogRepository.deleteAll();
    }

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturn200AndList() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void deleteUser_WhenValidId_ShouldReturn204() throws Exception {
        User user = User.builder()
                .email("delete@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Delete")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/admin/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_WhenInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/admin/users/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStatistics_WhenDataExists_ShouldReturn200() throws Exception {
        User user1 = User.builder()
                .email("user1@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("User1")
                .lastName("Test")
                .role(User.Role.USER)
                .build();
        User user2 = User.builder()
                .email("admin@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Admin")
                .lastName("Test")
                .role(User.Role.ADMIN)
                .build();
        userRepository.saveAll(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/admin/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.totalAdmins").value(1));
    }
}
