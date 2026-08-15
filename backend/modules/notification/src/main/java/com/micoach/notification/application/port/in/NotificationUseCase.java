package com.micoach.notification.application.port.in;

import com.micoach.notification.domain.Notification;
import com.micoach.notification.domain.Preference;
import com.micoach.notification.domain.Reminder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada del módulo notification (avisos, recordatorios, preferencias).
 */
public interface NotificationUseCase {

    // ------------------------- Notificaciones -------------------------

    List<Notification> listNotifications(Long userId, String status);

    Notification createNotification(Long userId, NotificationData data);

    Notification markRead(Long userId, Long notificationId);

    void deleteNotification(Long userId, Long notificationId);

    // ------------------------- Recordatorios -------------------------

    List<Reminder> listReminders(Long userId);

    Reminder createReminder(Long userId, ReminderData data);

    Reminder updateReminder(Long userId, Long reminderId, ReminderData data);

    void deleteReminder(Long userId, Long reminderId);

    // ------------------------- Preferencias -------------------------

    List<Preference> listPreferences(Long userId);

    Preference setPreference(Long userId, PreferenceData data);

    record NotificationData(String type, String title, String body, Map<String, Object> data, String channel,
                            Instant scheduledAt) {
    }

    record ReminderData(String reminderType, String scheduleCron, Map<String, Object> scheduleConfig,
                        String title, String body, boolean enabled) {
    }

    record PreferenceData(String eventType, String channel, boolean enabled) {
    }
}
