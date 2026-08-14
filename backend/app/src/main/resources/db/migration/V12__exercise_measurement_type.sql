-- Distingue ejercicios medidos en repeticiones de los medidos en tiempo (ej. Plancha,
-- un isométrico) — sin esto la UI mostraba "3 series · 30-60 reps" para un ejercicio
-- que en realidad se sostiene 30 a 60 SEGUNDOS, no se repite 30 a 60 veces.
ALTER TABLE workout_exercises
    ADD COLUMN measurement_type VARCHAR(10) NOT NULL DEFAULT 'reps'
        CHECK (measurement_type IN ('reps', 'duration'));

-- Único ejercicio genuinamente isométrico del catálogo actual (su propia descripción
-- ya dice "Isométrico abdominal en posición de tabla"). El resto de las menciones a
-- "mantén"/"sostén" en las instrucciones son cues de forma durante un movimiento
-- dinámico, no ejercicios de sostén puro — quedan como 'reps'.
UPDATE workout_exercises SET measurement_type = 'duration' WHERE id = 19;
