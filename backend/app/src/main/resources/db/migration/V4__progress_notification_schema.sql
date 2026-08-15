-- =====================================================================
-- MiCoach — V4: Schema progress + notification
-- Módulos: progress (seguimiento) y notification (avisos)
-- Documentación: docs/02-database.md
-- =====================================================================

-- ----------------------------- PROGRESS ------------------------------

-- Métricas con histórico (peso, IMC, % grasa, circunferencias...).
CREATE TABLE progress_entries (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    metric_type VARCHAR(30)  NOT NULL
        CHECK (metric_type IN ('weight', 'bmi', 'body_fat', 'waist', 'chest', 'arm',
                               'hip', 'thigh', 'neck', 'calf', 'water_percent',
                               'muscle_mass', 'bone_mass', 'visceral_fat', 'resting_hr')),
    value       NUMERIC(10,2) NOT NULL,
    unit        VARCHAR(20)  NOT NULL
        CHECK (unit IN ('kg', 'cm', 'pct', 'bpm', 'points')),
    measured_at TIMESTAMPTZ  NOT NULL,
    notes       VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_progress_entries_user_metric ON progress_entries (user_id, metric_type, measured_at DESC);

-- Galería de fotos de progreso (almacenadas en MinIO).
CREATE TABLE progress_photos (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    photo_url  VARCHAR(500) NOT NULL,
    angle      VARCHAR(20)
        CHECK (angle IN ('front', 'side', 'back')),
    taken_at   TIMESTAMPTZ  NOT NULL,
    notes      VARCHAR(500),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_progress_photos_user ON progress_photos (user_id, taken_at DESC);

-- ---------------------------- NOTIFICATION ---------------------------

-- Cola de notificaciones enviadas/programadas (push, email, in-app).
CREATE TABLE notification_notifications (
    id           BIGSERIAL    PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    type         VARCHAR(40)  NOT NULL,
    title        VARCHAR(200) NOT NULL,
    body         VARCHAR(1000),
    data         JSONB,
    channel      VARCHAR(20)  NOT NULL
        CHECK (channel IN ('push', 'email', 'in_app')),
    status       VARCHAR(20)  NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'sent', 'delivered', 'failed', 'read')),
    scheduled_at TIMESTAMPTZ,
    sent_at      TIMESTAMPTZ,
    read_at      TIMESTAMPTZ,
    error        VARCHAR(500),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_notification_user_status ON notification_notifications (user_id, status);
CREATE INDEX idx_notification_scheduled ON notification_notifications (scheduled_at);

-- Recordatorios recurrentes configurables por el usuario.
CREATE TABLE notification_reminders (
    id               BIGSERIAL    PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    reminder_type    VARCHAR(30)  NOT NULL
        CHECK (reminder_type IN ('workout', 'meal', 'water', 'medication',
                                 'measurement', 'weekly_report')),
    schedule_cron    VARCHAR(100),
    schedule_config  JSONB,
    title            VARCHAR(200),
    body             VARCHAR(500),
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    last_triggered_at TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_notification_reminders_user ON notification_reminders (user_id, enabled);

-- Alta/baja por tipo de evento y canal.
CREATE TABLE notification_preferences (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    event_type VARCHAR(40)  NOT NULL
        CHECK (event_type IN ('workout_reminder', 'meal_reminder', 'achievements',
                              'news', 'tips', 'system')),
    channel    VARCHAR(20)  NOT NULL
        CHECK (channel IN ('push', 'email', 'in_app')),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_notification_preference UNIQUE (user_id, event_type, channel)
);