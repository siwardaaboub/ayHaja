package com.climateplatform.admin.dto.response;

import com.climateplatform.admin.domain.model.AdminLog;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogResponse {
    private UUID id;
    private UUID adminId;
    private String adminEmail;
    private String action;
    private String entityType;
    private UUID entityId;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;

    public static AdminLogResponse from(AdminLog log) {
        return AdminLogResponse.builder()
                .id(log.getId())
                .adminId(log.getAdminId())
                .adminEmail(log.getAdminEmail())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
