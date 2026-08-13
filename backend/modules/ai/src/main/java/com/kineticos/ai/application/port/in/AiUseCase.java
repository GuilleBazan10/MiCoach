package com.kineticos.ai.application.port.in;

import com.kineticos.ai.domain.ChatMessage;
import com.kineticos.ai.domain.Conversation;
import com.kineticos.ai.domain.GenerationLog;
import com.kineticos.ai.domain.Prompt;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada del módulo ai (prompts versionados, conversaciones y auditoría de
 * generación). Base técnica: sin integración real con un proveedor de IA todavía.
 */
public interface AiUseCase {

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

    record PromptData(String slug, String provider, String model, String content, Map<String, Object> params) {
    }

    record MessageData(String role, String content, String provider, String model,
                       Map<String, Object> tokenUsage) {
    }

    record GenerationLogFilter(Long userId, String promptSlug) {
    }
}
