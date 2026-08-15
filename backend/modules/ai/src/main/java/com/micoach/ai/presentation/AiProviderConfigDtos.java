package com.micoach.ai.presentation;

import com.micoach.ai.application.port.in.AiUseCase.ProviderTestResult;
import com.micoach.ai.domain.AiProviderConfig;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

/**
 * DTOs del panel admin de proveedores de IA.
 */
public final class AiProviderConfigDtos {

    private AiProviderConfigDtos() {
    }

    /** Nunca incluye la API key: solo {@code hasApiKey} para que la UI sepa si ya hay una guardada. */
    public record ProviderConfigResponse(Long id, String provider, String displayName, String baseUrl, String model,
                                         boolean hasApiKey, boolean enabled, boolean active, Instant updatedAt) {

        static ProviderConfigResponse from(AiProviderConfig config) {
            return new ProviderConfigResponse(config.getId(), config.getProvider(), config.getDisplayName(),
                    config.getBaseUrl(), config.getModel(), config.hasApiKey(), config.isEnabled(),
                    config.isActive(), config.getUpdatedAt());
        }
    }

    /** {@code apiKey} vacío/nulo = mantener la key ya guardada (no se pisa por accidente al editar otro campo). */
    public record ProviderConfigRequest(@NotBlank String displayName, String baseUrl, @NotBlank String model,
                                        String apiKey, boolean enabled) {
    }

    public record ProviderTestResponse(boolean ok, String message) {

        static ProviderTestResponse from(ProviderTestResult result) {
            return new ProviderTestResponse(result.ok(), result.message());
        }
    }
}
