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
import com.climateplatform.dashboard.domain.model.HealthStatistic;
import com.climateplatform.dashboard.domain.repository.HealthStatisticRepository;
import com.climateplatform.domain.model.User;
import com.climateplatform.domain.repository.UserRepository;
import com.climateplatform.domain.repository.health.HealthProfileRepository;
import com.climateplatform.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final ClimateDataRepository climateDataRepository;
    private final ClimateAlertRepository climateAlertRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final HealthStatisticRepository healthStatisticRepository;
    private final AdminLogRepository adminLogRepository;

    @Transactional
    public void logAdminAction(UUID adminId, String adminEmail, String action,
                              String entityType, UUID entityId, String details) {
        AdminLog log = AdminLog.builder()
                .adminId(adminId)
                .adminEmail(adminEmail)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        adminLogRepository.save(log);
    }

    public List<AdminUserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        Set<UUID> usersWithProfiles = healthProfileRepository.findAll().stream()
                .map(profile -> profile.getUser().getId())
                .collect(Collectors.toSet());

        return users.stream()
                .map(user -> AdminUserResponse.from(user, usersWithProfiles.contains(user.getId())))
                .collect(Collectors.toList());
    }

    public Page<AdminUserResponse> getAllUsersPaginated(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        Set<UUID> usersWithProfiles = healthProfileRepository.findAll().stream()
                .map(profile -> profile.getUser().getId())
                .collect(Collectors.toSet());

        return users.map(user -> AdminUserResponse.from(user, usersWithProfiles.contains(user.getId())));
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userRepository.delete(user);
        log.info("User deleted: {}", userId);
    }

    @Transactional
    public User assignRole(UUID userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setRole(role);
        User updatedUser = userRepository.save(user);
        log.info("Role updated for user: {} to {}", userId, role);
        return updatedUser;
    }

    @Transactional
    public void deleteAlertRule(UUID ruleId) {
        AlertRule rule = alertRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", "id", ruleId));
        rule.setIsActive(false);
        alertRuleRepository.save(rule);
        log.info("Alert rule deactivated: {}", ruleId);
    }

    @Transactional
    public void resolveAlert(UUID alertId) {
        ClimateAlert alert = climateAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("ClimateAlert", "id", alertId));
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        climateAlertRepository.save(alert);
        log.info("Alert resolved: {}", alertId);
    }

    public AdminStatisticsResponse getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.ADMIN)
                .count();
        long totalGovernorates = 24L; // Tunisia has 24 governorates
        long totalClimateDataPoints = climateDataRepository.count();
        long totalAlerts = climateAlertRepository.count();
        long activeAlerts = climateAlertRepository.countActiveAlerts(LocalDateTime.now());
        long totalConversations = 0L; // Would be from AI conversation repository
        long totalHealthProfiles = healthProfileRepository.count();

        BigDecimal avgTemperature = calculateAverageTemperature();
        BigDecimal avgHumidity = calculateAverageHumidity();

        Map<String, Long> alertsByType = getAlertsByType();
        Map<String, Long> usersByRole = getUsersByRole();
        Map<String, Long> dataByGovernorate = getDataByGovernorate();
        Map<String, Long> alertsBySeverity = getAlertsBySeverity();

        return AdminStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalGovernorates(totalGovernorates)
                .totalClimateDataPoints(totalClimateDataPoints)
                .totalAlerts(totalAlerts)
                .activeAlerts(activeAlerts)
                .totalConversations(totalConversations)
                .totalHealthProfiles(totalHealthProfiles)
                .avgTemperature(avgTemperature)
                .avgHumidity(avgHumidity)
                .alertsByType(alertsByType)
                .usersByRole(usersByRole)
                .dataByGovernorate(dataByGovernorate)
                .alertsBySeverity(alertsBySeverity)
                .build();
    }

    public Page<AdminLogResponse> getAdminLogs(Pageable pageable) {
        Page<AdminLog> logs = adminLogRepository.findAll(pageable);
        return logs.map(AdminLogResponse::from);
    }

    public List<AdminLogResponse> getAdminLogsByAdmin(UUID adminId) {
        List<AdminLog> logs = adminLogRepository.findByAdminIdOrderByCreatedAtDesc(adminId);
        return logs.stream()
                .map(AdminLogResponse::from)
                .collect(Collectors.toList());
    }

    public List<AdminLogResponse> getAdminLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<AdminLog> logs = adminLogRepository.findByDateRange(startDate, endDate);
        return logs.stream()
                .map(AdminLogResponse::from)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateAverageTemperature() {
        List<ClimateData> recentData = climateDataRepository.findAll();
        if (recentData.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return recentData.stream()
                .map(ClimateData::getTemperature)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recentData.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAverageHumidity() {
        List<ClimateData> recentData = climateDataRepository.findAll();
        if (recentData.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return recentData.stream()
                .map(ClimateData::getHumidity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recentData.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private Map<String, Long> getAlertsByType() {
        List<ClimateAlert> alerts = climateAlertRepository.findActiveAlerts(LocalDateTime.now());
        return alerts.stream()
                .collect(Collectors.groupingBy(
                        alert -> alert.getAlertType().name(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getUsersByRole() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().name(),
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getDataByGovernorate() {
        List<ClimateData> data = climateDataRepository.findAll();
        return data.stream()
                .collect(Collectors.groupingBy(
                        ClimateData::getGovernorateName,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> getAlertsBySeverity() {
        List<ClimateAlert> alerts = climateAlertRepository.findActiveAlerts(LocalDateTime.now());
        return alerts.stream()
                .collect(Collectors.groupingBy(
                        alert -> alert.getSeverity().name(),
                        Collectors.counting()
                ));
    }
}
