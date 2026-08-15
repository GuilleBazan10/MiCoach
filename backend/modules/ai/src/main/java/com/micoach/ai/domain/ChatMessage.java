package com.micoach.ai.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Mensaje dentro de una conversación (tabla ai_chat_messages).
 */
@Getter
public class ChatMessage {

    private final Long id;
    private final Long conversationId;
    private final String role;
    private final String content;
    private final String provider;
    private final String model;
    private final Map<String, Object> tokenUsage;
    private final Instant createdAt;

    private ChatMessage(Long id, Long conversationId, String role, String content, String provider,
                        String model, Map<String, Object> tokenUsage, Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.provider = provider;
        this.model = model;
        this.tokenUsage = tokenUsage;
        this.createdAt = createdAt;
    }

    public static ChatMessage create(Long conversationId, String role, String content, String provider,
                                     String model, Map<String, Object> tokenUsage) {
        return new ChatMessage(null, conversationId, role, content, provider, model, tokenUsage, Instant.now());
    }

    public static ChatMessage restore(Long id, Long conversationId, String role, String content, String provider,
                                      String model, Map<String, Object> tokenUsage, Instant createdAt) {
        return new ChatMessage(id, conversationId, role, content, provider, model, tokenUsage, createdAt);
    }
}
