package com.kineticos.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationJpaRepository extends JpaRepository<ConversationJpa, Long> {

    List<ConversationJpa> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<ConversationJpa> findByUserIdAndTopicOrderByUpdatedAtDesc(Long userId, String topic);
}
