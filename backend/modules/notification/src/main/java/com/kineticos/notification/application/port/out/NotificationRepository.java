package com.kineticos.notification.application.port.out;

import com.kineticos.notification.domain.Notification;
import com.kineticos.notification.domain.Preference;
import com.kineticos.notification.domain.Reminder;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findNotifications(Long userId, String status);
    Optional<Notification> findNotificationById(Long notificationId);
    Notification saveNotification(Notification notification);
    void deleteNotification(Long notificationId);
    List<Reminder> findReminders(Long userId);
    Optional<Reminder> findReminderById(Long reminderId);
    Reminder saveReminder(Reminder reminder);
    void deleteReminder(Long reminderId);
    List<Preference> findPreferences(Long userId);
    Optional<Preference> findPreference(Long userId, String eventType, String channel);
    Preference savePreference(Preference preference);
}
