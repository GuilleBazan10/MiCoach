-- =====================================================================
-- KineticOs — V10: amplía el catálogo de ejercicios y recetas (Fase 4:
-- más variedad real para que la generación con IA no elija siempre entre
-- los mismos ~30 ejercicios / 12 recetas). Reutiliza los 60 ingredientes
-- ya sembrados en V6 — no hace falta agregar ingredientes nuevos.
--
-- Mismo cuidado que V7: los ids se insertan a mano, así que hay que
-- resincronizar la secuencia SERIAL al final o el próximo INSERT sin id
-- explícito (por API) choca con "duplicate key" — el bug real que ya
-- pasó una vez en el módulo admin (ver docs/00-progress.md Fase 2).
-- =====================================================================

-- ----------------------------- EJERCICIOS -----------------------------

INSERT INTO workout_exercises (id, name, description, category, equipment, difficulty, instructions)
VALUES
    (33, 'Sentadilla búlgara',        'Sentadilla a una pierna con la trasera apoyada.', 'strength', '["dumbbell","bench"]', 'intermediate', 'Apoya el empeine del pie trasero en un banco y baja flexionando la pierna delantera.'),
    (34, 'Hip thrust',                'Empuje de cadera con barra apoyando la espalda en un banco.', 'strength', '["barbell","bench"]', 'intermediate', 'Apoya la espalda alta en el banco, barra sobre la cadera, y empuja apretando los glúteos arriba.'),
    (35, 'Press de pecho en máquina', 'Empuje horizontal en máquina de press.', 'strength', '["machine"]', 'beginner', 'Ajusta el asiento y empuja las manijas al frente sin bloquear del todo los codos.'),
    (36, 'Jalón al pecho',            'Tracción de polea alta hacia el pecho, sentado.', 'strength', '["cable"]', 'beginner', 'Tira de la barra hacia el pecho llevando los codos hacia abajo y atrás.'),
    (37, 'Remo sentado en polea',     'Remo horizontal en polea baja, sentado.', 'strength', '["cable"]', 'beginner', 'Tira de la cuerda hacia el abdomen manteniendo la espalda recta.'),
    (38, 'Press inclinado con mancuernas', 'Empuje de mancuernas en banco inclinado.', 'strength', '["dumbbell","bench"]', 'intermediate', 'En banco a 30-45°, empuja las mancuernas hacia arriba sin chocar en el punto alto.'),
    (39, 'Aperturas con mancuernas',  'Apertura de brazos en banco plano.', 'strength', '["dumbbell","bench"]', 'beginner', 'Con leve flexión de codos, baja los brazos en arco y vuelve a juntar arriba.'),
    (40, 'Curl martillo',             'Curl de bíceps con agarre neutro.', 'strength', '["dumbbell"]', 'beginner', 'Sube las mancuernas con las palmas mirándose entre sí, sin rotar la muñeca.'),
    (41, 'Press francés',             'Extensión de tríceps con mancuerna por detrás de la cabeza.', 'strength', '["dumbbell"]', 'intermediate', 'Con los codos fijos apuntando arriba, baja la mancuerna detrás de la cabeza y extiende.'),
    (42, 'Sentadilla goblet',         'Sentadilla sosteniendo una mancuerna contra el pecho.', 'strength', '["dumbbell"]', 'beginner', 'Sostén la mancuerna vertical contra el pecho y baja en sentadilla manteniendo el torso erguido.'),
    (43, 'Peso muerto sumo',          'Peso muerto con postura ancha.', 'strength', '["barbell"]', 'advanced', 'Con postura ancha y puntas hacia afuera, levanta la barra manteniendo la espalda neutra.'),
    (44, 'Hiperextensiones',          'Extensión de tronco en banco romano.', 'strength', '["machine","bodyweight"]', 'beginner', 'Desde la cadera flexionada, extiende el tronco hasta alinear con las piernas sin hiperextender.'),
    (45, 'Elevación de piernas colgado', 'Elevación de piernas suspendido de una barra.', 'strength', '["bodyweight","pull_up_bar"]', 'intermediate', 'Colgado de la barra, sube las piernas hacia el pecho controlando el balanceo.'),
    (46, 'Russian twist',             'Rotación de tronco sentado.', 'strength', '["bodyweight"]', 'beginner', 'Sentado con el torso inclinado atrás, rota de lado a lado tocando el suelo.'),
    (47, 'Step up',                   'Subida a un cajón o banco alternando piernas.', 'strength', '["bodyweight","dumbbell","bench"]', 'beginner', 'Sube al banco apoyando todo el pie y baja controlado, alternando la pierna guía.'),
    (48, 'Farmer''s walk',            'Caminata cargando peso en ambas manos.', 'strength', '["dumbbell"]', 'intermediate', 'Camina manteniendo el torso erguido y el core firme con una mancuerna pesada en cada mano.'),
    (49, 'Elevación de talones sentado', 'Elevación de gemelos en máquina, sentado.', 'strength', '["machine"]', 'beginner', 'Sube los talones desde flexión de rodilla, aguanta arriba y baja controlado.'),
    (50, 'Natación',                  'Cardio de bajo impacto en agua.', 'cardio', '["pool"]', 'beginner', 'Nado continuo a ritmo constante, alternando estilos si hace falta variar la intensidad.'),
    (51, 'Comba (salto de cuerda)',   'Cardio con soga.', 'cardio', '["jump_rope"]', 'beginner', 'Salta con ambos pies o alternando, manteniendo un ritmo constante.'),
    (52, 'Empuje de trineo',          'Empuje de trineo cargado.', 'strength', '["sled"]', 'advanced', 'Empuja el trineo manteniendo el torso inclinado y pasos cortos y potentes.');

INSERT INTO workout_exercise_muscles (exercise_id, muscle_id, role) VALUES
    (33, 12, 'primary'),   (33, 14, 'secondary'),  (33, 13, 'stabilizer'),
    (34, 14, 'primary'),   (34, 13, 'secondary'),
    (35, 1,  'primary'),   (35, 7,  'secondary'),  (35, 2,  'secondary'),
    (36, 9,  'primary'),   (36, 6,  'secondary'),
    (37, 9,  'primary'),   (37, 10, 'secondary'),  (37, 6,  'secondary'),
    (38, 1,  'primary'),   (38, 2,  'secondary'),  (38, 7,  'secondary'),
    (39, 1,  'primary'),
    (40, 6,  'primary'),   (40, 8,  'secondary'),
    (41, 7,  'primary'),
    (42, 12, 'primary'),   (42, 14, 'secondary'),
    (43, 13, 'primary'),   (43, 14, 'secondary'),  (43, 18, 'stabilizer'),
    (44, 11, 'primary'),   (44, 14, 'secondary'),
    (45, 16, 'primary'),   (45, 20, 'secondary'),
    (46, 17, 'primary'),   (46, 16, 'secondary'),
    (47, 12, 'primary'),   (47, 14, 'secondary'),
    (48, 8,  'primary'),   (48, 5,  'secondary'),
    (49, 15, 'primary'),
    (50, 9,  'primary'),   (50, 1,  'secondary'),  (50, 12, 'secondary'),
    (51, 15, 'primary'),   (51, 12, 'secondary'),
    (52, 12, 'primary'),   (52, 14, 'secondary');

SELECT setval(pg_get_serial_sequence('workout_exercises', 'id'), (SELECT MAX(id) FROM workout_exercises));

-- ------------------------------ RECETAS -------------------------------

INSERT INTO nutrition_recipes (id, name, description, meal_category, difficulty, servings,
                               prep_time_min, cook_time_min,
                               calories_per_serving, protein_per_serving, carbs_per_serving,
                               fat_per_serving, fiber_per_serving, instructions)
VALUES
    (13, 'Panqueques de avena y banana', 'Panqueques simples sin harina.', 'breakfast', 'easy', 1, 10, 10,
     389.0, 19.5, 61.6, 8.6, 6.0, 'Licúa la avena, la banana, el huevo y la leche. Cocina de a porciones en sartén antiadherente vuelta y vuelta.'),
    (14, 'Yogur con avena y miel', 'Yogur cremoso con avena y frutos rojos.', 'breakfast', 'easy', 1, 5, 0,
     375.2, 20.7, 51.6, 10.4, 5.4, 'Mezcla el yogur con la avena, agrega la miel y corona con los arándanos.'),
    (15, 'Wrap de claras con espinaca', 'Wrap liviano y alto en proteína.', 'breakfast', 'easy', 1, 10, 5,
     227.0, 21.5, 31.4, 2.4, 4.1, 'Cocina las claras revueltas, calienta la tortilla y arma el wrap con espinaca y tomate.'),
    (16, 'Ensalada de pollo y garbanzos', 'Ensalada completa alta en proteína.', 'lunch', 'easy', 1, 10, 15,
     516.4, 56.2, 30.8, 18.2, 8.5, 'Cocina y corta la pechuga, mezcla con los garbanzos, el tomate y el pepino, y aliña con aceite.'),
    (17, 'Salmón con quinoa y espinaca', 'Plato completo con omega-3.', 'lunch', 'medium', 1, 10, 20,
     506.4, 38.2, 34.2, 22.6, 5.3, 'Cocina la quinoa, saltea la espinaca y sirve con el salmón sellado y unas gotas de limón.'),
    (18, 'Wok de tofu y vegetales', 'Salteado asiático vegetariano.', 'lunch', 'medium', 1, 15, 15,
     358.5, 20.9, 53.1, 9.2, 8.5, 'Saltea el tofu hasta dorar, agrega los vegetales cortados y la salsa de soja, y sirve sobre el arroz.'),
    (19, 'Bowl de pavo y boniato', 'Bowl energético post-entreno.', 'lunch', 'easy', 1, 10, 20,
     404.5, 33.9, 31.8, 15.4, 5.6, 'Cocina la carne de pavo, hornea o hierve el boniato en cubos, y sirve sobre espinaca fresca.'),
    (20, 'Pechuga a la plancha con puré de boniato', 'Clásico plato equilibrado.', 'dinner', 'easy', 1, 10, 25,
     453.5, 52.5, 47.0, 6.0, 8.6, 'Sella la pechuga a la plancha. Hierve el boniato y hazlo puré. Sirve con brócoli al vapor.'),
    (21, 'Camarones salteados con calabacín', 'Salteado rápido bajo en carbohidratos.', 'dinner', 'easy', 1, 10, 10,
     277.9, 38.3, 8.0, 11.1, 2.6, 'Saltea el calabacín y el pimiento con un poco de aceite, agrega los camarones al final hasta que estén rosados.'),
    (22, 'Guiso de porotos negros', 'Guiso vegetariano abundante en fibra.', 'dinner', 'easy', 1, 10, 30,
     322.5, 19.7, 61.6, 1.4, 20.9, 'Rehoga la cebolla, agrega el tomate y la zanahoria, incorpora los porotos y cocina 15 minutos.'),
    (23, 'Tortilla de claras con vegetales', 'Tortilla liviana y alta en proteína.', 'dinner', 'easy', 1, 10, 10,
     143.0, 24.3, 9.0, 0.8, 2.7, 'Bate las claras, saltea los vegetales picados y cocina todo junto en sartén antiadherente.'),
    (24, 'Hummus de garbanzos con zanahoria', 'Dip cremoso para untar o dippear.', 'snack', 'easy', 1, 10, 0,
     424.0, 14.4, 51.9, 19.1, 14.2, 'Procesa los garbanzos con el aceite y el limón hasta lograr una pasta cremosa. Sirve con bastones de zanahoria.'),
    (25, 'Tostada de queso blanco y tomate', 'Snack simple y liviano.', 'snack', 'easy', 1, 5, 0,
     206.2, 13.8, 28.3, 4.3, 4.8, 'Tuesta el pan, unta el queso blanco y corona con rodajas de tomate.'),
    (26, 'Mousse de cacao y palta', 'Postre cremoso sin azúcar refinada.', 'dessert', 'easy', 1, 10, 0,
     274.9, 6.2, 37.2, 18.3, 13.6, 'Procesa la palta con el cacao, la miel y la leche de almendras hasta obtener una textura de mousse. Enfría antes de servir.'),
    (27, 'Licuado de frutilla y yogur', 'Batido refrescante para la merienda.', 'drink', 'easy', 1, 5, 0,
     241.9, 17.6, 26.8, 7.9, 2.0, 'Licúa la frutilla, el yogur, la leche y la miel hasta homogeneizar. Sirve bien frío.');

INSERT INTO nutrition_recipe_ingredients (recipe_id, ingredient_id, amount, unit, order_index) VALUES
    -- R13 Panqueques de avena y banana
    (13, 19, 50,  'g',  1), (13, 39, 100, 'g',  2), (13, 8,  50,  'g',  3), (13, 51, 100, 'ml', 4),
    -- R14 Yogur con avena y miel
    (14, 13, 150, 'g',  1), (14, 19, 40,  'g',  2), (14, 53, 15,  'g',  3), (14, 42, 50,  'g',  4),
    -- R15 Wrap de claras con espinaca
    (15, 9,  150, 'g',  1), (15, 25, 60,  'g',  2), (15, 30, 40,  'g',  3), (15, 32, 50,  'g',  4),
    -- R16 Ensalada de pollo y garbanzos
    (16, 1,  150, 'g',  1), (16, 26, 100, 'g',  2), (16, 32, 50,  'g',  3), (16, 33, 50,  'g',  4), (16, 45, 10, 'ml', 5),
    -- R17 Salmón con quinoa y espinaca
    (17, 6,  150, 'g',  1), (17, 20, 150, 'g',  2), (17, 30, 50,  'g',  3), (17, 60, 10,  'g',  4),
    -- R18 Wok de tofu y vegetales
    (18, 10, 150, 'g',  1), (18, 29, 100, 'g',  2), (18, 34, 50,  'g',  3), (18, 31, 50,  'g',  4), (18, 54, 15, 'ml', 5), (18, 18, 150, 'g', 6),
    -- R19 Bowl de pavo y boniato
    (19, 4,  150, 'g',  1), (19, 22, 150, 'g',  2), (19, 30, 50,  'g',  3),
    -- R20 Pechuga a la plancha con puré de boniato
    (20, 1,  150, 'g',  1), (20, 22, 200, 'g',  2), (20, 29, 100, 'g',  3),
    -- R21 Camarones salteados con calabacín
    (21, 16, 150, 'g',  1), (21, 36, 150, 'g',  2), (21, 34, 50,  'g',  3), (21, 45, 10,  'ml', 4),
    -- R22 Guiso de porotos negros
    (22, 28, 200, 'g',  1), (22, 35, 50,  'g',  2), (22, 32, 100, 'g',  3), (22, 31, 50,  'g',  4),
    -- R23 Tortilla de claras con vegetales
    (23, 9,  200, 'g',  1), (23, 30, 50,  'g',  2), (23, 34, 50,  'g',  3), (23, 35, 30,  'g',  4),
    -- R24 Hummus de garbanzos con zanahoria
    (24, 26, 150, 'g',  1), (24, 45, 15,  'ml', 2), (24, 60, 15,  'g',  3), (24, 31, 100, 'g',  4),
    -- R25 Tostada de queso blanco y tomate
    (25, 24, 60,  'g',  1), (25, 14, 50,  'g',  2), (25, 32, 50,  'g',  3),
    -- R26 Mousse de cacao y palta
    (26, 46, 100, 'g',  1), (26, 56, 20,  'g',  2), (26, 53, 20,  'g',  3), (26, 52, 50,  'ml', 4),
    -- R27 Licuado de frutilla y yogur
    (27, 41, 100, 'g',  1), (27, 13, 150, 'g',  2), (27, 51, 100, 'ml', 3), (27, 53, 10,  'g',  4);

SELECT setval(pg_get_serial_sequence('nutrition_recipes', 'id'), (SELECT MAX(id) FROM nutrition_recipes));
