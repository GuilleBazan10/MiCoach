package com.kineticos.ai.infrastructure.provider;

import com.kineticos.ai.application.port.out.AiProviderStrategy;
import com.kineticos.ai.application.port.out.ResolvedProvider;
import com.kineticos.shared.error.DomainException;
import com.kineticos.shared.error.ErrorCode;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proveedor genérico para cualquier API compatible con el formato de OpenAI (chat
 * completions) — cubre Groq y OpenRouter sin escribir una integración por cada uno,
 * solo cambia la {@code baseUrl}. No es un {@code @Component}: {@code AiConfig} registra
 * una instancia por proveedor (ver {@code providerId}), cada una con su propio caché.
 */
public class OpenAiCompatibleProviderStrategy implements AiProviderStrategy {

    private final String providerId;
    private final Map<String, ChatLanguageModel> modelsByKey = new ConcurrentHashMap<>();

    public OpenAiCompatibleProviderStrategy(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public String providerId() {
        return providerId;
    }

    @Override
    public String complete(String prompt, ResolvedProvider provider) {
        if (provider.apiKey() == null || provider.apiKey().isBlank()) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "Falta configurar la API key de " + providerId + " (ver /admin)");
        }
        try {
            return modelFor(provider).generate(prompt);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo generar contenido con " + providerId + ": " + e.getMessage());
        }
    }

    private ChatLanguageModel modelFor(ResolvedProvider provider) {
        String cacheKey = provider.baseUrl() + "|" + provider.model() + "|" + provider.apiKey().hashCode();
        return modelsByKey.computeIfAbsent(cacheKey, k -> OpenAiChatModel.builder()
                .baseUrl(provider.baseUrl())
                .apiKey(provider.apiKey())
                .modelName(provider.model())
                .timeout(Duration.ofSeconds(60))
                .maxTokens(2048)
                .build());
    }
}
