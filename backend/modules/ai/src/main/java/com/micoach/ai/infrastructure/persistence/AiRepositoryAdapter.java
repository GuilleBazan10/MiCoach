package com.micoach.ai.infrastructure.persistence;

import com.micoach.ai.application.port.in.AiUseCase.GenerationLogFilter;
import com.micoach.ai.application.port.out.AiRepository;
import com.micoach.ai.domain.AiProviderConfig;
import com.micoach.ai.domain.ChatMessage;
import com.micoach.ai.domain.Conversation;
import com.micoach.ai.domain.GenerationLog;
import com.micoach.ai.domain.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA del puerto {@link AiRepository}.
 */
@Component
public class AiRepositoryAdapter implements AiRepository {

    private final AiProviderConfigJpaRepository providerConfigRepository;
    private final PromptJpaRepository promptRepository;
    private final ConversationJpaRepository conversationRepository;
    private final ChatMessageJpaRepository chatMessageRepository;
    private final GenerationLogJpaRepository generationLogRepository;

    public AiRepositoryAdapter(AiProviderConfigJpaRepository providerConfigRepository,
                               PromptJpaRepository promptRepository,
                               ConversationJpaRepository conversationRepository,
                               ChatMessageJpaRepository chatMessageRepository,
                               GenerationLogJpaRepository generationLogRepository) {
        this.providerConfigRepository = providerConfigRepository;
        this.promptRepository = promptRepository;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.generationLogRepository = generationLogRepository;
    }

    // ------------------------- Config de proveedores -------------------------

    @Override
    public List<AiProviderConfig> findProviderConfigs() {
        return providerConfigRepository.findAllByOrderByProviderAsc().stream()
                .map(AiProviderConfigMapper::toDomain).toList();
    }

    @Override
    public Optional<AiProviderConfig> findProviderConfigByProvider(String provider) {
        return providerConfigRepository.findByProvider(provider).map(AiProviderConfigMapper::toDomain);
    }

    @Override
    public Optional<AiProviderConfig> findActiveProviderConfig() {
        return providerConfigRepository.findByActiveTrue().map(AiProviderConfigMapper::toDomain);
    }

    @Override
    public AiProviderConfig saveProviderConfig(AiProviderConfig config) {
        return AiProviderConfigMapper.toDomain(
                providerConfigRepository.save(AiProviderConfigMapper.toJpa(config)));
    }

    @Override
    public void deactivateAllProviderConfigs() {
        providerConfigRepository.deactivateAll();
    }

    // ------------------------- Prompts -------------------------

    @Override
    public List<Prompt> findPrompts(String slug, boolean activeOnly) {
        List<PromptJpa> prompts;
        if (slug != null && activeOnly) {
            prompts = promptRepository.findBySlugAndActiveTrueOrderByVersionDesc(slug);
        } else if (slug != null) {
            prompts = promptRepository.findBySlugOrderByVersionDesc(slug);
        } else if (activeOnly) {
            prompts = promptRepository.findByActiveTrueOrderBySlugAscVersionDesc();
        } else {
            prompts = promptRepository.findAllByOrderBySlugAscVersionDesc();
        }
        return prompts.stream().map(PromptMapper::toDomain).toList();
    }

    @Override
    public Optional<Prompt> findPromptById(Long promptId) {
        return promptRepository.findById(promptId).map(PromptMapper::toDomain);
    }

    @Override
    public int findMaxVersion(String slug) {
        return promptRepository.findMaxVersion(slug);
    }

    @Override
    public Prompt savePrompt(Prompt prompt) {
        return PromptMapper.toDomain(promptRepository.save(PromptMapper.toJpa(prompt)));
    }

    // ------------------------- Conversaciones -------------------------

    @Override
    public List<Conversation> findConversationsByUser(Long userId, String topic) {
        List<ConversationJpa> conversations = topic != null
                ? conversationRepository.findByUserIdAndTopicOrderByUpdatedAtDesc(userId, topic)
                : conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return conversations.stream().map(c -> ConversationMapper.toDomain(c, loadMessages(c.getId()))).toList();
    }

    @Override
    public Optional<Conversation> findConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .map(c -> ConversationMapper.toDomain(c, loadMessages(c.getId())));
    }

    @Override
    public Conversation saveConversation(Conversation conversation) {
        ConversationJpa saved = conversationRepository.save(ConversationMapper.toJpa(conversation));
        return ConversationMapper.toDomain(saved, loadMessages(saved.getId()));
    }

    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        return ChatMessageMapper.toDomain(chatMessageRepository.save(ChatMessageMapper.toJpa(message)));
    }

    private List<ChatMessage> loadMessages(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(ChatMessageMapper::toDomain).toList();
    }

    // ------------------------- Auditoría -------------------------

    @Override
    public List<GenerationLog> findGenerationLogs(GenerationLogFilter filter) {
        List<GenerationLogJpa> logs;
        if (filter.userId() != null && filter.promptSlug() != null) {
            logs = generationLogRepository.findByUserIdAndPromptSlugOrderByCreatedAtDesc(filter.userId(),
                    filter.promptSlug());
        } else if (filter.userId() != null) {
            logs = generationLogRepository.findByUserIdOrderByCreatedAtDesc(filter.userId());
        } else if (filter.promptSlug() != null) {
            logs = generationLogRepository.findByPromptSlugOrderByCreatedAtDesc(filter.promptSlug());
        } else {
            logs = generationLogRepository.findAllByOrderByCreatedAtDesc();
        }
        return logs.stream().map(GenerationLogMapper::toDomain).toList();
    }

    @Override
    public GenerationLog saveGenerationLog(GenerationLog log) {
        return GenerationLogMapper.toDomain(generationLogRepository.save(GenerationLogMapper.toJpa(log)));
    }
}
