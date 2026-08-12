-- =====================================================================
-- KineticOs — V5: Schema ai
-- Módulo: ai (prompts versionados, chat, auditoría de generaciones)
-- Documentación: docs/02-database.md
-- =====================================================================

-- Prompts versionados por slug (el texto puede vivir aquí o en recursos).
CREATE TABLE ai_prompts (
    id         BIGSERIAL    PRIMARY KEY,
    slug       VARCHAR(100) NOT NULL,
    version    INTEGER      NOT NULL DEFAULT 1,
    provider   VARCHAR(20)  NOT NULL DEFAULT 'ollama',
    model      VARCHAR(100) NOT NULL DEFAULT 'llama3.2',
    content    TEXT         NOT NULL,
    params     JSONB,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_ai_prompt_version UNIQUE (slug, version)
);

-- Conversaciones de chat del usuario.
CREATE TABLE ai_conversations (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    topic      VARCHAR(50)
        CHECK (topic IN ('nutrition', 'workout', 'general')),
    status     VARCHAR(20)  NOT NULL DEFAULT 'active'
        CHECK (status IN ('active', 'archived')),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_ai_conversations_user ON ai_conversations (user_id);

-- Mensajes de una conversación.
CREATE TABLE ai_chat_messages (
    id              BIGSERIAL    PRIMARY KEY,
    conversation_id BIGINT       NOT NULL REFERENCES ai_conversations (id) ON DELETE CASCADE,
    role            VARCHAR(10)  NOT NULL
        CHECK (role IN ('user', 'assistant', 'system', 'tool')),
    content         TEXT         NOT NULL,
    provider        VARCHAR(20),
    model           VARCHAR(100),
    token_usage     JSONB,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_ai_chat_messages_conversation ON ai_chat_messages (conversation_id, created_at);

-- Auditoría de cada generación de IA (contexto de entrada + salida + métricas).
CREATE TABLE ai_generation_logs (
    id            BIGSERIAL    PRIMARY KEY,
    user_id       BIGINT       REFERENCES auth_users (id) ON DELETE SET NULL,
    prompt_slug   VARCHAR(100) NOT NULL,
    prompt_version INTEGER,
    provider      VARCHAR(20),
    model         VARCHAR(100),
    input_context JSONB,
    output        JSONB,
    duration_ms   INTEGER,
    status        VARCHAR(20)  NOT NULL DEFAULT 'success'
        CHECK (status IN ('success', 'error', 'partial')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_ai_generation_logs_user ON ai_generation_logs (user_id, created_at);
CREATE INDEX idx_ai_generation_logs_prompt ON ai_generation_logs (prompt_slug, created_at);