package com.climateplatform.climate.domain.repository;

import com.climateplatform.climate.domain.model.ClimateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClimateDataRepository extends JpaRepository<ClimateData, UUID> {
    List<ClimateData> findByGovernorateIdOrderByDateRecordedDesc(UUID governorateId);

    @Query("SELECT c FROM ClimateData c WHERE c.governorateId = :governorateId AND c.dateRecorded = :date")
    List<ClimateData> findByGovernorateAndDate(@Param("governorateId") UUID governorateId, @Param("date") LocalDate date);

    @Query("SELECT c FROM ClimateData c WHERE c.governorateId = :governorateId AND c.dateRecorded BETWEEN :startDate AND :endDate ORDER BY c.dateRecorded DESC")
    List<ClimateData> findByGovernorateAndDateRange(
            @Param("governorateId") UUID governorateId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT c FROM ClimateData c WHERE c.dateRecorded = :date ORDER BY c.governorateName")
    List<ClimateData> findByDate(@Param("date") LocalDate date);
}
