-- =====================================================================
-- MiCoach — V22: loop de retroalimentación humana sobre generaciones de
-- IA (memoria persistente que alimenta futuras generaciones, no solo
-- auditoría de solo-lectura como hasta ahora).
--
-- user_feedback en ai_generation_logs: qué hizo el usuario con lo que la
-- IA generó — 'kept' (lo conservó) o 'discarded' (lo borró). Nace NULL
-- (todavía no se sabe) y se completa cuando el usuario borra una rutina/
-- plan generado por IA (ver WorkoutService.deleteWorkout).
--
-- generation_log_id en workout_workouts: liga cada rutina generada por
-- IA con el intento de generación que la produjo, para poder marcar el
-- feedback correcto en el momento del borrado (no alcanza con "la última
-- generación de este usuario" porque puede haber generado más de una).
-- =====================================================================
ALTER TABLE ai_generation_logs
    ADD COLUMN user_feedback VARCHAR(20)
        CHECK (user_feedback IN ('kept', 'discarded'));

ALTER TABLE workout_workouts
    ADD COLUMN generation_log_id BIGINT REFERENCES ai_generation_logs (id) ON DELETE SET NULL;
