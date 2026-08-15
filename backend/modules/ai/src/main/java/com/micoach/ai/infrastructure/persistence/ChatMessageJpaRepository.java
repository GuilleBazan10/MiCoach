package com.micoach.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpa, Long> {

    List<ChatMessageJpa> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
