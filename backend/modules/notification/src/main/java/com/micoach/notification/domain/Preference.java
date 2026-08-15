package com.micoach.notification.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Alta/baja de notificaciones por tipo de evento y canal (tabla
 * notification_preferences). Clave lógica: (userId, eventType, channel).
 */
@Getter
public class Preference {

    private final Long id;
    private final Long userId;
    private final String eventType;
    private final String channel;
    private boolean enabled;
    private Instant updatedAt;

    private Preference(Long id, Long userId, String eventType, String channel, boolean enabled,
                       Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.channel = channel;
        this.enabled = enabled;
        this.updatedAt = updatedAt;
    }

    public static Preference create(Long userId, String eventType, String channel, boolean enabled) {
        return new Preference(null, userId, eventType, channel, enabled, Instant.now());
    }

    public static Preference restore(Long id, Long userId, String eventType, String channel, boolean enabled,
                                     Instant updatedAt) {
        return new Preference(id, userId, eventType, channel, enabled, updatedAt);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.updatedAt = Instant.now();
    }
}
