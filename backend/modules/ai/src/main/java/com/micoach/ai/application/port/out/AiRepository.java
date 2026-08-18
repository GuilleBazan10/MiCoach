package com.micoach.ai.application.port.out;

import com.micoach.ai.application.port.in.AiUseCase.GenerationLogFilter;
import com.micoach.ai.domain.*;
import java.util.List;
import java.util.Optional;

public interface AiRepository {
    List<AiProviderConfig> findProviderConfigs();
    Optional<AiProviderConfig> findProviderConfigByProvider(String provider);
    Optional<AiProviderConfig> findActiveProviderConfig();
    AiProviderConfig saveProviderConfig(AiProviderConfig config);
    void deactivateAllProviderConfigs();
    List<Prompt> findPrompts(String slug, boolean activeOnly);
    Optional<Prompt> findPromptById(Long promptId);
    int findMaxVersion(String slug);
    Prompt savePrompt(Prompt prompt);
    List<Conversation> findConversationsByUser(Long userId, String topic);
    Optional<Conversation> findConversationById(Long conversationId);
    Conversation saveConversation(Conversation conversation);
    ChatMessage saveMessage(ChatMessage message);
    List<GenerationLog> findGenerationLogs(GenerationLogFilter filter);
    GenerationLog saveGenerationLog(GenerationLog log);
    void updateGenerationLogStatus(Long logId, String status);
}
