package com.kineticos.notification.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Notificación en cola (enviada/programada), tabla notification_notifications.
 */
@Getter
public class Notification {

    private final Long id;
    private final Long userId;
    private final String type;
    private final String title;
    private final String body;
    private final Map<String, Object> data;
    private final String channel;
    private String status;
    private final Instant scheduledAt;
    private Instant sentAt;
    private Instant readAt;
    private String error;
    private final Instant createdAt;

    private Notification(Long id, Long userId, String type, String title, String body,
                         Map<String, Object> data, String channel, String status, Instant scheduledAt,
                         Instant sentAt, Instant readAt, String error, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.data = data;
        this.channel = channel;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.error = error;
        this.createdAt = createdAt;
    }

    public static Notification create(Long userId, String type, String title, String body,
                                      Map<String, Object> data, String channel, Instant scheduledAt) {
        return new Notification(null, userId, type, title, body, data, channel, "pending", scheduledAt,
                null, null, null, Instant.now());
    }

    public static Notification restore(Long id, Long userId, String type, String title, String body,
                                       Map<String, Object> data, String channel, String status,
                                       Instant scheduledAt, Instant sentAt, Instant readAt, String error,
                                       Instant createdAt) {
        return new Notification(id, userId, type, title, body, data, channel, status, scheduledAt, sentAt,
                readAt, error, createdAt);
    }

    public void markRead() {
        this.status = "read";
        this.readAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
