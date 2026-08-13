package com.kineticos.notification.infrastructure.persistence;

import com.kineticos.notification.domain.Notification;
import com.kineticos.notification.domain.Preference;
import com.kineticos.notification.domain.Reminder;

final class NotificationEntityMapper {

    private NotificationEntityMapper() {
    }

    static Notification toDomain(NotificationJpa jpa) {
        return Notification.restore(jpa.getId(), jpa.getUserId(), jpa.getType(), jpa.getTitle(), jpa.getBody(),
                jpa.getData(), jpa.getChannel(), jpa.getStatus(), jpa.getScheduledAt(), jpa.getSentAt(),
                jpa.getReadAt(), jpa.getError(), jpa.getCreatedAt());
    }

    static NotificationJpa toJpa(Notification domain) {
        return NotificationJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .type(domain.getType())
                .title(domain.getTitle())
                .body(domain.getBody())
                .data(domain.getData())
                .channel(domain.getChannel())
                .status(domain.getStatus())
                .scheduledAt(domain.getScheduledAt())
                .sentAt(domain.getSentAt())
                .readAt(domain.getReadAt())
                .error(domain.getError())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class ReminderMapper {

    private ReminderMapper() {
    }

    static Reminder toDomain(ReminderJpa jpa) {
        return Reminder.restore(jpa.getId(), jpa.getUserId(), jpa.getReminderType(), jpa.getScheduleCron(),
                jpa.getScheduleConfig(), jpa.getTitle(), jpa.getBody(), jpa.isEnabled(),
                jpa.getLastTriggeredAt(), jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static ReminderJpa toJpa(Reminder domain) {
        return ReminderJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .reminderType(domain.getReminderType())
                .scheduleCron(domain.getScheduleCron())
                .scheduleConfig(domain.getScheduleConfig())
                .title(domain.getTitle())
                .body(domain.getBody())
                .enabled(domain.isEnabled())
                .lastTriggeredAt(domain.getLastTriggeredAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class PreferenceMapper {

    private PreferenceMapper() {
    }

    static Preference toDomain(PreferenceJpa jpa) {
        return Preference.restore(jpa.getId(), jpa.getUserId(), jpa.getEventType(), jpa.getChannel(),
                jpa.isEnabled(), jpa.getUpdatedAt());
    }

    static PreferenceJpa toJpa(Preference domain) {
        return PreferenceJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .eventType(domain.getEventType())
                .channel(domain.getChannel())
                .enabled(domain.isEnabled())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
