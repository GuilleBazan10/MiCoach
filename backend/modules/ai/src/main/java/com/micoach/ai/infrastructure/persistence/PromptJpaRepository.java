package com.micoach.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromptJpaRepository extends JpaRepository<PromptJpa, Long> {

    List<PromptJpa> findBySlugOrderByVersionDesc(String slug);

    List<PromptJpa> findBySlugAndActiveTrueOrderByVersionDesc(String slug);

    List<PromptJpa> findAllByOrderBySlugAscVersionDesc();

    List<PromptJpa> findByActiveTrueOrderBySlugAscVersionDesc();

    @Query("select coalesce(max(p.version), 0) from PromptJpa p where p.slug = :slug")
    int findMaxVersion(@Param("slug") String slug);
}
