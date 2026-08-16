package com.climateplatform.map.domain.repository;

import com.climateplatform.map.domain.model.MapMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MapMarkerRepository extends JpaRepository<MapMarker, UUID> {
    List<MapMarker> findByGovernorateId(UUID governorateId);

    List<MapMarker> findByIsActiveTrue();

    List<MapMarker> findByMarkerType(String markerType);
}
