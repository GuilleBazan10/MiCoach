-- Completa image_url del catálogo con free-exercise-db (github.com/yuhonas/free-exercise-db),
-- dataset de dominio público (licencia Unlicense) hosteado en GitHub — sin API key, sin
-- rate limit, sin costo. Cada URL fue verificada (HTTP 200) antes de esta migración.
--
-- El dataset está en inglés y no tiene nombres en español, así que cada uno de los 52
-- ejercicios del catálogo se matcheó a mano contra el movimiento correcto (no una
-- traducción literal del nombre). La gran mayoría son el mismo ejercicio exacto; un
-- puñado son la variante más cercana disponible en el dataset, marcados abajo:
--   - id 13 (Peso muerto rumano)   -> Stiff-Legged Dumbbell Deadlift (mismo patrón, sin
--     nombre "Romanian" con mancuernas en el dataset)
--   - id 33 (Sentadilla búlgara)   -> Split Squat with Dumbbells (el dataset no tiene la
--     variante "rear foot elevated" específica)
--   - id 41 (Press francés)       -> Lying Triceps Press (mismo grupo muscular/patrón,
--     variante acostado en vez de parado)
--   - id 25 (Apertura de cadera)  -> Hip Circles (prone) (mobilidad de cadera genérica)
-- Quedan sin imagen (no hay match razonable en el dataset): id 29 Burpees, id 50 Natación.
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Squat/0.jpg' WHERE id = 1;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Deadlift/0.jpg' WHERE id = 2;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Bench_Press_-_Medium_Grip/0.jpg' WHERE id = 3;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Military_Press/0.jpg' WHERE id = 4;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_Barbell_Row/0.jpg' WHERE id = 5;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pullups/0.jpg' WHERE id = 6;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Parallel_Bar_Dip/0.jpg' WHERE id = 7;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Bicep_Curl/0.jpg' WHERE id = 8;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Pushdown/0.jpg' WHERE id = 9;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Side_Lateral_Raise/0.jpg' WHERE id = 10;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Two-Dumbbell_Raise/0.jpg' WHERE id = 11;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Dumbbell_Row/0.jpg' WHERE id = 12;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Stiff-Legged_Dumbbell_Deadlift/0.jpg' WHERE id = 13;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Lunges/0.jpg' WHERE id = 14;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Press/0.jpg' WHERE id = 15;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Extensions/0.jpg' WHERE id = 16;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lying_Leg_Curls/0.jpg' WHERE id = 17;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Calf_Raises/0.jpg' WHERE id = 18;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plank/0.jpg' WHERE id = 19;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crunches/0.jpg' WHERE id = 20;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Glute_Bridge/0.jpg' WHERE id = 21;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Straight-Arm_Dumbbell_Pullover/0.jpg' WHERE id = 22;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Face_Pull/0.jpg' WHERE id = 23;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cat_Stretch/0.jpg' WHERE id = 24;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hip_Circles_prone/0.jpg' WHERE id = 25;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Running_Treadmill/0.jpg' WHERE id = 26;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bicycling_Stationary/0.jpg' WHERE id = 27;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Rowing_Stationary/0.jpg' WHERE id = 28;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Mountain_Climbers/0.jpg' WHERE id = 30;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Star_Jump/0.jpg' WHERE id = 31;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Freehand_Jump_Squat/0.jpg' WHERE id = 32;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Split_Squat_with_Dumbbells/0.jpg' WHERE id = 33;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Hip_Thrust/0.jpg' WHERE id = 34;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leverage_Chest_Press/0.jpg' WHERE id = 35;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Wide-Grip_Lat_Pulldown/0.jpg' WHERE id = 36;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Cable_Rows/0.jpg' WHERE id = 37;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Incline_Dumbbell_Press/0.jpg' WHERE id = 38;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Flyes/0.jpg' WHERE id = 39;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hammer_Curls/0.jpg' WHERE id = 40;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lying_Triceps_Press/0.jpg' WHERE id = 41;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Goblet_Squat/0.jpg' WHERE id = 42;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sumo_Deadlift/0.jpg' WHERE id = 43;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hyperextensions_Back_Extensions/0.jpg' WHERE id = 44;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hanging_Leg_Raise/0.jpg' WHERE id = 45;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Russian_Twist/0.jpg' WHERE id = 46;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Step_Ups/0.jpg' WHERE id = 47;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Farmers_Walk/0.jpg' WHERE id = 48;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Calf_Raise/0.jpg' WHERE id = 49;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Rope_Jumping/0.jpg' WHERE id = 51;
UPDATE workout_exercises SET image_url = 'https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sled_Push/0.jpg' WHERE id = 52;
