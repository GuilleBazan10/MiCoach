package com.micoach.ai.infrastructure.provider;

import com.micoach.ai.application.port.out.AiProviderStrategy;
import com.micoach.ai.application.port.out.ResolvedProvider;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proveedor de IA local vía Ollama (LangChain4j). Gratis, sin API key, corre en la
 * infra del proyecto ({@code docker-compose.yml} servicio {@code ollama}).
 * Un {@link ChatLanguageModel} por combinación baseUrl+modelo (se cachean, son inmutables;
 * si el admin cambia la config desde el panel, se arma un cliente nuevo bajo otra key).
 */
@Component
public class OllamaProviderStrategy implements AiProviderStrategy {

    private final Map<String, ChatLanguageModel> modelsByKey = new ConcurrentHashMap<>();

    @Override
    public String providerId() {
        return "ollama";
    }

    @Override
    public String complete(String prompt, ResolvedProvider provider) {
        try {
            return modelFor(provider).generate(prompt);
        } catch (Exception e) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo generar contenido con Ollama (¿está corriendo? ver docker-compose.yml): "
                            + e.getMessage());
        }
    }

    private ChatLanguageModel modelFor(ResolvedProvider provider) {
        String cacheKey = provider.baseUrl() + "|" + provider.model();
        return modelsByKey.computeIfAbsent(cacheKey, k -> OllamaChatModel.builder()
                .baseUrl(provider.baseUrl())
                .modelName(provider.model())
                .timeout(Duration.ofMinutes(3))
                // Sin esto, una respuesta JSON larga (varios días con varias comidas/
                // ejercicios cada uno) se corta a mitad de generación con el límite por
                // defecto del cliente — se vio en la práctica con meal_plan_generator
                // devolviendo un JSON incompleto sin el cierre final.
                .numPredict(2048)
                .build());
    }
}
