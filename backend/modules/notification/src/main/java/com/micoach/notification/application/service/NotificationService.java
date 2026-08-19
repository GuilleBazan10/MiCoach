package com.micoach.notification.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.AuditLogEvent;
import com.micoach.notification.application.port.in.NotificationUseCase;
import com.micoach.notification.application.port.out.NotificationRepository;
import com.micoach.notification.domain.Notification;
import com.micoach.notification.domain.Preference;
import com.micoach.notification.domain.Reminder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo notification. Depende solo del puerto de
 * salida.
 */
@Slf4j
@Service
public class NotificationService implements NotificationUseCase {

    private final NotificationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(NotificationRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
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
        log.info("Creando notificación para el usuario ID: {} (Tipo: {}, Canal: {})", userId, data.type(), data.channel());
        Notification notification = Notification.create(userId, data.type(), data.title(), data.body(),
                data.data(), data.channel(), data.scheduledAt());
        Notification saved = repository.saveNotification(notification);
        
        log.info("Notificación creada exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "NOTIFICATION_CREATE", "NOTIFICATION", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public Notification markRead(Long userId, Long notificationId) {
        log.info("Marcando notificación ID: {} como leída para el usuario ID: {}", notificationId, userId);
        Notification notification = requireOwnedNotification(userId, notificationId);
        notification.markRead();
        Notification saved = repository.saveNotification(notification);
        
        log.info("Notificación ID: {} marcada como leída exitosamente para el usuario ID: {}", notificationId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "NOTIFICATION_MARK_READ", "NOTIFICATION", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("Eliminando notificación ID: {} para el usuario ID: {}", notificationId, userId);
        requireOwnedNotification(userId, notificationId);
        repository.deleteNotification(notificationId);
        
        log.info("Notificación ID: {} eliminada exitosamente para el usuario ID: {}", notificationId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "NOTIFICATION_DELETE", "NOTIFICATION", notificationId));
    }

    private Notification requireOwnedNotification(Long userId, Long notificationId) {
        Notification notification = repository.findNotificationById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Operación fallida: notificación ID {} no encontrada para el usuario ID: {}", notificationId, userId);
                    return new DomainException(404, ErrorCode.NOT_FOUND, "Notificación no encontrada");
                });
        if (!notification.belongsTo(userId)) {
            log.warn("Operación fallida: la notificación ID {} no pertenece al usuario ID: {}", notificationId, userId);
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
        log.info("Creando recordatorio para el usuario ID: {} (Tipo: {})", userId, data.reminderType());
        Reminder reminder = Reminder.create(userId, data.reminderType(), data.scheduleCron(),
                data.scheduleConfig(), data.title(), data.body(), data.enabled());
        Reminder saved = repository.saveReminder(reminder);
        
        log.info("Recordatorio creado exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "REMINDER_CREATE", "REMINDER", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public Reminder updateReminder(Long userId, Long reminderId, ReminderData data) {
        log.info("Actualizando recordatorio ID: {} para el usuario ID: {}", reminderId, userId);
        Reminder reminder = requireOwnedReminder(userId, reminderId);
        reminder.update(data.reminderType(), data.scheduleCron(), data.scheduleConfig(), data.title(),
                data.body(), data.enabled());
        Reminder saved = repository.saveReminder(reminder);
        
        log.info("Recordatorio ID: {} actualizado exitosamente para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "REMINDER_UPDATE", "REMINDER", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteReminder(Long userId, Long reminderId) {
        log.info("Eliminando recordatorio ID: {} para el usuario ID: {}", reminderId, userId);
        requireOwnedReminder(userId, reminderId);
        repository.deleteReminder(reminderId);
        
        log.info("Recordatorio ID: {} eliminado exitosamente para el usuario ID: {}", reminderId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "REMINDER_DELETE", "REMINDER", reminderId));
    }

    private Reminder requireOwnedReminder(Long userId, Long reminderId) {
        Reminder reminder = repository.findReminderById(reminderId)
                .orElseThrow(() -> {
                    log.warn("Operación fallida: recordatorio ID {} no encontrado para el usuario ID: {}", reminderId, userId);
                    return new DomainException(404, ErrorCode.NOT_FOUND, "Recordatorio no encontrado");
                });
        if (!reminder.belongsTo(userId)) {
            log.warn("Operación fallida: el recordatorio ID {} no pertenece al usuario ID: {}", reminderId, userId);
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
        log.info("Estableciendo preferencia de notificación para el usuario ID: {} (Tipo de evento: {}, Canal: {}, Habilitado: {})", 
                userId, data.eventType(), data.channel(), data.enabled());
        Preference preference = repository.findPreference(userId, data.eventType(), data.channel())
                .orElseGet(() -> Preference.create(userId, data.eventType(), data.channel(), data.enabled()));
        preference.setEnabled(data.enabled());
        Preference saved = repository.savePreference(preference);
        
        log.info("Preferencia establecida exitosamente para el usuario ID: {}", userId);
        return saved;
    }
}
