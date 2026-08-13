package com.kineticos.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenerationLogJpaRepository extends JpaRepository<GenerationLogJpa, Long> {

    List<GenerationLogJpa> findAllByOrderByCreatedAtDesc();

    List<GenerationLogJpa> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<GenerationLogJpa> findByPromptSlugOrderByCreatedAtDesc(String promptSlug);

    List<GenerationLogJpa> findByUserIdAndPromptSlugOrderByCreatedAtDesc(Long userId, String promptSlug);
}
