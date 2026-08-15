package com.micoach.ai.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Prompt versionado (tabla ai_prompts). Se crea una fila nueva por versión de un mismo
 * {@code slug}; solo se ejecuta la versión marcada como activa.
 */
@Getter
public class Prompt {

    private final Long id;
    private final String slug;
    private final Integer version;
    private final String provider;
    private final String model;
    private final String content;
    private final Map<String, Object> params;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private Prompt(Long id, String slug, Integer version, String provider, String model, String content,
                   Map<String, Object> params, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.slug = slug;
        this.version = version;
        this.provider = provider;
        this.model = model;
        this.content = content;
        this.params = params;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Prompt create(String slug, Integer version, String provider, String model, String content,
                                Map<String, Object> params) {
        Instant now = Instant.now();
        return new Prompt(null, slug, version, provider == null ? "ollama" : provider,
                model == null ? "llama3.2" : model, content, params, true, now, now);
    }

    public static Prompt restore(Long id, String slug, Integer version, String provider, String model,
                                 String content, Map<String, Object> params, boolean active, Instant createdAt,
                                 Instant updatedAt) {
        return new Prompt(id, slug, version, provider, model, content, params, active, createdAt, updatedAt);
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = Instant.now();
    }
}
