package com.climateplatform.admin.domain.service;

import com.climateplatform.admin.domain.model.AdminLog;
import com.climateplatform.admin.domain.repository.AdminLogRepository;
import com.climateplatform.admin.dto.response.AdminLogResponse;
import com.climateplatform.admin.dto.response.AdminStatisticsResponse;
import com.climateplatform.admin.dto.response.AdminUserResponse;
import com.climateplatform.alert.domain.model.AlertRule;
import com.climateplatform.alert.domain.model.ClimateAlert;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.alert.domain.repository.ClimateAlertRepository;
import com.climateplatform.climate.domain.model.ClimateData;
import com.climateplatform.climate.domain.repository.ClimateDataRepository;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.domain.repository.health.HealthProfileRepository;
import com.climateplatform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HealthProfileRepository healthProfileRepository;

    @Mock
    private ClimateDataRepository climateDataRepository;

    @Mock
    private ClimateAlertRepository climateAlertRepository;

    @Mock
    private AlertRuleRepository alertRuleRepository;

    @Mock
    private HealthStatisticRepository healthStatisticRepository;

    @Mock
    private AdminLogRepository adminLogRepository;

    @InjectMocks
    private AdminService adminService;

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
    void getAllUsers_WhenUsersExist_ShouldReturnResponses() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(healthProfileRepository.findAll()).thenReturn(Arrays.asList());

        List<AdminUserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertFalse(result.get(0).getHasHealthProfile());
    }

    @Test
    void getAllUsers_WhenUserHasProfile_ShouldMarkAsTrue() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // Mock health profile with user reference
        var mockProfile = mock(com.climateplatform.domain.model.health.HealthProfile.class);
        when(mockProfile.getUser()).thenReturn(testUser);
        when(healthProfileRepository.findAll()).thenReturn(Arrays.asList(mockProfile));

        List<AdminUserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.get(0).getHasHealthProfile());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        adminService.deleteUser(userId);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteUser(userId)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void assignRole_WhenValidRequest_ShouldUpdateRole() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User updatedUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.ADMIN)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = adminService.assignRole(userId, User.Role.ADMIN);

        assertNotNull(result);
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void assignRole_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.assignRole(userId, User.Role.ADMIN)
        );

        assertEquals("User not found with id : '" + userId + "'", exception.getMessage());
    }

    @Test
    void getSystemStatistics_WhenDataExists_ShouldReturnStatistics() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(climateDataRepository.count()).thenReturn(5000L);
        when(climateAlertRepository.count()).thenReturn(50L);
        when(climateAlertRepository.countActiveAlerts(any(LocalDateTime.class))).thenReturn(10L);
        when(climateAlertRepository.findActiveAlerts(any(LocalDateTime.class))).thenReturn(Arrays.asList());
        when(climateDataRepository.findAll()).thenReturn(Arrays.asList());

        AdminStatisticsResponse result = adminService.getSystemStatistics();

        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(50L, result.getTotalAlerts());
        assertEquals(10L, result.getActiveAlerts());
    }

    @Test
    void deleteAlertRule_WhenRuleExists_ShouldDeactivateRule() {
        UUID ruleId = UUID.randomUUID();
        AlertRule rule = AlertRule.builder()
                .id(ruleId)
                .ruleName("Test Rule")
                .isActive(true)
                .build();

        when(alertRuleRepository.findById(ruleId)).thenReturn(Optional.of(rule));
        when(alertRuleRepository.save(any(AlertRule.class))).thenReturn(rule);

        adminService.deleteAlertRule(ruleId);

        assertFalse(rule.getIsActive());
        verify(alertRuleRepository).save(any(AlertRule.class));
    }

    @Test
    void deleteAlertRule_WhenRuleNotFound_ShouldThrowException() {
        UUID ruleId = UUID.randomUUID();
        when(alertRuleRepository.findById(ruleId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteAlertRule(ruleId)
        );

        assertEquals("AlertRule not found with id : '" + ruleId + "'", exception.getMessage());
    }

    @Test
    void resolveAlert_WhenAlertExists_ShouldResolveAlert() {
        UUID alertId = UUID.randomUUID();
        ClimateAlert alert = ClimateAlert.builder()
                .id(alertId)
                .title("Test Alert")
                .isResolved(false)
                .build();

        when(climateAlertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(climateAlertRepository.save(any(ClimateAlert.class))).thenReturn(alert);

        adminService.resolveAlert(alertId);

        assertTrue(alert.getIsResolved());
        assertNotNull(alert.getResolvedAt());
        verify(climateAlertRepository).save(any(ClimateAlert.class));
    }

    @Test
    void resolveAlert_WhenAlertNotFound_ShouldThrowException() {
        UUID alertId = UUID.randomUUID();
        when(climateAlertRepository.findById(alertId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.resolveAlert(alertId)
        );

        assertEquals("ClimateAlert not found with id : '" + alertId + "'", exception.getMessage());
    }

    @Test
    void getAdminLogsByAdmin_WhenLogsExist_ShouldReturnResponses() {
        UUID adminId = UUID.randomUUID();
        AdminLog log = AdminLog.builder()
                .id(UUID.randomUUID())
                .adminId(adminId)
                .adminEmail("admin@example.com")
                .action("USER_DELETED")
                .entityType("User")
                .details("Deleted user")
                .build();

        when(adminLogRepository.findByAdminIdOrderByCreatedAtDesc(adminId)).thenReturn(Arrays.asList(log));

        List<AdminLogResponse> result = adminService.getAdminLogsByAdmin(adminId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USER_DELETED", result.get(0).getAction());
    }

    @Test
    void getAdminLogsByDateRange_WhenLogsExist_ShouldReturnResponses() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        AdminLog log = AdminLog.builder()
                .id(UUID.randomUUID())
                .adminId(UUID.randomUUID())
                .adminEmail("admin@example.com")
                .action("USER_CREATED")
                .entityType("User")
                .details("Created user")
                .build();

        when(adminLogRepository.findByDateRange(startDate, endDate)).thenReturn(Arrays.asList(log));

        List<AdminLogResponse> result = adminService.getAdminLogsByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USER_CREATED", result.get(0).getAction());
    }

    @Test
    void logAdminAction_WhenValidData_ShouldCreateLog() {
        UUID adminId = UUID.randomUUID();
        String adminEmail = "admin@example.com";

        AdminLog savedLog = AdminLog.builder()
                .id(UUID.randomUUID())
                .adminId(adminId)
                .adminEmail(adminEmail)
                .action("USER_DELETED")
                .entityType("User")
                .entityId(UUID.randomUUID())
                .details("Deleted user")
                .build();

        when(adminLogRepository.save(any(AdminLog.class))).thenReturn(savedLog);

        adminService.logAdminAction(adminId, adminEmail, "USER_DELETED", "User", UUID.randomUUID(), "Deleted user");

        verify(adminLogRepository).save(any(AdminLog.class));
    }
}
