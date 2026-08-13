-- =====================================================================
-- KineticOs — V7: Fix de secuencias tras el seed de catálogos (V6)
-- Bug: V6 insertó filas con id explícito (ej: admin_roles id=1..4) sin
-- avanzar la secuencia SERIAL asociada. Postgres no sincroniza la secuencia
-- automáticamente cuando se inserta un id a mano, así que el próximo INSERT
-- sin id (vía JPA/IDENTITY) intenta reusar id=1 y viola la PK.
-- Descubierto al implementar POST /api/v1/admin/roles (primer create-endpoint
-- sobre una tabla de catálogo pre-sembrada).
-- =====================================================================

SELECT setval(pg_get_serial_sequence('admin_roles', 'id'), COALESCE((SELECT MAX(id) FROM admin_roles), 1));
SELECT setval(pg_get_serial_sequence('admin_permissions', 'id'), COALESCE((SELECT MAX(id) FROM admin_permissions), 1));
SELECT setval(pg_get_serial_sequence('nutrition_allergens', 'id'), COALESCE((SELECT MAX(id) FROM nutrition_allergens), 1));
SELECT setval(pg_get_serial_sequence('nutrition_diets', 'id'), COALESCE((SELECT MAX(id) FROM nutrition_diets), 1));
SELECT setval(pg_get_serial_sequence('workout_muscles', 'id'), COALESCE((SELECT MAX(id) FROM workout_muscles), 1));
SELECT setval(pg_get_serial_sequence('workout_exercises', 'id'), COALESCE((SELECT MAX(id) FROM workout_exercises), 1));
SELECT setval(pg_get_serial_sequence('nutrition_ingredients', 'id'), COALESCE((SELECT MAX(id) FROM nutrition_ingredients), 1));
SELECT setval(pg_get_serial_sequence('nutrition_recipes', 'id'), COALESCE((SELECT MAX(id) FROM nutrition_recipes), 1));
