-- =====================================================================
-- MiCoach — V24: plantillas de rutina de ejemplo (docs/06-ux-ui-audit.md
-- y feedback de usuario real: is_template existía en el schema desde
-- V2 y el dominio ya documentaba "userId nulo = plantilla global
-- (admin/IA)", pero nunca hubo forma de crear una — ni endpoint, ni
-- seed, ni UI. Esta migración siembra 3 plantillas reales y bien
-- armadas, usando exclusivamente ejercicios que ya existen en
-- workout_exercises (misma regla que le exigimos a la IA: nada
-- inventado). El endpoint para "usar" una plantilla (clonarla a rutinas
-- propias) se agrega en el backend en el mismo cambio que esta migración.
-- =====================================================================

DO $$
DECLARE
    v_workout_id BIGINT;
    v_day_id     BIGINT;
BEGIN

    -- =================================================================
    -- Plantilla 1: Full Body principiante (3 días)
    -- =================================================================
    INSERT INTO workout_workouts (user_id, name, description, objective, level, duration_weeks, is_template, is_ai_generated, status)
    VALUES (NULL, 'Full Body — Principiante',
            'Cuerpo completo 3 veces por semana. Punto de partida clásico para quien recién empieza: pocos ejercicios por día, todos los grupos musculares grandes cubiertos, progresión simple de series.',
            'general_health', 'beginner', 8, TRUE, FALSE, 'active')
    RETURNING id INTO v_workout_id;

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 1, 'Día A', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 1,  1, 3, 8, 10, 90, NULL, NULL),   -- Sentadilla
        (v_day_id, 3,  2, 3, 8, 10, 90, NULL, NULL),   -- Press de banca
        (v_day_id, 5,  3, 3, 8, 10, 90, NULL, NULL),   -- Remo con barra
        (v_day_id, 19, 4, 3, 1, 1,  45, NULL, NULL);   -- Plancha (isométrico, ver notas en la app)

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 2, 'Día B', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 13, 1, 3, 10, 12, 90, NULL, NULL),  -- Peso muerto rumano
        (v_day_id, 4,  2, 3, 8,  10, 90, NULL, NULL),  -- Press militar
        (v_day_id, 6,  3, 3, 6,  10, 90, NULL, NULL),  -- Dominadas
        (v_day_id, 20, 4, 3, 15, 20, 45, NULL, NULL);  -- Crunches abdominales

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 3, 'Día C', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 42, 1, 3, 10, 12, 90, NULL, NULL),  -- Sentadilla goblet
        (v_day_id, 38, 2, 3, 10, 12, 90, NULL, NULL),  -- Press inclinado con mancuernas
        (v_day_id, 37, 3, 3, 10, 12, 75, NULL, NULL),  -- Remo sentado en polea
        (v_day_id, 18, 4, 3, 15, 20, 45, NULL, NULL);  -- Elevaciones de gemelos

    -- =================================================================
    -- Plantilla 2: Push / Pull / Legs — Intermedio
    -- =================================================================
    INSERT INTO workout_workouts (user_id, name, description, objective, level, duration_weeks, is_template, is_ai_generated, status)
    VALUES (NULL, 'Push / Pull / Legs — Intermedio',
            'El split clásico de gimnasio para nivel intermedio: empuje, tracción y piernas separados. Pensado para 3 días por semana, repetible dos veces para llegar a 6.',
            'gain_muscle', 'intermediate', 8, TRUE, FALSE, 'active')
    RETURNING id INTO v_workout_id;

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 1, 'Push', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 3,  1, 4, 6,  8,  120, 75, NULL),  -- Press de banca
        (v_day_id, 4,  2, 3, 8,  10, 90,  70, NULL),  -- Press militar
        (v_day_id, 38, 3, 3, 10, 12, 90,  NULL, NULL),-- Press inclinado con mancuernas
        (v_day_id, 9,  4, 3, 12, 15, 60,  NULL, NULL),-- Extensiones de tríceps en polea
        (v_day_id, 10, 5, 3, 12, 15, 60,  NULL, NULL);-- Elevaciones laterales

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 2, 'Pull', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 2,  1, 3, 5,  6,  150, 80, NULL),  -- Peso muerto
        (v_day_id, 6,  2, 4, 6,  10, 90,  NULL, NULL),-- Dominadas
        (v_day_id, 5,  3, 3, 8,  10, 90,  NULL, NULL),-- Remo con barra
        (v_day_id, 36, 4, 3, 10, 12, 75,  NULL, NULL),-- Jalón al pecho
        (v_day_id, 8,  5, 3, 10, 12, 60,  NULL, NULL);-- Curl de bíceps

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 3, 'Legs', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 1,  1, 4, 6,  8,  150, 80, NULL),  -- Sentadilla
        (v_day_id, 13, 2, 3, 8,  10, 90,  NULL, NULL),-- Peso muerto rumano
        (v_day_id, 14, 3, 3, 10, 12, 75,  NULL, NULL),-- Zancadas
        (v_day_id, 17, 4, 3, 12, 15, 60,  NULL, NULL),-- Curl femoral
        (v_day_id, 18, 5, 3, 15, 20, 45,  NULL, NULL);-- Elevaciones de gemelos

    -- =================================================================
    -- Plantilla 3: Fuerza básica — estilo powerlifting (4 días)
    -- =================================================================
    INSERT INTO workout_workouts (user_id, name, description, objective, level, duration_weeks, is_template, is_ai_generated, status)
    VALUES (NULL, 'Fuerza Básica — Estilo Powerlifting',
            'Foco en los 3 levantamientos compuestos principales (sentadilla, press de banca, peso muerto) con series bajas y descansos largos, más accesorios para sostener el volumen total.',
            'strength', 'intermediate', 10, TRUE, FALSE, 'active')
    RETURNING id INTO v_workout_id;

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 1, 'Sentadilla', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 1,  1, 5, 5, 5,  180, 80, NULL),   -- Sentadilla
        (v_day_id, 3,  2, 3, 6, 8,  120, 70, NULL),   -- Press de banca
        (v_day_id, 34, 3, 3, 8, 10, 90,  NULL, NULL), -- Hip thrust
        (v_day_id, 19, 4, 3, 1, 1,  45,  NULL, NULL); -- Plancha

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 2, 'Press de banca', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 3,  1, 5, 5,  5,  180, 80, NULL),  -- Press de banca
        (v_day_id, 4,  2, 3, 6,  8,  120, 70, NULL),  -- Press militar
        (v_day_id, 5,  3, 3, 8,  10, 90,  NULL, NULL),-- Remo con barra
        (v_day_id, 9,  4, 3, 10, 12, 60,  NULL, NULL);-- Extensiones de tríceps en polea

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 3, 'Peso muerto', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 2,  1, 5, 5, 5,  180, 80, NULL),   -- Peso muerto
        (v_day_id, 67, 2, 3, 6, 8,  120, 70, NULL),   -- Sentadilla frontal
        (v_day_id, 6,  3, 3, 6, 8,  90,  NULL, NULL), -- Dominadas
        (v_day_id, 8,  4, 3, 10, 12, 60, NULL, NULL); -- Curl de bíceps

    INSERT INTO workout_workout_days (workout_id, day_index, name, is_rest_day) VALUES (v_workout_id, 4, 'Accesorios', FALSE) RETURNING id INTO v_day_id;
    INSERT INTO workout_workout_exercises (workout_day_id, exercise_id, order_index, sets, reps_min, reps_max, rest_seconds, intensity_percent, tempo) VALUES
        (v_day_id, 4,  1, 3, 8,  10, 90, NULL, NULL), -- Press militar
        (v_day_id, 83, 2, 3, 10, 12, 75, NULL, NULL), -- Remo en T
        (v_day_id, 14, 3, 3, 10, 12, 75, NULL, NULL), -- Zancadas
        (v_day_id, 17, 4, 3, 12, 15, 60, NULL, NULL); -- Curl femoral

END $$;
