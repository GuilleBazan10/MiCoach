-- =====================================================================
-- KineticOs — V8: Prompt activo para la generación de rutinas con IA
-- (Fase 4). Slug consumido por WorkoutAiGenerator vía AiUseCase.generate.
--
-- Nota: el slug 'workout_generator' ya tenía 2 versiones de prueba
-- (creadas a mano por curl al verificar el CRUD de /ai/prompts en la
-- Fase 2, con contenido placeholder tipo "{perfil}" que no sirve para
-- generar nada real). Se desactivan acá y se agrega la versión 3, la
-- primera con contenido real ({{goal}}/{{catalog}}) y el formato JSON
-- que espera WorkoutAiGenerator.
-- =====================================================================
UPDATE ai_prompts SET is_active = FALSE WHERE slug = 'workout_generator';

INSERT INTO ai_prompts (slug, version, provider, model, content, is_active)
VALUES (
    'workout_generator',
    3,
    'ollama',
    'llama3.2',
    $prompt$Sos un entrenador experto que diseña rutinas de entrenamiento. Te paso el pedido de un usuario y el catálogo de ejercicios disponibles. Devolvé SOLO un JSON válido (sin texto antes ni después, sin markdown, sin comentarios) con esta forma exacta:

{
  "name": "string corto",
  "description": "string breve",
  "objective": "lose_fat|gain_muscle|maintain|endurance|strength|general_health",
  "level": "beginner|intermediate|advanced",
  "durationWeeks": number,
  "days": [
    {
      "name": "string (ej: Push, Pull, Piernas)",
      "restDay": boolean,
      "exercises": [
        {"exerciseName": "nombre EXACTO de la lista de abajo", "sets": number, "repsMin": number, "repsMax": number}
      ]
    }
  ]
}

Reglas:
- Usá SOLO nombres de ejercicio que estén EXACTAMENTE en esta lista (copiá el texto tal cual, sin traducir ni modificar):
{{catalog}}
- Los días de descanso llevan "restDay": true y "exercises": [].
- Generá entre 3 y 6 días según lo que pida el usuario.
- No inventes ejercicios que no estén en la lista.

Pedido del usuario: {{goal}}$prompt$,
    TRUE
);
