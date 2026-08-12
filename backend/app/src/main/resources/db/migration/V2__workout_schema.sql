-- =====================================================================
-- KineticOs — V2: Schema workout
-- Módulo: workout (ejercicios, músculos, rutinas, sesiones)
-- Documentación: docs/02-database.md
-- =====================================================================

-- Catálogo de músculos agrupados por zona corporal.
CREATE TABLE workout_muscles (
    id           BIGSERIAL    PRIMARY KEY,
    code         VARCHAR(50)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    muscle_group VARCHAR(50)  NOT NULL
        CHECK (muscle_group IN ('chest', 'back', 'legs', 'shoulders', 'arms', 'core')),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_workout_muscle_code UNIQUE (code)
);

-- Catálogo de ejercicios (base + AI).
CREATE TABLE workout_exercises (
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(200) NOT NULL,
    description    VARCHAR(1000),
    category       VARCHAR(30)  NOT NULL
        CHECK (category IN ('strength', 'cardio', 'mobility', 'flexibility', 'hiit', 'plyometric')),
    equipment      JSONB,
    difficulty     VARCHAR(15)  NOT NULL DEFAULT 'beginner'
        CHECK (difficulty IN ('beginner', 'intermediate', 'advanced')),
    instructions   TEXT,
    video_url      VARCHAR(500),
    image_url      VARCHAR(500),
    is_ai_generated BOOLEAN     NOT NULL DEFAULT FALSE,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_workout_exercises_category ON workout_exercises (category, difficulty);

-- Músculos involucrados por ejercicio, con su rol.
CREATE TABLE workout_exercise_muscles (
    exercise_id BIGINT      NOT NULL REFERENCES workout_exercises (id) ON DELETE CASCADE,
    muscle_id   BIGINT      NOT NULL REFERENCES workout_muscles (id) ON DELETE CASCADE,
    role        VARCHAR(20) NOT NULL DEFAULT 'primary'
        CHECK (role IN ('primary', 'secondary', 'stabilizer')),
    PRIMARY KEY (exercise_id, muscle_id, role)
);

-- Rutinas/plantillas. user_id NULL = plantilla global (admin/IA).
CREATE TABLE workout_workouts (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       REFERENCES auth_users (id) ON DELETE SET NULL,
    name            VARCHAR(200) NOT NULL,
    description     VARCHAR(1000),
    objective       VARCHAR(30)
        CHECK (objective IN ('lose_fat', 'gain_muscle', 'maintain', 'endurance',
                             'strength', 'general_health')),
    level           VARCHAR(15)
        CHECK (level IN ('beginner', 'intermediate', 'advanced')),
    duration_weeks  SMALLINT,
    is_template     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_ai_generated BOOLEAN      NOT NULL DEFAULT FALSE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'active'
        CHECK (status IN ('active', 'archived')),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_workout_workouts_user ON workout_workouts (user_id);
CREATE INDEX idx_workout_workouts_template ON workout_workouts (is_template, objective);

-- Días de la rutina (1..n, con opción de día de descanso).
CREATE TABLE workout_workout_days (
    id          BIGSERIAL    PRIMARY KEY,
    workout_id  BIGINT       NOT NULL REFERENCES workout_workouts (id) ON DELETE CASCADE,
    day_index   SMALLINT     NOT NULL CHECK (day_index >= 1),
    name        VARCHAR(100),
    is_rest_day BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_workout_day UNIQUE (workout_id, day_index)
);

-- Ejercicios dentro de un día de rutina, con prescripción de series/reps.
CREATE TABLE workout_workout_exercises (
    id               BIGSERIAL  PRIMARY KEY,
    workout_day_id   BIGINT     NOT NULL REFERENCES workout_workout_days (id) ON DELETE CASCADE,
    exercise_id      BIGINT     NOT NULL REFERENCES workout_exercises (id) ON DELETE RESTRICT,
    order_index      SMALLINT   NOT NULL CHECK (order_index >= 1),
    sets             SMALLINT   NOT NULL DEFAULT 1 CHECK (sets >= 1),
    reps_min         SMALLINT,
    reps_max         SMALLINT,
    rest_seconds     SMALLINT,
    intensity_percent SMALLINT  CHECK (intensity_percent BETWEEN 1 AND 100),
    tempo            VARCHAR(30),
    notes            VARCHAR(500)
);
CREATE INDEX idx_workout_plan_exercises_day ON workout_workout_exercises (workout_day_id, order_index);

-- Sesiones realizadas (historial de entrenamiento).
CREATE TABLE workout_sessions (
    id             BIGSERIAL  PRIMARY KEY,
    user_id        BIGINT     NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    workout_id     BIGINT     REFERENCES workout_workouts (id) ON DELETE SET NULL,
    workout_day_id BIGINT     REFERENCES workout_workout_days (id) ON DELETE SET NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'in_progress'
        CHECK (status IN ('planned', 'in_progress', 'completed', 'aborted')),
    started_at     TIMESTAMPTZ,
    completed_at   TIMESTAMPTZ,
    duration_seconds INTEGER,
    notes          VARCHAR(500),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_workout_sessions_user ON workout_sessions (user_id, started_at DESC);
CREATE INDEX idx_workout_sessions_status ON workout_sessions (status);

-- Ejecución real por ejercicio dentro de una sesión.
CREATE TABLE workout_session_exercises (
    id                  BIGSERIAL    PRIMARY KEY,
    session_id          BIGINT       NOT NULL REFERENCES workout_sessions (id) ON DELETE CASCADE,
    workout_exercise_id BIGINT       REFERENCES workout_workout_exercises (id) ON DELETE SET NULL,
    exercise_id         BIGINT       NOT NULL REFERENCES workout_exercises (id) ON DELETE RESTRICT,
    sets_done           SMALLINT     NOT NULL DEFAULT 0,
    weight_kg           NUMERIC(6,2),
    reps                SMALLINT,
    rpe                 SMALLINT     CHECK (rpe BETWEEN 1 AND 10),
    duration_seconds    INTEGER,
    distance_meters     INTEGER,
    notes               VARCHAR(500)
);
CREATE INDEX idx_workout_session_exercises_session ON workout_session_exercises (session_id);