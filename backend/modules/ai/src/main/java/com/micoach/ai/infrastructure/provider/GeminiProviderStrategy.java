package com.micoach.ai.infrastructure.provider;

import com.micoach.ai.application.port.out.AiProviderStrategy;
import com.micoach.ai.application.port.out.ResolvedProvider;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proveedor Google Gemini (tier gratis de Google AI Studio). A diferencia de Ollama/Groq/
 * OpenRouter no usa una base URL configurable: el endpoint de Google es fijo, solo hacen
 * falta la API key y el modelo.
 */
@Component
public class GeminiProviderStrategy implements AiProviderStrategy {

    private final Map<String, ChatLanguageModel> modelsByKey = new ConcurrentHashMap<>();

    @Override
    public String providerId() {
        return "gemini";
    }

    @Override
    public String complete(String prompt, ResolvedProvider provider) {
        if (provider.apiKey() == null || provider.apiKey().isBlank()) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "Falta configurar la API key de Gemini (ver /admin)");
        }
        try {
            return modelFor(provider).generate(prompt);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo generar contenido con Gemini: " + e.getMessage());
        }
    }

    private ChatLanguageModel modelFor(ResolvedProvider provider) {
        String cacheKey = provider.model() + "|" + provider.apiKey().hashCode();
        return modelsByKey.computeIfAbsent(cacheKey, k -> GoogleAiGeminiChatModel.builder()
                .apiKey(provider.apiKey())
                .modelName(provider.model())
                .maxOutputTokens(2048)
                .build());
    }
}
