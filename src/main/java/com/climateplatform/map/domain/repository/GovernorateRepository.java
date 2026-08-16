package com.climateplatform.map.domain.repository;

import com.climateplatform.map.domain.model.Governorate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GovernorateRepository extends JpaRepository<Governorate, UUID> {
    Optional<Governorate> findByName(String name);

    @Query("SELECT g FROM Governorate g ORDER BY g.name")
    List<Governorate> findAllOrderedByName();

    @Query(value = "SELECT ST_AsGeoJSON(g.geometry) FROM governorates g WHERE g.id = :id", nativeQuery = true)
    String findGeometryAsGeoJson(@Param("id") UUID id);

    @Query(value = "SELECT g.id, g.name, g.name_ar, g.center_lat, g.center_lon, " +
                   "ST_AsGeoJSON(g.geometry) as geometry FROM governorates g", nativeQuery = true)
    List<Object[]> findAllWithGeometry();

    @Query(value = "SELECT g.id, g.name, g.name_ar, g.center_lat, g.center_lon, " +
                   "ST_AsGeoJSON(g.geometry) as geometry FROM governorates g WHERE g.id = :id", nativeQuery = true)
    Object[] findWithGeometryById(@Param("id") UUID id);
}
