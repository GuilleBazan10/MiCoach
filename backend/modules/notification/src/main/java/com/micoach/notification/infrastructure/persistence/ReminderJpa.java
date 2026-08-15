package com.micoach.notification.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * Entidad JPA de la tabla {@code notification_reminders}.
 */
@Entity
@Table(name = "notification_reminders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reminder_type", nullable = false)
    private String reminderType;

    @Column(name = "schedule_cron")
    private String scheduleCron;

    @Column(name = "schedule_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> scheduleConfig;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
