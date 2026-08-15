package com.micoach.ai.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Configuración de un proveedor de IA (tabla {@code ai_provider_configs}): una fila fija
 * por proveedor soportado (ollama, groq, openrouter, gemini), sembrada por migración.
 * Solo uno puede estar {@code active} a la vez — es el que usa {@code AiService.generate}.
 * La API key se guarda siempre cifrada; este objeto nunca ve el valor en texto plano.
 */
@Getter
public class AiProviderConfig {

    private final Long id;
    private final String provider;
    private String displayName;
    private String baseUrl;
    private String model;
    private String apiKeyEncrypted;
    private boolean enabled;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiProviderConfig(Long id, String provider, String displayName, String baseUrl, String model,
                             String apiKeyEncrypted, boolean enabled, boolean active, Instant createdAt,
                             Instant updatedAt) {
        this.id = id;
        this.provider = provider;
        this.displayName = displayName;
        this.baseUrl = baseUrl;
        this.model = model;
        this.apiKeyEncrypted = apiKeyEncrypted;
        this.enabled = enabled;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiProviderConfig restore(Long id, String provider, String displayName, String baseUrl,
                                           String model, String apiKeyEncrypted, boolean enabled, boolean active,
                                           Instant createdAt, Instant updatedAt) {
        return new AiProviderConfig(id, provider, displayName, baseUrl, model, apiKeyEncrypted, enabled, active,
                createdAt, updatedAt);
    }

    public boolean hasApiKey() {
        return apiKeyEncrypted != null && !apiKeyEncrypted.isBlank();
    }

    /** {@code newApiKeyEncrypted} null = mantener la key ya guardada (edición sin cambiarla). */
    public void update(String displayName, String baseUrl, String model, String newApiKeyEncrypted,
                       boolean enabled) {
        this.displayName = displayName;
        this.baseUrl = baseUrl;
        this.model = model;
        if (newApiKeyEncrypted != null) {
            this.apiKeyEncrypted = newApiKeyEncrypted;
        }
        this.enabled = enabled;
        this.updatedAt = Instant.now();
    }

    /** Activar implica que está habilitado — no tendría sentido un proveedor activo pero deshabilitado. */
    public void activate() {
        this.active = true;
        this.enabled = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
