package com.climateplatform.admin.controller;

import com.climateplatform.admin.domain.service.AdminService;
import com.climateplatform.admin.dto.response.AdminLogResponse;
import com.climateplatform.admin.dto.response.AdminStatisticsResponse;
import com.climateplatform.admin.dto.response.AdminUserResponse;
import com.climateplatform.alert.dto.response.AlertRuleResponse;
import com.climateplatform.alert.domain.repository.AlertRuleRepository;
import com.climateplatform.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final AlertRuleRepository alertRuleRepository;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> assignRole(
            @PathVariable UUID id,
            @RequestParam User.Role role) {
        User updatedUser = adminService.assignRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getSystemStatistics() {
        AdminStatisticsResponse statistics = adminService.getSystemStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AdminLogResponse>> getAdminLogs(
            @RequestParam(required = false) UUID adminId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        if (adminId != null) {
            List<AdminLogResponse> logs = adminService.getAdminLogsByAdmin(adminId);
            return ResponseEntity.ok(logs);
        }

        if (startDate != null && endDate != null) {
            List<AdminLogResponse> logs = adminService.getAdminLogsByDateRange(startDate, endDate);
            return ResponseEntity.ok(logs);
        }

        List<AdminLogResponse> logs = adminService.getAdminLogsByDateRange(
                LocalDateTime.now().minusDays(30), LocalDateTime.now());
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/alerts/rules")
    public ResponseEntity<List<AlertRuleResponse>> getAllAlertRules() {
        List<AlertRuleResponse> rules = alertRuleRepository.findByIsActiveTrue().stream()
                .map(AlertRuleResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/alerts/rules/{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable UUID id) {
        adminService.deleteAlertRule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<Void> resolveAlert(@PathVariable UUID id) {
        adminService.resolveAlert(id);
        return ResponseEntity.ok().build();
    }
}
