package com.climateplatform.dashboard.domain.repository;

import com.climateplatform.dashboard.domain.model.HealthStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HealthStatisticRepository extends JpaRepository<HealthStatistic, UUID> {
    List<HealthStatistic> findByGovernorateIdOrderByStatisticDateDesc(UUID governorateId);

    @Query("SELECT h FROM HealthStatistic h WHERE h.statisticDate BETWEEN :startDate AND :endDate ORDER BY h.statisticDate DESC")
    List<HealthStatistic> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT h FROM HealthStatistic h WHERE h.governorateId = :governorateId AND h.statisticDate BETWEEN :startDate AND :endDate ORDER BY h.statisticDate DESC")
    List<HealthStatistic> findByGovernorateAndDateRange(
            @Param("governorateId") UUID governorateId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT h FROM HealthStatistic h WHERE h.statisticDate = (SELECT MAX(h2.statisticDate) FROM HealthStatistic h2 WHERE h2.governorateId = h.governorateId)")
    List<HealthStatistic> findLatestStatistics();

    @Query("SELECT h FROM HealthStatistic h WHERE h.heatwaveRisk > 0 OR h.droughtRisk > 0 OR h.fireRisk > 0 ORDER BY h.statisticDate DESC")
    List<HealthStatistic> findActiveRiskAreas();
}
