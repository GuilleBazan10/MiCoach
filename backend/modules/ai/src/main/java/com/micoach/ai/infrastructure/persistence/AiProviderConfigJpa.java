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

import java.time.Instant;

/**
 * Entidad JPA de la tabla {@code ai_provider_configs}.
 */
@Entity
@Table(name = "ai_provider_configs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProviderConfigJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "provider", nullable = false, unique = true)
    private String provider;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "api_key_enc")
    private String apiKeyEnc;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
