package com.climateplatform.climate.domain.repository;

import com.climateplatform.climate.domain.model.ClimateForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClimateForecastRepository extends JpaRepository<ClimateForecast, UUID> {
    @Query("SELECT f FROM ClimateForecast f WHERE f.governorateId = :governorateId AND f.forecastDate >= :startDate ORDER BY f.forecastDate")
    List<ClimateForecast> findByGovernorateAndStartDate(
            @Param("governorateId") UUID governorateId,
            @Param("startDate") LocalDate startDate
    );

    @Query("SELECT f FROM ClimateForecast f WHERE f.governorateId = :governorateId AND f.forecastDate BETWEEN :startDate AND :endDate ORDER BY f.forecastDate")
    List<ClimateForecast> findByGovernorateAndDateRange(
            @Param("governorateId") UUID governorateId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    void deleteByGovernorateIdAndForecastDateBefore(UUID governorateId, LocalDate date);
}
