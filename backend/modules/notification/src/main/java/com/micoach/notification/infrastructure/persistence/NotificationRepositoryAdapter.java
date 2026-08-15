package com.micoach.notification.infrastructure.persistence;

import com.micoach.notification.application.port.out.NotificationRepository;
import com.micoach.notification.domain.Notification;
import com.micoach.notification.domain.Preference;
import com.micoach.notification.domain.Reminder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA del puerto {@link NotificationRepository}.
 */
@Component
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository notificationRepository;
    private final ReminderJpaRepository reminderRepository;
    private final PreferenceJpaRepository preferenceRepository;

    public NotificationRepositoryAdapter(NotificationJpaRepository notificationRepository,
                                         ReminderJpaRepository reminderRepository,
                                         PreferenceJpaRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.reminderRepository = reminderRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public List<Notification> findNotifications(Long userId, String status) {
        List<NotificationJpa> notifications = status != null
                ? notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(NotificationEntityMapper::toDomain).toList();
    }

    @Override
    public Optional<Notification> findNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId).map(NotificationEntityMapper::toDomain);
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return NotificationEntityMapper.toDomain(
                notificationRepository.save(NotificationEntityMapper.toJpa(notification)));
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<Reminder> findReminders(Long userId) {
        return reminderRepository.findByUserId(userId).stream().map(ReminderMapper::toDomain).toList();
    }

    @Override
    public Optional<Reminder> findReminderById(Long reminderId) {
        return reminderRepository.findById(reminderId).map(ReminderMapper::toDomain);
    }

    @Override
    public Reminder saveReminder(Reminder reminder) {
        return ReminderMapper.toDomain(reminderRepository.save(ReminderMapper.toJpa(reminder)));
    }

    @Override
    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }

    @Override
    public List<Preference> findPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId).stream().map(PreferenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Preference> findPreference(Long userId, String eventType, String channel) {
        return preferenceRepository.findByUserIdAndEventTypeAndChannel(userId, eventType, channel)
                .map(PreferenceMapper::toDomain);
    }

    @Override
    public Preference savePreference(Preference preference) {
        return PreferenceMapper.toDomain(preferenceRepository.save(PreferenceMapper.toJpa(preference)));
    }
}
