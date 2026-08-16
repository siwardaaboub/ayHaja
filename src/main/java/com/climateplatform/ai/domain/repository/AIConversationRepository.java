package com.climateplatform.ai.domain.repository;

import com.climateplatform.ai.domain.model.AIConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AIConversationRepository extends JpaRepository<AIConversation, UUID> {
    List<AIConversation> findByUserIdOrderByStartedAtDesc(UUID userId);

    @Query("SELECT c FROM AIConversation c WHERE c.sessionId = :sessionId AND c.isActive = true")
    Optional<AIConversation> findBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT c FROM AIConversation c WHERE c.userId = :userId AND c.isActive = true ORDER BY c.lastInteractionAt DESC")
    Optional<AIConversation> findActiveByUserId(@Param("userId") UUID userId);

    List<AIConversation> findByGovernorateId(UUID governorateId);
}
