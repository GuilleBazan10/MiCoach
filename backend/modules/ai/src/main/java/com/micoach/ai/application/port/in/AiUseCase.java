package com.micoach.ai.application.port.in;

import com.micoach.ai.domain.AiProviderConfig;
import com.micoach.ai.domain.ChatMessage;
import com.micoach.ai.domain.Conversation;
import com.micoach.ai.domain.GenerationLog;
import com.micoach.ai.domain.Prompt;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada del módulo ai: config de proveedores (panel admin), prompts
 * versionados, conversaciones, auditoría de generación y la generación en sí.
 */
public interface AiUseCase {

    // ------------------------- Config de proveedores (admin) -------------------------

    List<AiProviderConfig> listProviderConfigs();

    AiProviderConfig updateProviderConfig(String provider, ProviderConfigData data);

    AiProviderConfig activateProvider(String provider);

    ProviderTestResult testProvider(String provider);

    // ------------------------- Prompts -------------------------

    List<Prompt> listPrompts(String slug, boolean activeOnly);

    Prompt getPrompt(Long promptId);

    Prompt createPrompt(PromptData data);

    Prompt setPromptActive(Long promptId, boolean active);

    // ------------------------- Conversaciones -------------------------

    List<Conversation> listConversations(Long userId, String topic);

    Conversation getConversation(Long userId, Long conversationId);

    Conversation createConversation(Long userId, String topic);

    Conversation archiveConversation(Long userId, Long conversationId);

    ChatMessage addMessage(Long userId, Long conversationId, MessageData data);

    // ------------------------- Auditoría -------------------------

    List<GenerationLog> listGenerationLogs(GenerationLogFilter filter);

    // ------------------------- Generación -------------------------

    /**
     * Ejecuta el prompt activo de {@code promptSlug} con las {@code variables} dadas
     * (reemplazo simple {@code {{clave}}}) contra el proveedor de IA configurado
     * (Strategy Pattern, ver {@code micoach.ai.provider}). Audita el intento en
     * {@code ai_generation_logs}, éxito o error.
     */
    GenerationResult generate(Long userId, String promptSlug, Map<String, Object> variables);

    record PromptData(String slug, String provider, String model, String content, Map<String, Object> params) {
    }

    /** {@code apiKey} null o vacío = no tocar la key ya guardada. */
    record ProviderConfigData(String displayName, String baseUrl, String model, String apiKey, boolean enabled) {
    }

    record ProviderTestResult(boolean ok, String message) {
    }

    record GenerationResult(String rawOutput, String provider, String model, Integer durationMs) {
    }

    record MessageData(String role, String content, String provider, String model,
                       Map<String, Object> tokenUsage) {
    }

    record GenerationLogFilter(Long userId, String promptSlug) {
    }
}
