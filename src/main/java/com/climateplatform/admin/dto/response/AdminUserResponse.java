package com.climateplatform.admin.dto.response;

import com.climateplatform.domain.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasHealthProfile;

    public static AdminUserResponse from(User user, boolean hasHealthProfile) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .hasHealthProfile(hasHealthProfile)
                .build();
    }
}
