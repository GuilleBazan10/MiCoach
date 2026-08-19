package com.micoach.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenerationLogJpaRepository extends JpaRepository<GenerationLogJpa, Long> {

    List<GenerationLogJpa> findAllByOrderByCreatedAtDesc();

    List<GenerationLogJpa> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<GenerationLogJpa> findByPromptSlugOrderByCreatedAtDesc(String promptSlug);

    List<GenerationLogJpa> findByUserIdAndPromptSlugOrderByCreatedAtDesc(Long userId, String promptSlug);

    @Modifying
    @Query("UPDATE GenerationLogJpa g SET g.status = :status WHERE g.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying
    @Query("UPDATE GenerationLogJpa g SET g.userFeedback = :feedback WHERE g.id = :id")
    void updateUserFeedback(@Param("id") Long id, @Param("feedback") String feedback);
}
