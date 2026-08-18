-- =====================================================================
-- MiCoach — V20: segunda imagen del ejercicio (posición final/contraída
-- del movimiento). docs/10-recomendaciones-coach-nutricion.md § H.6:
-- free-exercise-db trae 0.jpg (inicial) y 1.jpg (final) por ejercicio,
-- pero V13 solo guardó la primera — "con una no se puede entender nada"
-- (ej. una sentadilla parada no comunica la profundidad del movimiento
-- sin ver también la posición abajo).
--
-- Backfill determinístico: todas las filas sembradas desde free-exercise-db
-- tienen image_url con el patrón .../exercises/{X}/0.jpg, así que
-- REPLACE(...,'0.jpg','1.jpg') cubre la gran mayoría sin repetir el mapeo
-- manual ejercicio por ejercicio que sí hizo falta en V13. No todos los
-- ejercicios del dataset tienen 1.jpg (poco frecuente) — queda null para
-- esos y el frontend ya maneja el caso de que falte cualquiera de las dos.
-- =====================================================================
ALTER TABLE workout_exercises ADD COLUMN image_url_end VARCHAR(500);

UPDATE workout_exercises
SET image_url_end = REPLACE(image_url, '/0.jpg', '/1.jpg')
WHERE image_url LIKE '%/0.jpg';
