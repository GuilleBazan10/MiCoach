package com.kineticos.ai.infrastructure.persistence;

import com.kineticos.ai.domain.ChatMessage;
import com.kineticos.ai.domain.Conversation;
import com.kineticos.ai.domain.GenerationLog;
import com.kineticos.ai.domain.Prompt;

import java.util.List;

final class PromptMapper {

    private PromptMapper() {
    }

    static Prompt toDomain(PromptJpa jpa) {
        return Prompt.restore(jpa.getId(), jpa.getSlug(), jpa.getVersion(), jpa.getProvider(), jpa.getModel(),
                jpa.getContent(), jpa.getParams(), jpa.isActive(), jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static PromptJpa toJpa(Prompt domain) {
        return PromptJpa.builder()
                .id(domain.getId())
                .slug(domain.getSlug())
                .version(domain.getVersion())
                .provider(domain.getProvider())
                .model(domain.getModel())
                .content(domain.getContent())
                .params(domain.getParams())
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class ConversationMapper {

    private ConversationMapper() {
    }

    static Conversation toDomain(ConversationJpa jpa, List<ChatMessage> messages) {
        return Conversation.restore(jpa.getId(), jpa.getUserId(), jpa.getTopic(), jpa.getStatus(), messages,
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static ConversationJpa toJpa(Conversation domain) {
        return ConversationJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .topic(domain.getTopic())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class ChatMessageMapper {

    private ChatMessageMapper() {
    }

    static ChatMessage toDomain(ChatMessageJpa jpa) {
        return ChatMessage.restore(jpa.getId(), jpa.getConversationId(), jpa.getRole(), jpa.getContent(),
                jpa.getProvider(), jpa.getModel(), jpa.getTokenUsage(), jpa.getCreatedAt());
    }

    static ChatMessageJpa toJpa(ChatMessage domain) {
        return ChatMessageJpa.builder()
                .id(domain.getId())
                .conversationId(domain.getConversationId())
                .role(domain.getRole())
                .content(domain.getContent())
                .provider(domain.getProvider())
                .model(domain.getModel())
                .tokenUsage(domain.getTokenUsage())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class GenerationLogMapper {

    private GenerationLogMapper() {
    }

    static GenerationLog toDomain(GenerationLogJpa jpa) {
        return GenerationLog.restore(jpa.getId(), jpa.getUserId(), jpa.getPromptSlug(), jpa.getPromptVersion(),
                jpa.getProvider(), jpa.getModel(), jpa.getInputContext(), jpa.getOutput(), jpa.getDurationMs(),
                jpa.getStatus(), jpa.getCreatedAt());
    }
}
