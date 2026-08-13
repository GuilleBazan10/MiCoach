package com.kineticos.notification.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Recordatorio recurrente configurable por el usuario (tabla notification_reminders).
 */
@Getter
public class Reminder {

    private final Long id;
    private final Long userId;
    private String reminderType;
    private String scheduleCron;
    private Map<String, Object> scheduleConfig;
    private String title;
    private String body;
    private boolean enabled;
    private final Instant lastTriggeredAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Reminder(Long id, Long userId, String reminderType, String scheduleCron,
                     Map<String, Object> scheduleConfig, String title, String body, boolean enabled,
                     Instant lastTriggeredAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.reminderType = reminderType;
        this.scheduleCron = scheduleCron;
        this.scheduleConfig = scheduleConfig;
        this.title = title;
        this.body = body;
        this.enabled = enabled;
        this.lastTriggeredAt = lastTriggeredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Reminder create(Long userId, String reminderType, String scheduleCron,
                                  Map<String, Object> scheduleConfig, String title, String body,
                                  boolean enabled) {
        Instant now = Instant.now();
        return new Reminder(null, userId, reminderType, scheduleCron, scheduleConfig, title, body, enabled,
                null, now, now);
    }

    public static Reminder restore(Long id, Long userId, String reminderType, String scheduleCron,
                                   Map<String, Object> scheduleConfig, String title, String body,
                                   boolean enabled, Instant lastTriggeredAt, Instant createdAt,
                                   Instant updatedAt) {
        return new Reminder(id, userId, reminderType, scheduleCron, scheduleConfig, title, body, enabled,
                lastTriggeredAt, createdAt, updatedAt);
    }

    public void update(String reminderType, String scheduleCron, Map<String, Object> scheduleConfig,
                       String title, String body, boolean enabled) {
        this.reminderType = reminderType;
        this.scheduleCron = scheduleCron;
        this.scheduleConfig = scheduleConfig;
        this.title = title;
        this.body = body;
        this.enabled = enabled;
        this.updatedAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
