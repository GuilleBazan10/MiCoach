package com.micoach.notification.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.notification.application.port.in.NotificationUseCase;
import com.micoach.notification.application.port.out.NotificationRepository;
import com.micoach.notification.domain.Notification;
import com.micoach.notification.domain.Preference;
import com.micoach.notification.domain.Reminder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo notification. Depende solo del puerto de
 * salida.
 */
@Service
public class NotificationService implements NotificationUseCase {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // ------------------------- Notificaciones -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Notification> listNotifications(Long userId, String status) {
        return repository.findNotifications(userId, status);
    }

    @Override
    @Transactional
    public Notification createNotification(Long userId, NotificationData data) {
        Notification notification = Notification.create(userId, data.type(), data.title(), data.body(),
                data.data(), data.channel(), data.scheduledAt());
        return repository.saveNotification(notification);
    }

    @Override
    @Transactional
    public Notification markRead(Long userId, Long notificationId) {
        Notification notification = requireOwnedNotification(userId, notificationId);
        notification.markRead();
        return repository.saveNotification(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        requireOwnedNotification(userId, notificationId);
        repository.deleteNotification(notificationId);
    }

    private Notification requireOwnedNotification(Long userId, Long notificationId) {
        Notification notification = repository.findNotificationById(notificationId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Notificación no encontrada"));
        if (!notification.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Notificación no encontrada");
        }
        return notification;
    }

    // ------------------------- Recordatorios -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Reminder> listReminders(Long userId) {
        return repository.findReminders(userId);
    }

    @Override
    @Transactional
    public Reminder createReminder(Long userId, ReminderData data) {
        Reminder reminder = Reminder.create(userId, data.reminderType(), data.scheduleCron(),
                data.scheduleConfig(), data.title(), data.body(), data.enabled());
        return repository.saveReminder(reminder);
    }

    @Override
    @Transactional
    public Reminder updateReminder(Long userId, Long reminderId, ReminderData data) {
        Reminder reminder = requireOwnedReminder(userId, reminderId);
        reminder.update(data.reminderType(), data.scheduleCron(), data.scheduleConfig(), data.title(),
                data.body(), data.enabled());
        return repository.saveReminder(reminder);
    }

    @Override
    @Transactional
    public void deleteReminder(Long userId, Long reminderId) {
        requireOwnedReminder(userId, reminderId);
        repository.deleteReminder(reminderId);
    }

    private Reminder requireOwnedReminder(Long userId, Long reminderId) {
        Reminder reminder = repository.findReminderById(reminderId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Recordatorio no encontrado"));
        if (!reminder.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Recordatorio no encontrado");
        }
        return reminder;
    }

    // ------------------------- Preferencias -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Preference> listPreferences(Long userId) {
        return repository.findPreferences(userId);
    }

    @Override
    @Transactional
    public Preference setPreference(Long userId, PreferenceData data) {
        Preference preference = repository.findPreference(userId, data.eventType(), data.channel())
                .orElseGet(() -> Preference.create(userId, data.eventType(), data.channel(), data.enabled()));
        preference.setEnabled(data.enabled());
        return repository.savePreference(preference);
    }
}
