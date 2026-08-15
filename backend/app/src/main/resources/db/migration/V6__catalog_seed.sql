-- =====================================================================
-- MiCoach — V6: Seed de catálogos
-- Roles/perisos, alérgenos, dietas, músculos, ejercicios base, alimentos,
-- recetas base. IDs explícitos para referenciar las tablas puente.
-- Documentación: docs/02-database.md
-- =====================================================================

-- ------------------------------ ADMIN --------------------------------

INSERT INTO admin_roles (id, code, name, description, is_system) VALUES
    (1, 'ROLE_USER',       'Usuario',        'Usuario estándar registrado',             TRUE),
    (2, 'ROLE_PREMIUM',    'Usuario Premium','Usuario con funciones premium',           TRUE),
    (3, 'ROLE_MODERATOR',  'Moderador',      'Modera contenidos del catálogo',          TRUE),
    (4, 'ROLE_ADMIN',      'Administrador',  'Acceso total a la plataforma',            TRUE);

INSERT INTO admin_permissions (id, code, name, description) VALUES
    (1,  'workout:read',   'Leer entrenamiento',  'Ver ejercicios, rutinas y sesiones'),
    (2,  'workout:write',  'Editar entrenamiento','Crear/modificar rutinas y registrar sesiones'),
    (3,  'nutrition:read', 'Leer alimentación',   'Ver recetas, planes y sustituciones'),
    (4,  'nutrition:write','Editar alimentación', 'Crear/modificar planes y registrar consumo'),
    (5,  'progress:read',  'Leer progreso',       'Ver métricas y fotos de progreso'),
    (6,  'progress:write', 'Editar progreso',     'Registrar métricas y fotos'),
    (7,  'user:read',      'Leer perfil',         'Ver el propio perfil de salud'),
    (8,  'user:write',     'Editar perfil',       'Modificar el propio perfil de salud'),
    (9,  'ai:use',         'Usar IA',             'Chat y generación con IA'),
    (10, 'admin:users:manage', 'Gestionar usuarios',  'Administrar cuentas de usuarios'),
    (11, 'admin:catalog:manage','Gestionar catálogos','Alta/baja de ejercicios, recetas, alimentos'),
    (12, 'admin:roles:manage', 'Gestionar roles',     'Asignar roles y permisos'),
    (13, 'admin:audit:read',   'Leer auditoría',      'Consultar logs de auditoría');

INSERT INTO admin_role_permissions (role_id, permission_id) VALUES
    -- ROLE_USER
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),
    -- ROLE_PREMIUM (mismo set base)
    (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9),
    -- ROLE_MODERATOR (añade gestión de catálogo)
    (3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 11),
    -- ROLE_ADMIN (todos)
    (4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9),
    (4, 10), (4, 11), (4, 12), (4, 13);

-- --------------------------- ALÉRGENOS --------------------------------

INSERT INTO nutrition_allergens (id, code, name) VALUES
    (1,  'gluten',      'Gluten'),
    (2,  'lactose',     'Lactosa'),
    (3,  'peanut',      'Cacahuete'),
    (4,  'tree_nut',    'Frutos secos'),
    (5,  'egg',         'Huevo'),
    (6,  'soy',         'Soja'),
    (7,  'fish',        'Pescado'),
    (8,  'shellfish',   'Marisco'),
    (9,  'sesame',      'Sésamo'),
    (10, 'celery',      'Apio'),
    (11, 'mustard',     'Mostaza'),
    (12, 'sulfites',    'Sulfitos'),
    (13, 'crustacean',  'Crustáceos'),
    (14, 'mollusc',     'Moluscos');

-- ------------------------------ DIETAS --------------------------------

INSERT INTO nutrition_diets (id, code, name, description) VALUES
    (1, 'vegan',        'Vegana',        'Sin productos de origen animal'),
    (2, 'vegetarian',   'Vegetariana',   'Sin carne ni pescado'),
    (3, 'pescatarian',  'Pescetariana',  'Vegetariana más pescado'),
    (4, 'keto',         'Cetogénica',    'Baja en carbohidratos, alta en grasas'),
    (5, 'low_carb',     'Baja en carbos','Carbohidratos reducidos'),
    (6, 'gluten_free',  'Sin gluten',    'Excluye trigo, cebada y centeno'),
    (7, 'dairy_free',   'Sin lácteos',   'Excluye productos lácteos'),
    (8, 'halal',        'Halal',         'Alimentos permitidos por la ley islámica'),
    (9, 'kosher',       'Kosher',        'Alimentos permitidos por la ley judía');

-- ------------------------------ MÚSCULOS ------------------------------

INSERT INTO workout_muscles (id, code, name, muscle_group) VALUES
    (1,  'pecs',           'Pectoral',           'chest'),
    (2,  'anterior_delts', 'Deltoide anterior',  'shoulders'),
    (3,  'lateral_delts',  'Deltoide lateral',   'shoulders'),
    (4,  'posterior_delts','Deltoide posterior', 'shoulders'),
    (5,  'traps',          'Trapecio',           'shoulders'),
    (6,  'biceps',         'Bíceps',             'arms'),
    (7,  'triceps',        'Tríceps',            'arms'),
    (8,  'forearms',       'Antebrazos',         'arms'),
    (9,  'lats',           'Dorsales',           'back'),
    (10, 'rhomboids',      'Romboides',          'back'),
    (11, 'lower_back',     'Zona lumbar',        'back'),
    (12, 'quads',          'Cuádriceps',         'legs'),
    (13, 'hamstrings',     'Isquiosurales',      'legs'),
    (14, 'glutes',         'Glúteos',            'legs'),
    (15, 'calves',         'Gemelos',            'legs'),
    (16, 'abs',            'Abdominales',        'core'),
    (17, 'obliques',       'Oblicuos',           'core'),
    (18, 'adductors',      'Aductores',          'legs'),
    (19, 'neck',           'Cuello',             'core'),
    (20, 'hip_flexors',    'Flexores de cadera', 'legs');

-- ----------------------------- EJERCICIOS -----------------------------

INSERT INTO workout_exercises (id, name, description, category, equipment, difficulty, instructions)
VALUES
    (1,  'Sentadilla',            'Sentadilla con barra tras nuca.', 'strength', '["barbell","rack"]', 'beginner',     'Coloca la barra sobre el trapecio, baja flexionando rodillas y cadera hasta el paralelo, y vuelve a subir empujando con los talones.'),
    (2,  'Peso muerto',           'Levantamiento de barra desde el suelo con cadera.', 'strength', '["barbell"]', 'intermediate', 'Mantén la espalda neutra, empuja la cadera hacia atrás y sube la barra pegada a las piernas.'),
    (3,  'Press de banca',        'Empuje de barra en banco horizontal.', 'strength', '["barbell","bench"]', 'beginner',   'Acuéstate en el banco, baja la barra al pecho y empuja hacia arriba sin despegar los omóplatos.'),
    (4,  'Press militar',         'Empuje de barra por encima de la cabeza de pie.', 'strength', '["barbell"]', 'intermediate','De pie, sube la barra desde los hombros hasta extender los brazos.'),
    (5,  'Remo con barra',        'Remo inclinado con barra hacia el abdomen.', 'strength', '["barbell"]', 'beginner',    'Inclina el tronco ~45°, lleva la barra al abdomen apretando los dorsales.'),
    (6,  'Dominadas',             'Tracción en barra fija.', 'strength', '["bodyweight","pull_up_bar"]', 'intermediate','Desde suspensión total, sube hasta pasar la barbilla por encima de la barra.'),
    (7,  'Fondos en paralelas',   'Fondos de tríceps en paralelas.', 'strength', '["bodyweight","dip_bars"]', 'intermediate','Baja flexionando los codos hasta ~90° y vuelve a subir.'),
    (8,  'Curl de bíceps',        'Flexión de codo con mancuernas.', 'strength', '["dumbbell"]', 'beginner',          'Con los codos fijos al torso, sube las mancuernas hasta los hombros.'),
    (9,  'Extensiones de tríceps en polea', 'Empuje de polea alta hacia abajo.', 'strength', '["cable"]', 'beginner',  'Con codos pegados al torso, extiende los brazos hasta bloquear.'),
    (10, 'Elevaciones laterales', 'Elevación de mancuernas a los lados.', 'strength', '["dumbbell"]', 'beginner',      'Sube las mancuernas hasta la altura de los hombros sin balanceo.'),
    (11, 'Elevaciones frontales', 'Elevación de mancuernas al frente.', 'strength', '["dumbbell"]', 'beginner',        'Sube las mancuernas al frente hasta la altura de los hombros.'),
    (12, 'Remo unilateral con mancuerna', 'Remo a una mano apoyando la otra en un banco.', 'strength', '["dumbbell"]', 'intermediate', 'Apoya una mano y rodilla en el banco y rema con la mancuerna contraria.'),
    (13, 'Peso muerto rumano',    'Hip hinge con mancuernas manteniendo piernas semi-flexionadas.', 'strength', '["dumbbell"]', 'intermediate', 'Baja las mancuernas deslizándolas por los muslos empujando la cadera atrás.'),
    (14, 'Zancadas',              'Pasos alternos hacia adelante.', 'strength', '["dumbbell"]', 'beginner',            'Da un paso largo y baja hasta que ambas rodillas formen ~90°.'),
    (15, 'Prensa de piernas',     'Empuje de plataforma inclinada.', 'strength', '["machine"]', 'beginner',           'Baja la plataforma hasta ~90° de rodilla y empuja sin bloquear del todo.'),
    (16, 'Extensiones de piernas','Extensiones de cuádriceps en máquina.', 'strength', '["machine"]', 'beginner',       'Extiende las piernas hasta bloquear las rodillas.'),
    (17, 'Curl femoral',          'Flexión de rodilla en máquina.', 'strength', '["machine"]', 'beginner',             'Lleva los talones hacia los glúteos.'),
    (18, 'Elevaciones de gemelos','Subida de talones en máquina o suelo.', 'strength', '["bodyweight","machine"]', 'beginner', 'Sube sobre las puntas de los pies, aguanta y baja lentamente.'),
    (19, 'Plancha',               'Isométrico abdominal en posición de tabla.', 'strength', '["bodyweight"]', 'beginner', 'Mantén el cuerpo en línea recta apoyado en antebrazos y puntas de pies.'),
    (20, 'Crunches abdominales',  'Encogimientos abdominales en el suelo.', 'strength', '["bodyweight"]', 'beginner',  'Eleva el tronco hacia las rodillas sin tirar del cuello.'),
    (21, 'Puente de glúteo',      'Elevación de cadera en el suelo.', 'strength', '["bodyweight","barbell"]', 'beginner','Acuéstate y eleva la cadera apretando los glúteos.'),
    (22, 'Pull-over',             'Mancuerna por detrás de la cabeza en banco.', 'strength', '["dumbbell"]', 'beginner', 'Con los brazos extendidos, baja la mancuerna por detrás de la cabeza.'),
    (23, 'Face pull',             'Tracción de polea hacia la cara.', 'strength', '["cable"]', 'intermediate',          'Tira de la cuerda hacia la cara abriendo los codos.'),
    (24, 'Gato-camello',          'Movilidad de columna a cuatro patas.', 'mobility', '["bodyweight"]', 'beginner',      'Alterna arquear y redondear la espalda.'),
    (25, 'Apertura de cadera',    'Movilidad de cadera en el suelo.', 'mobility', '["bodyweight"]', 'beginner',          'Desde posición de sentadilla abierta, rota las caderas.'),
    (26, 'Carrera continua',      'Cardio de intensidad moderada.', 'cardio', '["treadmill","outdoor"]', 'beginner',    'Ritmo constante manteniendo una conversación.'),
    (27, 'Bicicleta estática',    'Cardio en bicicleta fija.', 'cardio', '["bike"]', 'beginner',                        'Pedaleo continuo con resistencia moderada.'),
    (28, 'Remo ergómetro',        'Cardio en máquina de remo.', 'cardio', '["rowing_machine"]', 'beginner',             'Empuja con piernas, inclina el tronco y tira con los brazos.'),
    (29, 'Burpees',               'Ejercicio integral con salto.', 'hiit', '["bodyweight"]', 'intermediate',            'Sentadilla → plancha → flexión → salto.'),
    (30, 'Escaladores',           'Rodillas al pecho en posición de plancha.', 'hiit', '["bodyweight"]', 'intermediate', 'En plancha, alterna llevar las rodillas al pecho a ritmo rápido.'),
    (31, 'Saltos de tijera',      'Apertura y cierre de piernas con salto.', 'hiit', '["bodyweight"]', 'beginner',      'Salta abriendo piernas y brazos, y vuelve a cerrar.'),
    (32, 'Sentadilla con salto',  'Sentadilla explosiva con salto.', 'plyometric', '["bodyweight"]', 'intermediate',    'Baja en sentadilla y salta lo más alto posible.');

INSERT INTO workout_exercise_muscles (exercise_id, muscle_id, role) VALUES
    (1, 12, 'primary'),    (1, 14, 'secondary'),   (1, 11, 'stabilizer'),  (1, 16, 'stabilizer'),
    (2, 13, 'primary'),    (2, 14, 'secondary'),   (2, 11, 'secondary'),   (2, 5, 'secondary'),   (2, 8, 'stabilizer'),
    (3, 1,  'primary'),    (3, 7,  'secondary'),   (3, 2,  'secondary'),
    (4, 2,  'primary'),    (4, 7,  'secondary'),   (4, 3,  'secondary'),   (4, 16, 'stabilizer'),
    (5, 9,  'primary'),    (5, 10, 'secondary'),   (5, 6,  'secondary'),   (5, 11, 'stabilizer'),
    (6, 9,  'primary'),    (6, 6,  'secondary'),   (6, 10, 'secondary'),
    (7, 7,  'primary'),    (7, 1,  'secondary'),   (7, 2,  'secondary'),
    (8, 6,  'primary'),    (8, 8,  'secondary'),
    (9, 7,  'primary'),
    (10, 3, 'primary'),    (10, 2, 'secondary'),
    (11, 2, 'primary'),    (11, 3, 'secondary'),
    (12, 9, 'primary'),    (12, 10, 'secondary'),  (12, 6, 'secondary'),
    (13, 13,'primary'),    (13, 14, 'secondary'),
    (14, 12,'primary'),    (14, 14, 'secondary'),  (14, 13, 'secondary'),
    (15, 12,'primary'),    (15, 14, 'secondary'),  (15, 13, 'secondary'),
    (16, 12,'primary'),
    (17, 13,'primary'),
    (18, 15,'primary'),
    (19, 16,'primary'),    (19, 17, 'secondary'),  (19, 11, 'secondary'),
    (20, 16,'primary'),    (20, 17, 'secondary'),
    (21, 14,'primary'),    (21, 13, 'secondary'),  (21, 12, 'secondary'),
    (22, 9, 'primary'),    (22, 1,  'secondary'),
    (23, 4, 'primary'),    (23, 10, 'secondary'),  (23, 5, 'secondary'),
    (24, 11,'primary'),    (24, 16, 'secondary'),
    (25, 20,'primary'),    (25, 14, 'secondary'),  (25, 18, 'secondary'),
    (26, 12,'primary'),    (26, 13, 'secondary'),  (26, 15, 'secondary'),
    (27, 12,'primary'),    (27, 13, 'secondary'),  (27, 14, 'secondary'),
    (28, 9, 'primary'),    (28, 10, 'secondary'),  (28, 12, 'secondary'),
    (29, 12,'primary'),    (29, 16, 'secondary'),  (29, 1,  'secondary'),
    (30, 16,'primary'),    (30, 12, 'secondary'),  (30, 2,  'secondary'),
    (31, 15,'primary'),    (31, 16, 'secondary'),  (31, 3,  'secondary'),
    (32, 12,'primary'),    (32, 14, 'secondary'),  (32, 15, 'secondary');

-- ----------------------------- ALIMENTOS ------------------------------

INSERT INTO nutrition_ingredients (id, name, category, base_unit, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g) VALUES
    (1,  'Pechuga de pollo',          'proteins',   'g',  165, 31,  0,    3.6,  0),
    (2,  'Pechuga de pavo',           'proteins',   'g',  135, 29,  0,    1.5,  0),
    (3,  'Carne de res magra',        'proteins',   'g',  250, 26,  0,   15,   0),
    (4,  'Carne molida de pavo (93%)','proteins',   'g',  176, 20,  0,   10,   0),
    (5,  'Atún en lata (al agua)',    'proteins',   'g',  116, 26,  0,    1,    0),
    (6,  'Salmón',                    'proteins',   'g',  208, 20,  0,   13,   0),
    (7,  'Merluza',                   'proteins',   'g',   82, 18,  0,    1,    0),
    (8,  'Huevo',                     'proteins',   'g',  143, 13,  1.1,  9.5,  0),
    (9,  'Claras de huevo',           'proteins',   'g',   52, 11,  0.7,  0.2,  0),
    (10, 'Tofu',                      'proteins',   'g',   76,  8,  1.9,  4.8,  0.3),
    (11, 'Tempeh',                    'proteins',   'g',  192, 20,  7.6, 11,   0),
    (12, 'Proteína de suero (whey)',  'proteins',   'g',  400, 78,  8,    6,    2),
    (13, 'Yogur griego',              'dairy',      'g',   97,  9,  3.9,  5,    0),
    (14, 'Queso blanco descremado',   'dairy',      'g',   98, 11,  3.4,  4.3,  0),
    (15, 'Jamón de pavo',             'proteins',   'g',  107, 18,  1.5,  3,    0),
    (16, 'Camarones',                 'proteins',   'g',   99, 24,  0.2,  0.3,  0),
    (17, 'Arroz blanco cocido',       'grains',     'g',  130,  2.7, 28,  0.3,  0.4),
    (18, 'Arroz integral cocido',     'grains',     'g',  111,  2.6, 23,  0.9,  1.8),
    (19, 'Avena',                     'grains',     'g',  389, 16.9, 66,  6.9, 10.6),
    (20, 'Quinoa cocida',             'grains',     'g',  120,  4.4, 21,  1.9,  2.8),
    (21, 'Patata hervida',            'carbs',      'g',   87,  1.9, 20,  0.1,  1.8),
    (22, 'Boniato',                   'carbs',      'g',   86,  1.6, 20,  0.1,  3),
    (23, 'Pasta integral cocida',     'grains',     'g',  124,  5.3, 25,  0.8,  4.5),
    (24, 'Pan integral',              'grains',     'g',  247, 13,  41,  3.4,  7),
    (25, 'Tortilla de maíz',          'grains',     'g',  218,  5.7, 45,  3.1,  4.4),
    (26, 'Garbanzos cocidos',         'legumes',    'g',  164,  8.9, 27,  2.6,  7.6),
    (27, 'Lentejas cocidas',          'legumes',    'g',  116,  9,  20,  0.4,  7.9),
    (28, 'Porotos negros cocidos',    'legumes',    'g',  132,  8.9, 24,  0.5,  8.7),
    (29, 'Brócoli',                   'vegetables', 'g',   34,  2.8,  7,  0.4,  2.6),
    (30, 'Espinaca',                  'vegetables', 'g',   23,  2.9,  3.6,  0.4,  2.2),
    (31, 'Zanahoria',                 'vegetables', 'g',   41,  0.9, 10,  0.2,  2.8),
    (32, 'Tomate',                    'vegetables', 'g',   18,  0.9,  3.9,  0.2,  1.2),
    (33, 'Pepino',                    'vegetables', 'g',   15,  0.7,  3.6,  0.1,  0.5),
    (34, 'Pimiento rojo',             'vegetables', 'g',   31,  1,    6,  0.3,  2.1),
    (35, 'Cebolla',                   'vegetables', 'g',   40,  1.1,  9.3,  0.1,  1.7),
    (36, 'Calabacín',                 'vegetables', 'g',   17,  1.2,  3.1,  0.3,  1),
    (37, 'Coliflor',                  'vegetables', 'g',   25,  1.9,  5,   0.3,  2),
    (38, 'Choclo (maíz)',             'vegetables', 'g',   86,  3.3, 19,  1.4,  2.7),
    (39, 'Banana',                    'fruits',     'g',   89,  1.1, 23,  0.3,  2.6),
    (40, 'Manzana',                   'fruits',     'g',   52,  0.3, 14,  0.2,  2.4),
    (41, 'Frutilla',                  'fruits',     'g',   32,  0.7,  7.7,  0.3,  2),
    (42, 'Arándanos',                 'fruits',     'g',   57,  0.7, 14,  0.3,  2.4),
    (43, 'Naranja',                   'fruits',     'g',   47,  0.9, 12,  0.1,  2.4),
    (44, 'Pera',                      'fruits',     'g',   57,  0.4, 15,  0.1,  3.1),
    (45, 'Aceite de oliva',           'fats',       'ml', 884,  0,    0, 100,   0),
    (46, 'Palta (aguacate)',          'fats',       'g',  160,  2,    9, 15,    7),
    (47, 'Almendras',                 'nuts',       'g',  579, 21,   22, 50,   12.5),
    (48, 'Nueces',                    'nuts',       'g',  654, 15,   14, 65,    6.7),
    (49, 'Mantequilla de maní',       'nuts',       'g',  588, 25,   20, 50,    6),
    (50, 'Semillas de chía',          'nuts',       'g',  486, 17,   42, 31,   34),
    (51, 'Leche descremada',          'dairy',      'ml',  34,  3.4,  5,  0.1,  0),
    (52, 'Leche de almendras',        'dairy',      'ml',  17,  0.4,  0.3, 1.1,  0.3),
    (53, 'Miel',                      'condiments', 'g',  304,  0.3, 82,  0,    0),
    (54, 'Salsa de soja',             'condiments', 'ml',  53,  8,  4.9,  0,    0.8),
    (55, 'Mostaza',                   'condiments', 'g',   66,  4.4,  5,  3.3,  3.2),
    (56, 'Cacao en polvo sin azúcar', 'condiments', 'g',  228, 19.6, 58, 13.7, 33),
    (57, 'Levadura nutricional',      'other',      'g',  350, 50,   35,  5,   20),
    (58, 'Sal',                       'other',      'g',    0,  0,    0,  0,    0),
    (59, 'Pimienta',                  'other',      'g',  255, 10,   65,  3,   27),
    (60, 'Limón',                     'fruits',     'g',   29,  1.1,  9.3,  0.3,  2.8);

-- ------------------------------ RECETAS -------------------------------

INSERT INTO nutrition_recipes (id, name, description, meal_category, difficulty, servings,
                               prep_time_min, cook_time_min,
                               calories_per_serving, protein_per_serving, carbs_per_serving,
                               fat_per_serving, fiber_per_serving, instructions)
VALUES
    (1, 'Avena con banana y frutilla', 'Avena fría con fruta y un toque de miel.', 'breakfast', 'easy', 1, 5, 0,
     372.1, 16.1, 69.9, 4.6, 8.5, 'Mezcla la avena con la leche y deja reposar 5 minutos. Corta la banana y la frutilla, añade y endulza con miel.'),
    (2, 'Tostada integral con palta y huevo', 'Tostada con palta y huevo para empezar el día.', 'breakfast', 'easy', 1, 5, 5,
     303.3, 15.5, 30.4, 14.3, 7.9, 'Tuesta el pan, aplasta la palta encima y corona con el huevo frito o pochado y rodajas de tomate.'),
    (3, 'Pechuga de pollo con arroz integral y brócoli', 'Comida equilibrada alta en proteína.', 'lunch', 'easy', 1, 10, 20,
     536.4, 53.2, 41.5, 17.2, 5.3, 'Cocina el arroz y el brócoli al vapor. Sella la pechuga con un hilo de aceite y salpimienta.'),
    (4, 'Quinoa bowl con atún', 'Bowl frío de quinoa, atún y vegetales.', 'lunch', 'easy', 1, 10, 15,
     363.5, 34.2, 36.5, 9.1, 5.9, 'Mezcla la quinoa con el atún, el tomate, el pepino y la espinaca. Aliña con aceite y limón.'),
    (5, 'Merluza al horno con boniato y calabacín', 'Plato ligero de pescado al horno.', 'dinner', 'easy', 1, 10, 30,
     360.3, 30.7, 34.0, 12.0, 5.8, 'Corta el boniato y el calabacín en rodajas, rocía con aceite y hornea con la merluza durante 25-30 minutos.'),
    (6, 'Lentejas estofadas con zanahoria', 'Guiso de lentejas vegetariano.', 'dinner', 'easy', 1, 10, 35,
     342.5, 20.3, 55.1, 6.1, 19.5, 'Rehoga la cebolla y la zanahoria, añade las lentejas y el agua, y cocina a fuego bajo hasta que estén tiernas. Añade salsa de soja.'),
    (7, 'Yogur griego con almendras y arándanos', 'Snack cremoso con frutos secos.', 'snack', 'easy', 1, 5, 0,
     284.1, 18.0, 15.9, 17.6, 3.5, 'Sirve el yogur en un bowl, agrega las almendras y los arándanos.'),
    (8, 'Rollitos de banana con mantequilla de maní', 'Snack energético de tortilla con banana.', 'snack', 'easy', 1, 5, 0,
     231.5, 7.6, 28.9, 11.3, 3.7, 'Unta la mantequilla de maní en la tortilla, coloca la banana y enrolla. Corta en rodajas.'),
    (9, 'Chocolate caliente con cacao', 'Bebida caliente baja en grasa.', 'dessert', 'easy', 1, 5, 3,
     132.6, 9.8, 26.9, 2.3, 5.0, 'Calienta la leche, disuelve el cacao y la miel, y remueve hasta integrar.'),
    (10, 'Pudín de chía con frutilla', 'Pudín de chía sin cocción.', 'dessert', 'easy', 1, 10, 0,
     139.8, 4.4, 14.9, 7.5, 8.7, 'Mezcla la chía con la leche de almendras y deja reposar en heladera. Añade la frutilla al servir.'),
    (11, 'Batido proteico de banana', 'Batido de proteína para después de entrenar.', 'drink', 'easy', 1, 5, 0,
     277.0, 31.3, 35.4, 2.3, 3.2, 'Licúa la proteína con la banana y la leche hasta obtener una textura homogénea.'),
    (12, 'Smoothie verde', 'Batido verde refrescante.', 'drink', 'easy', 1, 5, 0,
     136.1, 3.0, 18.4, 6.5, 5.0, 'Licúa la espinaca, la banana, la palta y la leche de almendras. Sirve frío.');

INSERT INTO nutrition_recipe_ingredients (recipe_id, ingredient_id, amount, unit, order_index) VALUES
    -- R1 Avena con banana y frutilla
    (1, 19, 60,   'g',  1),
    (1, 39, 50,   'g',  2),
    (1, 41, 40,   'g',  3),
    (1, 51, 150,  'ml', 4),
    (1, 53, 10,   'g',  5),
    -- R2 Tostada integral con palta y huevo
    (2, 24, 60,   'g',  1),
    (2, 46, 50,   'g',  2),
    (2, 8,  50,   'g',  3),
    (2, 32, 20,   'g',  4),
    -- R3 Pechuga de pollo con arroz integral y brócoli
    (3, 1,  150,  'g',  1),
    (3, 18, 150,  'g',  2),
    (3, 29, 100,  'g',  3),
    (3, 45, 10,   'ml', 4),
    -- R4 Quinoa bowl con atún
    (4, 20, 150,  'g',  1),
    (4, 5,  100,  'g',  2),
    (4, 32, 50,   'g',  3),
    (4, 33, 30,   'g',  4),
    (4, 30, 30,   'g',  5),
    (4, 45, 5,    'ml', 6),
    (4, 60, 10,   'g',  7),
    -- R5 Merluza al horno con boniato y calabacín
    (5, 7,  150,  'g',  1),
    (5, 22, 150,  'g',  2),
    (5, 36, 100,  'g',  3),
    (5, 45, 10,   'ml', 4),
    (5, 60, 10,   'g',  5),
    -- R6 Lentejas estofadas con zanahoria
    (6, 27, 200,  'g',  1),
    (6, 31, 100,  'g',  2),
    (6, 35, 50,   'g',  3),
    (6, 54, 10,   'ml', 4),
    (6, 45, 5,    'ml', 5),
    -- R7 Yogur griego con almendras y arándanos
    (7, 13, 150,  'g',  1),
    (7, 47, 20,   'g',  2),
    (7, 42, 40,   'g',  3),
    -- R8 Rollitos de banana con mantequilla de maní
    (8, 25, 40,   'g',  1),
    (8, 49, 20,   'g',  2),
    (8, 39, 30,   'g',  3),
    -- R9 Chocolate caliente con cacao
    (9, 51, 200,  'ml', 1),
    (9, 56, 15,   'g',  2),
    (9, 53, 10,   'g',  3),
    -- R10 Pudín de chía con frutilla
    (10, 50, 20,  'g',  1),
    (10, 41, 80,  'g',  2),
    (10, 52, 100, 'ml', 3),
    -- R11 Batido proteico de banana
    (11, 12, 30,  'g',  1),
    (11, 39, 100, 'g',  2),
    (11, 51, 200, 'ml', 3),
    -- R12 Smoothie verde
    (12, 30, 40,  'g',  1),
    (12, 39, 60,  'g',  2),
    (12, 46, 30,  'g',  3),
    (12, 52, 150, 'ml', 4);