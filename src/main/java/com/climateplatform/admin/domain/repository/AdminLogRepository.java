package com.climateplatform.admin.domain.repository;

import com.climateplatform.admin.domain.model.AdminLog;
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
public interface AdminLogRepository extends JpaRepository<AdminLog, UUID> {
    Page<AdminLog> findByAdminIdOrderByCreatedAtDesc(UUID adminId, Pageable pageable);

    List<AdminLog> findByAdminIdOrderByCreatedAtDesc(UUID adminId);

    @Query("SELECT l FROM AdminLog l WHERE l.createdAt BETWEEN :startDate AND :endDate ORDER BY l.createdAt DESC")
    List<AdminLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT l FROM AdminLog l WHERE l.action = :action AND l.createdAt BETWEEN :startDate AND :endDate ORDER BY l.createdAt DESC")
    List<AdminLog> findByActionAndDateRange(@Param("action") String action,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT l FROM AdminLog l WHERE l.entityType = :entityType ORDER BY l.createdAt DESC")
    List<AdminLog> findByEntityType(@Param("entityType") String entityType);
}
