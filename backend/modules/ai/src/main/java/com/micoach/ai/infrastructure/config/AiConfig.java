package com.micoach.ai.infrastructure.config;

import com.micoach.ai.application.port.out.AiProviderStrategy;
import com.micoach.ai.infrastructure.provider.OpenAiCompatibleProviderStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring del módulo ai. Groq y OpenRouter comparten la misma implementación
 * (API compatible con OpenAI, ver {@link OpenAiCompatibleProviderStrategy}) — acá se
 * registran como dos beans separados, cada uno con su {@code providerId}. El resto de los
 * beans funcionales (AiService, otras estrategias, repositorio) se descubren por escaneo
 * de componentes desde {@code com.micoach} (app).
 */
@Configuration
public class AiConfig {

    @Bean
    AiProviderStrategy groqProviderStrategy() {
        return new OpenAiCompatibleProviderStrategy("groq");
    }

    @Bean
    AiProviderStrategy openRouterProviderStrategy() {
        return new OpenAiCompatibleProviderStrategy("openrouter");
    }
}
