-- Configuración de proveedores de IA (panel de admin): reemplaza la selección
-- estática por variable de entorno por una tabla editable en runtime. La API key
-- se guarda cifrada (AES/GCM) desde la capa de aplicación, nunca en texto plano.
CREATE TABLE ai_provider_configs (
    id           BIGSERIAL    PRIMARY KEY,
    provider     VARCHAR(30)  NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    base_url     VARCHAR(300),
    model        VARCHAR(100) NOT NULL,
    api_key_enc  TEXT,
    enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    is_active    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Solo puede haber un proveedor activo a la vez.
CREATE UNIQUE INDEX ai_provider_configs_single_active
    ON ai_provider_configs (is_active) WHERE is_active = TRUE;

-- Ollama queda activo por defecto para no romper el comportamiento actual.
INSERT INTO ai_provider_configs (provider, display_name, base_url, model, enabled, is_active) VALUES
    ('ollama', 'Ollama (local)', 'http://localhost:11434', 'llama3.2:1b', TRUE, TRUE),
    ('groq', 'Groq', 'https://api.groq.com/openai/v1', 'llama-3.3-70b-versatile', FALSE, FALSE),
    ('openrouter', 'OpenRouter', 'https://openrouter.ai/api/v1', 'meta-llama/llama-3.3-70b-instruct:free', FALSE, FALSE),
    ('gemini', 'Google Gemini', NULL, 'gemini-1.5-flash', FALSE, FALSE);

-- Le doy ROLE_ADMIN al usuario de prueba de la web para poder probar el panel.
INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 4 FROM auth_users WHERE email = 'web-e2e-test@kineticos.dev'
ON CONFLICT DO NOTHING;
