package com.kineticos.notification.presentation;

import com.kineticos.notification.domain.Notification;
import com.kineticos.notification.domain.Preference;
import com.kineticos.notification.domain.Reminder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

/**
 * DTOs del módulo notification. Cada verbose class es un contrato de entrada/salida.
 */
public final class NotificationDtos {

    private NotificationDtos() {
    }

    // ------------------------- Notificaciones -------------------------

    public record NotificationResponse(Long id, String type, String title, String body, Map<String, Object> data,
                                       String channel, String status, Instant scheduledAt, Instant sentAt,
                                       Instant readAt, String error) {

        static NotificationResponse from(Notification n) {
            return new NotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.getData(),
                    n.getChannel(), n.getStatus(), n.getScheduledAt(), n.getSentAt(), n.getReadAt(),
                    n.getError());
        }
    }

    public record NotificationRequest(@NotBlank @Size(max = 40) String type, @NotBlank @Size(max = 200) String title,
                                      @Size(max = 1000) String body, Map<String, Object> data,
                                      @NotBlank String channel, Instant scheduledAt) {
    }

    // ------------------------- Recordatorios -------------------------

    public record ReminderResponse(Long id, String reminderType, String scheduleCron,
                                   Map<String, Object> scheduleConfig, String title, String body,
                                   boolean enabled, Instant lastTriggeredAt) {

        static ReminderResponse from(Reminder r) {
            return new ReminderResponse(r.getId(), r.getReminderType(), r.getScheduleCron(),
                    r.getScheduleConfig(), r.getTitle(), r.getBody(), r.isEnabled(), r.getLastTriggeredAt());
        }
    }

    public record ReminderRequest(@NotBlank String reminderType, @Size(max = 100) String scheduleCron,
                                  Map<String, Object> scheduleConfig, @Size(max = 200) String title,
                                  @Size(max = 500) String body, boolean enabled) {
    }

    // ------------------------- Preferencias -------------------------

    public record PreferenceResponse(Long id, String eventType, String channel, boolean enabled) {

        static PreferenceResponse from(Preference p) {
            return new PreferenceResponse(p.getId(), p.getEventType(), p.getChannel(), p.isEnabled());
        }
    }

    public record PreferenceRequest(@NotBlank String eventType, @NotBlank String channel,
                                    @NotNull Boolean enabled) {
    }
}
