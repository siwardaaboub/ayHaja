package com.climateplatform.domain.model.health;

import com.climateplatform.domain.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "age")
    private Integer age;

    @Column(name = "hypertension")
    private Boolean hypertension;

    @Column(name = "asthma")
    private Boolean asthma;

    @Column(name = "diabetes")
    private Boolean diabetes;

    @Column(name = "heart_disease")
    private Boolean heartDisease;

    @Column(name = "pregnancy")
    private Boolean pregnancy;

    @Column(name = "elderly_person")
    private Boolean elderlyPerson;

    @Column(name = "children_at_home")
    private Boolean childrenAtHome;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
