package com.micoach.ai.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * Entidad JPA de la tabla {@code ai_generation_logs}.
 */
@Entity
@Table(name = "ai_generation_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationLogJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "prompt_slug", nullable = false)
    private String promptSlug;

    @Column(name = "prompt_version")
    private Integer promptVersion;

    @Column(name = "provider")
    private String provider;

    @Column(name = "model")
    private String model;

    @Column(name = "input_context")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> inputContext;

    @Column(name = "output")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> output;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "user_feedback")
    private String userFeedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
