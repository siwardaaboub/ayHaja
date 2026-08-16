package com.climateplatform.alert.domain.repository;

import com.climateplatform.alert.domain.model.AlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, UUID> {
    List<AlertSubscription> findByUserIdAndIsActiveTrue(UUID userId);

    List<AlertSubscription> findByGovernorateIdAndIsActiveTrue(UUID governorateId);

    List<AlertSubscription> findByAlertTypeAndIsActiveTrue(
            com.climateplatform.alert.domain.model.AlertRule.AlertType alertType
    );
}
