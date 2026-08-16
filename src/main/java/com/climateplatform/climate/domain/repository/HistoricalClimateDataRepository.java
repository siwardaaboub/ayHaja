package com.climateplatform.climate.domain.repository;

import com.climateplatform.climate.domain.model.HistoricalClimateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricalClimateDataRepository extends JpaRepository<HistoricalClimateData, UUID> {
    @Query("SELECT h FROM HistoricalClimateData h WHERE h.governorateId = :governorateId AND h.dateRecorded BETWEEN :startDate AND :endDate ORDER BY h.dateRecorded DESC")
    List<HistoricalClimateData> findByGovernorateAndDateRange(
            @Param("governorateId") UUID governorateId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT h FROM HistoricalClimateData h WHERE h.governorateId = :governorateId AND YEAR(h.dateRecorded) = :year ORDER BY h.dateRecorded DESC")
    List<HistoricalClimateData> findByGovernorateAndYear(
            @Param("governorateId") UUID governorateId,
            @Param("year") int year
    );

    @Query("SELECT h FROM HistoricalClimateData h WHERE h.governorateId = :governorateId AND YEAR(h.dateRecorded) = :year AND MONTH(h.dateRecorded) = :month ORDER BY h.dateRecorded DESC")
    List<HistoricalClimateData> findByGovernorateYearAndMonth(
            @Param("governorateId") UUID governorateId,
            @Param("year") int year,
            @Param("month") int month
    );

    void deleteByGovernorateId(UUID governorateId);
}
