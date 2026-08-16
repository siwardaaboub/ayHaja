package com.climateplatform.alert.domain.repository;

import com.climateplatform.alert.domain.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, UUID> {
    List<AlertRule> findByIsActiveTrue();

    List<AlertRule> findByAlertType(AlertRule.AlertType alertType);

    List<AlertRule> findByAlertTypeAndIsActiveTrue(AlertRule.AlertType alertType);
}
