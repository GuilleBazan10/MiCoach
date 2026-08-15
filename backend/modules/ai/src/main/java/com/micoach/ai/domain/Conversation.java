package com.micoach.ai.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Conversación de chat del usuario (tabla ai_conversations), agregado con sus mensajes.
 */
@Getter
public class Conversation {

    private final Long id;
    private final Long userId;
    private final String topic;
    private String status;
    private final List<ChatMessage> messages;
    private final Instant createdAt;
    private Instant updatedAt;

    private Conversation(Long id, Long userId, String topic, String status, List<ChatMessage> messages,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.topic = topic;
        this.status = status;
        this.messages = messages == null ? List.of() : messages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation create(Long userId, String topic) {
        Instant now = Instant.now();
        return new Conversation(null, userId, topic, "active", List.of(), now, now);
    }

    public static Conversation restore(Long id, Long userId, String topic, String status,
                                       List<ChatMessage> messages, Instant createdAt, Instant updatedAt) {
        return new Conversation(id, userId, topic, status, messages, createdAt, updatedAt);
    }

    public void archive() {
        this.status = "archived";
        this.updatedAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
