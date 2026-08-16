package com.climateplatform.alert.domain.repository;

import com.climateplatform.alert.domain.model.ClimateAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClimateAlertRepository extends JpaRepository<ClimateAlert, UUID> {
    List<ClimateAlert> findByGovernorateIdAndIsResolvedFalseOrderByTriggeredAtDesc(UUID governorateId);

    List<ClimateAlert> findByIsResolvedFalseOrderByTriggeredAtDesc();

    @Query("SELECT a FROM ClimateAlert a WHERE a.isResolved = false AND a.expiresAt > :now ORDER BY a.triggeredAt DESC")
    List<ClimateAlert> findActiveAlerts(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM ClimateAlert a WHERE a.governorateId = :governorateId AND a.alertType = :alertType AND a.isResolved = false ORDER BY a.triggeredAt DESC")
    List<ClimateAlert> findByGovernorateAndAlertType(
            @Param("governorateId") UUID governorateId,
            @Param("alertType") com.climateplatform.alert.domain.model.AlertRule.AlertType alertType
    );

    Page<ClimateAlert> findByGovernorateIdOrderByTriggeredAtDesc(UUID governorateId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM ClimateAlert a WHERE a.isResolved = false AND a.expiresAt > :now")
    long countActiveAlerts(@Param("now") LocalDateTime now);

    List<ClimateAlert> findByExpiresAtBeforeAndIsResolvedFalse(LocalDateTime now);
}
