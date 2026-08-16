package com.climateplatform.map.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "map_layers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "layer_type", nullable = false, length = 50)
    private String layerType;

    @Column(name = "url_template", length = 500)
    private String urlTemplate;

    @Column(name = "attribution", length = 500)
    private String attribution;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "z_index")
    private Integer zIndex = 0;

    @Column(name = "opacity")
    private BigDecimal opacity = BigDecimal.ONE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
