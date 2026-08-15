-- =====================================================================
-- MiCoach — V18: Usuario admin de arranque
-- Crea una cuenta admin lista para usar (login + /admin/ai) sin depender
-- del flujo de registro ni de acceso directo a la base de datos.
-- Password: MiCoachAdmin2026! (hash BCrypt, cost 10 — igual algoritmo que
-- BCryptPasswordEncoder en SecurityConfig).
-- =====================================================================

INSERT INTO auth_users (email, password_hash, email_verified_at)
VALUES (
    'admin@micoach.dev',
    '$2a$10$YIt0L1vCfoq195PtqTAgl.luSGsyJFt..Gh3UfADRZEBs87lFsjxa',
    now()
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 4 FROM auth_users WHERE email = 'admin@micoach.dev'
ON CONFLICT DO NOTHING;
