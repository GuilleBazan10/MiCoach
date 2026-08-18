package com.micoach.ai.infrastructure.config;

import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.ai.application.port.in.AiUseCase.ProviderConfigData;
import com.micoach.ai.domain.AiProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seed idempotente para desarrollo local: si {@code GROQ_API_KEY} está en el entorno
 * (.env, nunca versionado) y el proveedor Groq todavía no tiene ninguna key cargada
 * (ej. base de datos recién creada por las migraciones, sin pasar por /admin todavía),
 * la carga cifrada y activa el proveedor — evita tener que volver a cargarla a mano
 * cada vez que se recrea la base. Si ya hay una key (porque este seed ya corrió antes,
 * o porque se cargó manualmente desde /admin), no toca nada.
 */
@Component
class GroqApiKeyBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(GroqApiKeyBootstrap.class);

    private final AiUseCase aiUseCase;
    private final String groqApiKey;

    GroqApiKeyBootstrap(AiUseCase aiUseCase, @Value("${micoach.ai.bootstrap.groq-api-key:}") String groqApiKey) {
        this.aiUseCase = aiUseCase;
        this.groqApiKey = groqApiKey;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (groqApiKey == null || groqApiKey.isBlank()) {
            return;
        }
        AiProviderConfig groq = aiUseCase.listProviderConfigs().stream()
                .filter(c -> "groq".equals(c.getProvider()))
                .findFirst()
                .orElse(null);
        if (groq == null || groq.hasApiKey()) {
            return;
        }
        aiUseCase.updateProviderConfig("groq",
                new ProviderConfigData(groq.getDisplayName(), groq.getBaseUrl(), groq.getModel(), groqApiKey, true));
        aiUseCase.activateProvider("groq");
        log.info("Groq configurado automáticamente desde GROQ_API_KEY (.env) — no había ninguna key cargada.");
    }
}
