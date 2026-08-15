-- =====================================================================
-- MiCoach — V1: Schema core
-- Módulos: auth + user + admin + catálogos transversales (alérgenos y dietas)
-- Documentación: docs/02-database.md
-- Enums: VARCHAR + CHECK (fácil de evolucionar en Flyway).
-- Fechas: TIMESTAMPTZ. updated_at lo gestiona la app (JPA @PreUpdate).
-- =====================================================================

-- ------------------------------ AUTH --------------------------------

-- Identidad: solo credenciales. El perfil de salud vive en user_profiles.
CREATE TABLE auth_users (
    id                 BIGSERIAL   PRIMARY KEY,
    email              VARCHAR(255) NOT NULL,
    password_hash      VARCHAR(255) NOT NULL,
    email_verified_at  TIMESTAMPTZ,
    totp_secret        VARCHAR(255),
    totp_enabled       BOOLEAN      NOT NULL DEFAULT FALSE,
    status             VARCHAR(20)  NOT NULL DEFAULT 'active'
        CHECK (status IN ('active', 'disabled', 'blocked', 'pending_verification')),
    last_login_at      TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_auth_users_email UNIQUE (email)
);

-- Cuentas sociales (Google / Apple / Facebook) vinculadas al usuario.
CREATE TABLE auth_oauth_accounts (
    id               BIGSERIAL   PRIMARY KEY,
    user_id          BIGINT      NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    provider         VARCHAR(30) NOT NULL
        CHECK (provider IN ('google', 'apple', 'facebook')),
    provider_subject VARCHAR(255) NOT NULL,
    provider_email   VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_auth_oauth_provider_subject UNIQUE (provider, provider_subject)
);
CREATE INDEX idx_auth_oauth_user ON auth_oauth_accounts (user_id);

-- Tokens de refresco rotatorios. Solo se guarda el hash SHA-256.
CREATE TABLE auth_refresh_tokens (
    id             BIGSERIAL   PRIMARY KEY,
    user_id        BIGINT      NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    token_hash     VARCHAR(64) NOT NULL,
    expires_at     TIMESTAMPTZ NOT NULL,
    revoked_at     TIMESTAMPTZ,
    replaced_by_id BIGINT      REFERENCES auth_refresh_tokens (id),
    user_agent     VARCHAR(255),
    ip_address     VARCHAR(45),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_auth_refresh_token_hash UNIQUE (token_hash)
);
CREATE INDEX idx_auth_refresh_user ON auth_refresh_tokens (user_id, revoked_at);

-- Tokens de un solo uso: verificar email / resetear password.
CREATE TABLE auth_verification_tokens (
    id          BIGSERIAL   PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    purpose     VARCHAR(30) NOT NULL
        CHECK (purpose IN ('email_verification', 'password_reset', 'totp_reset')),
    token_hash  VARCHAR(64) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_auth_verification_token_hash UNIQUE (token_hash)
);
CREATE INDEX idx_auth_verification_user ON auth_verification_tokens (user_id, purpose);

-- ------------------------------ USER --------------------------------

-- Perfil de salud 1:1. Peso/altura aquí son valores iniciales; el
-- histórico vive en progress_entries.
CREATE TABLE user_profiles (
    id                   BIGSERIAL    PRIMARY KEY,
    user_id              BIGINT       NOT NULL UNIQUE REFERENCES auth_users (id) ON DELETE CASCADE,
    sex                  VARCHAR(10)
        CHECK (sex IN ('male', 'female', 'other')),
    birth_date           DATE,
    height_cm            NUMERIC(5,2) CHECK (height_cm > 0),
    weight_kg            NUMERIC(6,2) CHECK (weight_kg > 0),
    activity_level       VARCHAR(20)
        CHECK (activity_level IN ('sedentary', 'light', 'moderate', 'active', 'very_active')),
    experience_level     VARCHAR(20)
        CHECK (experience_level IN ('beginner', 'intermediate', 'advanced')),
    equipment            JSONB,
    training_days_per_week SMALLINT    CHECK (training_days_per_week BETWEEN 1 AND 7),
    training_minutes     SMALLINT      CHECK (training_minutes > 0),
    preferred_time       VARCHAR(20)
        CHECK (preferred_time IN ('morning', 'midday', 'afternoon', 'evening')),
    timezone             VARCHAR(50),
    tdee_calories        INTEGER       CHECK (tdee_calories > 0),
    dietary_goal         VARCHAR(20)
        CHECK (dietary_goal IN ('lose_fat', 'gain_muscle', 'maintain', 'endurance', 'health')),
    notes                VARCHAR(1000),
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Objetivos cuantificables con prioridad.
CREATE TABLE user_goals (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    goal_type    VARCHAR(30)  NOT NULL
        CHECK (goal_type IN ('lose_fat', 'gain_muscle', 'maintain_weight', 'endurance',
                             'strength', 'flexibility', 'general_health')),
    target_value NUMERIC(8,2),
    target_unit  VARCHAR(20),
    target_date  DATE,
    priority     SMALLINT     NOT NULL DEFAULT 1 CHECK (priority >= 1),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_goals_profile ON user_goals (profile_id, is_active);

-- Patologías diagnosticadas (valor normalizado en minúsculas).
CREATE TABLE user_pathologies (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    pathology    VARCHAR(150) NOT NULL,
    notes        VARCHAR(500),
    diagnosed_at DATE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_pathologies_profile ON user_pathologies (profile_id);

-- Lesiones con estado, para que la IA evite ejercicios de riesgo.
CREATE TABLE user_injuries (
    id           BIGSERIAL    PRIMARY KEY,
    profile_id   BIGINT       NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    body_part    VARCHAR(100) NOT NULL,
    injury_type  VARCHAR(150) NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'active'
        CHECK (status IN ('active', 'recovered', 'chronic')),
    notes        VARCHAR(500),
    occurred_at  DATE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_injuries_profile ON user_injuries (profile_id);

-- Medicación actual.
CREATE TABLE user_medications (
    id              BIGSERIAL    PRIMARY KEY,
    profile_id      BIGINT       NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    medication_name VARCHAR(150) NOT NULL,
    dosage          VARCHAR(100),
    schedule        VARCHAR(200),
    notes           VARCHAR(500),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_medications_profile ON user_medications (profile_id);

-- ----------------------- CATÁLOGOS TRANSVERSALES ---------------------

-- Alérgenos (referenciados por user_food_restrictions y por recetas/IA).
CREATE TABLE nutrition_allergens (
    id         BIGSERIAL    PRIMARY KEY,
    code       VARCHAR(50)  NOT NULL,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_nutrition_allergen_code UNIQUE (code)
);

-- Tipos de dieta (vegana, keto, ...).
CREATE TABLE nutrition_diets (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_nutrition_diet_code UNIQUE (code)
);

-- Alergias/intolerancias del usuario (referencia el catálogo de alérgenos).
CREATE TABLE user_food_restrictions (
    id               BIGSERIAL    PRIMARY KEY,
    profile_id       BIGINT       NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    allergen_id      BIGINT       REFERENCES nutrition_allergens (id) ON DELETE RESTRICT,
    restriction_type VARCHAR(20)  NOT NULL
        CHECK (restriction_type IN ('allergy', 'intolerance')),
    severity         VARCHAR(20)
        CHECK (severity IN ('mild', 'moderate', 'severe')),
    description      VARCHAR(500),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_food_restriction UNIQUE (profile_id, allergen_id)
);
CREATE INDEX idx_user_food_restrictions_profile ON user_food_restrictions (profile_id);

-- Preferencias de dieta (vegana, keto, ...) del usuario.
CREATE TABLE user_diet_preferences (
    id         BIGSERIAL   PRIMARY KEY,
    profile_id BIGINT      NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    diet_id    BIGINT      NOT NULL REFERENCES nutrition_diets (id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_diet_preference UNIQUE (profile_id, diet_id)
);

-- ------------------------------ ADMIN -------------------------------

-- Roles del sistema (los is_system no se pueden borrar desde la app).
CREATE TABLE admin_roles (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    is_system   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_admin_role_code UNIQUE (code)
);

-- Permisos granulares (ej: workout:read, admin:users:manage).
CREATE TABLE admin_permissions (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(80)  NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(300),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_admin_permission_code UNIQUE (code)
);

-- Asignación permisos -> rol.
CREATE TABLE admin_role_permissions (
    role_id       BIGINT      NOT NULL REFERENCES admin_roles (id) ON DELETE CASCADE,
    permission_id BIGINT      NOT NULL REFERENCES admin_permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Asignación roles -> usuario.
CREATE TABLE admin_user_roles (
    user_id     BIGINT      NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    role_id     BIGINT      NOT NULL REFERENCES admin_roles (id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, role_id)
);

-- Auditoría de operaciones críticas (datos pseudonimizados).
CREATE TABLE admin_audit_logs (
    id             BIGSERIAL    PRIMARY KEY,
    user_id        BIGINT       REFERENCES auth_users (id) ON DELETE SET NULL,
    action         VARCHAR(100) NOT NULL,
    entity_type    VARCHAR(50)  NOT NULL,
    entity_id      BIGINT,
    before         JSONB,
    after          JSONB,
    ip_address     VARCHAR(45),
    user_agent     VARCHAR(255),
    correlation_id VARCHAR(36),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_admin_audit_user ON admin_audit_logs (user_id, created_at);
CREATE INDEX idx_admin_audit_entity ON admin_audit_logs (entity_type, entity_id);
CREATE INDEX idx_admin_audit_created ON admin_audit_logs (created_at);