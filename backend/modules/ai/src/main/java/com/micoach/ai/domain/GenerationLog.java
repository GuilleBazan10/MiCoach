package com.micoach.ai.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Auditoría de una generación de IA (tabla ai_generation_logs): contexto de entrada,
 * salida y métricas. Solo lectura por API por ahora; lo escribirán los otros módulos
 * cuando empiecen a llamar realmente a un proveedor de IA (Fase 4).
 */
@Getter
public class GenerationLog {

    private final Long id;
    private final Long userId;
    private final String promptSlug;
    private final Integer promptVersion;
    private final String provider;
    private final String model;
    private final Map<String, Object> inputContext;
    private final Map<String, Object> output;
    private final Integer durationMs;
    private final String status;
    /** Qué hizo el usuario con lo generado: 'kept' | 'discarded' | null (todavía no se sabe). */
    private final String userFeedback;
    private final Instant createdAt;

    private GenerationLog(Long id, Long userId, String promptSlug, Integer promptVersion, String provider,
                          String model, Map<String, Object> inputContext, Map<String, Object> output,
                          Integer durationMs, String status, String userFeedback, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.promptSlug = promptSlug;
        this.promptVersion = promptVersion;
        this.provider = provider;
        this.model = model;
        this.inputContext = inputContext;
        this.output = output;
        this.durationMs = durationMs;
        this.status = status;
        this.userFeedback = userFeedback;
        this.createdAt = createdAt;
    }

    public static GenerationLog restore(Long id, Long userId, String promptSlug, Integer promptVersion,
                                        String provider, String model, Map<String, Object> inputContext,
                                        Map<String, Object> output, Integer durationMs, String status,
                                        String userFeedback, Instant createdAt) {
        return new GenerationLog(id, userId, promptSlug, promptVersion, provider, model, inputContext, output,
                durationMs, status, userFeedback, createdAt);
    }

    public static GenerationLog create(Long userId, String promptSlug, Integer promptVersion, String provider,
                                       String model, Map<String, Object> inputContext, Map<String, Object> output,
                                       Integer durationMs, String status) {
        return new GenerationLog(null, userId, promptSlug, promptVersion, provider, model, inputContext, output,
                durationMs, status, null, Instant.now());
    }
}
