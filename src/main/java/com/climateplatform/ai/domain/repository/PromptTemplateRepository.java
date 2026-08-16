package com.climateplatform.ai.domain.repository;

import com.climateplatform.ai.domain.model.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, UUID> {
    Optional<PromptTemplate> findByTemplateNameAndIsActiveTrue(String templateName);

    List<PromptTemplate> findByPromptTypeAndIsActiveTrue(PromptTemplate.PromptType promptType);

    List<PromptTemplate> findByIsActiveTrue();
}
