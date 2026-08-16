package com.climateplatform.domain.repository.health;

import com.climateplatform.domain.model.health.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, UUID> {
    Optional<HealthProfile> findByUserId(UUID userId);

    @Query("SELECT hp FROM HealthProfile hp JOIN FETCH hp.user WHERE hp.id = :id")
    Optional<HealthProfile> findByIdWithUser(@Param("id") UUID id);

    boolean existsByUserId(UUID userId);
}
