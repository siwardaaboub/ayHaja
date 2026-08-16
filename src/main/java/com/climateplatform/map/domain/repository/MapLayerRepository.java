package com.climateplatform.map.domain.repository;

import com.climateplatform.map.domain.model.MapLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MapLayerRepository extends JpaRepository<MapLayer, UUID> {
    @Query("SELECT ml FROM MapLayer ml WHERE ml.isActive = true ORDER BY ml.zIndex ASC")
    List<MapLayer> findByIsActiveTrueOrderByZIndex();

    List<MapLayer> findByLayerType(String layerType);
}
