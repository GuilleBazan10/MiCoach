package com.kineticos.ai.application.service;

import com.kineticos.shared.error.DomainException;
import com.kineticos.shared.error.ErrorCode;
import com.kineticos.ai.application.port.in.AiUseCase;
import com.kineticos.ai.application.port.out.AiRepository;
import com.kineticos.ai.domain.ChatMessage;
import com.kineticos.ai.domain.Conversation;
import com.kineticos.ai.domain.GenerationLog;
import com.kineticos.ai.domain.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo ai. Depende solo del puerto de salida.
 */
@Service
public class AiService implements AiUseCase {

    private final AiRepository repository;

    public AiService(AiRepository repository) {
        this.repository = repository;
    }

    // ------------------------- Prompts -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Prompt> listPrompts(String slug, boolean activeOnly) {
        return repository.findPrompts(slug, activeOnly);
    }

    @Override
    @Transactional(readOnly = true)
    public Prompt getPrompt(Long promptId) {
        return repository.findPromptById(promptId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Prompt no encontrado"));
    }

    @Override
    @Transactional
    public Prompt createPrompt(PromptData data) {
        int nextVersion = repository.findMaxVersion(data.slug()) + 1;
        Prompt prompt = Prompt.create(data.slug(), nextVersion, data.provider(), data.model(), data.content(),
                data.params());
        return repository.savePrompt(prompt);
    }

    @Override
    @Transactional
    public Prompt setPromptActive(Long promptId, boolean active) {
        Prompt prompt = repository.findPromptById(promptId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Prompt no encontrado"));
        prompt.setActive(active);
        return repository.savePrompt(prompt);
    }

    // ------------------------- Conversaciones -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> listConversations(Long userId, String topic) {
        return repository.findConversationsByUser(userId, topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getConversation(Long userId, Long conversationId) {
        return requireOwnedConversation(userId, conversationId);
    }

    @Override
    @Transactional
    public Conversation createConversation(Long userId, String topic) {
        return repository.saveConversation(Conversation.create(userId, topic));
    }

    @Override
    @Transactional
    public Conversation archiveConversation(Long userId, Long conversationId) {
        Conversation conversation = requireOwnedConversation(userId, conversationId);
        conversation.archive();
        return repository.saveConversation(conversation);
    }

    @Override
    @Transactional
    public ChatMessage addMessage(Long userId, Long conversationId, MessageData data) {
        requireOwnedConversation(userId, conversationId);
        ChatMessage message = ChatMessage.create(conversationId, data.role(), data.content(), data.provider(),
                data.model(), data.tokenUsage());
        return repository.saveMessage(message);
    }

    private Conversation requireOwnedConversation(Long userId, Long conversationId) {
        Conversation conversation = repository.findConversationById(conversationId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Conversación no encontrada"));
        if (!conversation.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Conversación no encontrada");
        }
        return conversation;
    }

    // ------------------------- Auditoría -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<GenerationLog> listGenerationLogs(GenerationLogFilter filter) {
        return repository.findGenerationLogs(filter);
    }
}
