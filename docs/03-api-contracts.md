# MiCoach — Contratos de API

> **Estado: FASE 2 COMPLETADA.** Los 9 módulos backend (`shared`, **auth**, **user**
> (2026-08-11), **workout**, **nutrition**, **progress**, **notification**, **admin**,
> **ai** (2026-08-12)) están implementados y documentados acá.

## Convenciones ya definidas

- **Base path:** `/api/v1/<modulo>` (ej: `/api/v1/auth`, `/api/v1/users`).
- **Formato de error unificado:**
  ```json
  {
    "timestamp": "2026-01-01T12:00:00Z",
    "status": 400,
    "code": "VALIDATION_ERROR",
    "message": "Descripción legible",
    "path": "/api/v1/users"
  }
  ```
- **Autenticación:** `Authorization: Bearer <JWT>`. Access token 15 min, refresh 7 días.
- **Correlación:** header `X-Correlation-Id` de entrada; se replica en logs y eventos.
- **Documentación OpenAPI/Swagger:** expuesta en `/swagger-ui.html` (perfil dev).
- **Rate limiting:** por IP y por usuario en el gateway.

## Módulo auth (implementado)

Base path: `/api/v1/auth`. Rutas públicas: register/login/refresh. El resto exige JWT.

### `POST /api/v1/auth/register`
Crea una cuenta (email + password ≥ 8 chars). BCrypt. Devuelve pair de tokens + usuario.
- `201 Created` → `AuthResponse`
- `400` → `VALIDATION_ERROR` (email inválido, password corta)
- `409` → `EMAIL_ALREADY_REGISTERED`

```json
// Request
{"email":"ana@ejemplo.com","password":"secreto123"}
// Response 201
{
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {"id": 1, "email": "ana@ejemplo.com", "roles": ["ROLE_USER"]}
}
```

### `POST /api/v1/auth/login`
- `200` → `AuthResponse`
- `401` → `INVALID_CREDENTIALS` (email inexistente o password incorrecta)
- `403` → `UNAUTHORIZED` (cuenta no activa)

### `POST /api/v1/auth/refresh`
Body: `{"refreshToken":"..."}`. Rota el par de tokens.
- `200` → `AuthResponse`
- `401` → `INVALID_TOKEN` (token no es refresh, expirado o inválido)

### `GET /api/v1/auth/me` (JWT)
Devuelve el usuario autenticado.
- `200` → `AuthUserResponse` (`{id, email, roles}`)
- `401` → `UNAUTHORIZED`

## Módulo user (implementado)

Base path: `/api/v1/users/me` (todos los endpoints requieren `Authorization: Bearer <JWT>`).
El perfil se crea automáticamente al registrarse (`UserRegisteredEvent` de auth → listener)

### `GET /api/v1/users/me/profile`
Crea el perfil si no existe. `200` → `ProfileResponse`.

### `PUT /api/v1/users/me/profile`
Actualiza el perfil de salud.
- `200` → `ProfileResponse`
- `400` → `VALIDATION_ERROR`

```json
// Request (todos los campos opcionales)
{
  "sex": "female", "birthDate": "1995-05-10", "heightCm": 165.00, "weightKg": 60.5,
  "activityLevel": "moderate", "experienceLevel": "intermediate",
  "equipment": ["dumbbells", "resistance_band"],
  "trainingDaysPerWeek": 4, "trainingMinutes": 60, "preferredTime": "morning",
  "timezone": "America/Argentina/Buenos_Aires", "tdeeCalories": 2100,
  "dietaryGoal": "gain_muscle", "notes": "..."
}
```

### Sub-recursos (mismo patrón; base `/api/v1/users/me`)
Cada uno con `GET` (lista), `POST` (crear, `201`) y `DELETE /{id}` (`204`):

| Recurso | Body POST (requeridos marcados *) | Notas |
|---|---|---|
| `/goals` | `goalType*`, `targetValue`, `targetUnit`, `targetDate`, `priority` | prioridad ≥ 1 |
| `/pathologies` | `pathology*`, `notes`, `diagnosedAt` | |
| `/injuries` | `bodyPart*`, `injuryType*`, `status`, `notes`, `occurredAt` | status: active/recovered/chronic |
| `/medications` | `medicationName*`, `dosage`, `schedule`, `notes` | |

Errores: `404 NOT_FOUND` si no existe perfil; `401 UNAUTHORIZED` sin JWT.

## Módulo workout (implementado)

Base path: `/api/v1/workouts` (todos los endpoints requieren `Authorization: Bearer <JWT>`,
igual que el resto de la API salvo auth).

### Catálogo (lectura)

| Endpoint | Descripción |
|---|---|
| `GET /workouts/muscles` | Catálogo de músculos, ordenado por grupo y nombre. |
| `GET /workouts/exercises` | Catálogo de ejercicios. Query params opcionales: `category`, `difficulty`, `muscleId`, `search` (contiene, case-insensitive). |
| `GET /workouts/exercises/{exerciseId}` | Detalle de un ejercicio con sus músculos (`role`: primary/secondary/stabilizer). `404 NOT_FOUND` si no existe. |

### Rutinas

| Endpoint | Descripción |
|---|---|
| `GET /workouts?templates=false\|true` | `false` (default) = rutinas propias del usuario. `true` = plantillas globales (`user_id` nulo). |
| `GET /workouts/{workoutId}` | Rutina con sus días y ejercicios prescritos. `404` si no existe o no es propia ni plantilla. |
| `POST /workouts` | Crea una rutina propia con días y ejercicios anidados. `201`. |
| `POST /workouts/generate` | **(Fase 4)** Genera una rutina con IA a partir de un pedido en lenguaje natural (`{"goal": "..."}`). Usa el catálogo real de ejercicios (el nombre que devuelve la IA se resuelve contra `workout_exercises`; lo que no matchea se descarta). Crea la rutina con `aiGenerated: true`. `201` — mismo `WorkoutResponse` que `POST /workouts`. `502 INTERNAL_ERROR` si el proveedor de IA falla o devuelve algo no interpretable (ver `docs/00-progress.md` § Fase 4). |
| `PUT /workouts/{workoutId}` | Reemplaza nombre/objetivo/nivel y **todos** los días/ejercicios (estrategia replace). Solo el dueño. `200` / `404` si no es propia. |
| `DELETE /workouts/{workoutId}` | Borra la rutina (cascada a días y ejercicios). `204` / `404` si no es propia. |

```json
// POST /api/v1/workouts (request)
{
  "name": "Push Pull Legs", "description": "...", "objective": "gain_muscle",
  "level": "intermediate", "durationWeeks": 8,
  "days": [
    {"dayIndex": 1, "name": "Push", "restDay": false, "exercises": [
      {"exerciseId": 1, "orderIndex": 1, "sets": 4, "repsMin": 6, "repsMax": 10,
       "restSeconds": 90, "intensityPercent": null, "tempo": null, "notes": null}
    ]},
    {"dayIndex": 2, "name": "Descanso", "restDay": true, "exercises": []}
  ]
}
```

### Sesiones (historial de entrenamiento)

| Endpoint | Descripción |
|---|---|
| `GET /workouts/sessions` | Sesiones del usuario, más recientes primero. |
| `GET /workouts/sessions/{sessionId}` | Detalle con ejercicios ejecutados. `404` si no es propia. |
| `POST /workouts/sessions` | Inicia una sesión (`workoutId`/`workoutDayId` opcionales). `201`, `status: in_progress`. |
| `PUT /workouts/sessions/{sessionId}/complete` | Marca `completed`, guarda `durationSeconds`/`notes`. |
| `PUT /workouts/sessions/{sessionId}/abort` | Marca `aborted`. |
| `POST /workouts/sessions/{sessionId}/exercises` | Registra la ejecución real de un ejercicio (`201`). |

Errores comunes: `404 NOT_FOUND` (rutina/ejercicio/sesión inexistente o de otro usuario),
`400 VALIDATION_ERROR`, `401 UNAUTHORIZED` sin JWT.

## Módulo nutrition (implementado)

Base path: `/api/v1/nutrition` (todos los endpoints requieren `Authorization: Bearer <JWT>`).

### Catálogo (lectura)

| Endpoint | Descripción |
|---|---|
| `GET /nutrition/ingredients` | Catálogo de ingredientes. Query params: `category`, `search`. |
| `GET /nutrition/ingredients/{ingredientId}` | Detalle de un ingrediente. |
| `GET /nutrition/ingredients/{ingredientId}/substitutions` | Sustitutos sugeridos para ese ingrediente (por alergia/intolerancia/preferencia). |
| `GET /nutrition/recipes` | Catálogo de recetas. Query params: `mealCategory`, `difficulty`, `search`. |
| `GET /nutrition/recipes/{recipeId}` | Detalle de una receta con sus ingredientes (nombre resuelto, no solo el id). |

### Planes de alimentación

| Endpoint | Descripción |
|---|---|
| `GET /nutrition/meal-plans` | Planes propios del usuario. |
| `GET /nutrition/meal-plans/{mealPlanId}` | Plan con sus días y comidas. `404` si no es propio. |
| `POST /nutrition/meal-plans` | Crea un plan con días/comidas anidados. `201`. |
| `POST /nutrition/meal-plans/generate` | **(Fase 4)** Genera un plan con IA a partir de un pedido en lenguaje natural (`{"goal": "..."}`) y del perfil real del usuario (objetivo dietario, peso, TDEE, patologías). Usa el catálogo real de recetas. Crea el plan con `aiGenerated: true`. `201` — mismo `MealPlanResponse` que `POST /nutrition/meal-plans`. `502 INTERNAL_ERROR` si el proveedor de IA falla o devuelve algo no interpretable. |
| `PUT /nutrition/meal-plans/{mealPlanId}` | Reemplaza datos y **todos** los días/comidas (estrategia replace, igual que rutinas). `200`. |
| `DELETE /nutrition/meal-plans/{mealPlanId}` | Borra el plan (cascada a días y comidas). `204`. |

```json
// POST /api/v1/nutrition/meal-plans (request)
{
  "name": "Plan de la semana", "startDate": "2026-08-17", "endDate": "2026-08-23",
  "targetCalories": 2200,
  "days": [
    {"planDate": "2026-08-17", "meals": [
      {"recipeId": 1, "mealType": "breakfast", "orderIndex": 1, "servings": 1}
    ]}
  ]
}
```

### Diario alimentario

| Endpoint | Descripción |
|---|---|
| `GET /nutrition/intake?date=YYYY-MM-DD` | Registros del día (sin `date`, devuelve todo el historial del usuario). |
| `POST /nutrition/intake` | Registra qué comió (`recipeId` y/o macros manuales). `201`. |
| `DELETE /nutrition/intake/{intakeId}` | Borra un registro. `204`. |

### Listas de compra

| Endpoint | Descripción |
|---|---|
| `GET /nutrition/shopping-lists` | Listas del usuario. |
| `GET /nutrition/shopping-lists/{shoppingListId}` | Lista con sus ítems. |
| `POST /nutrition/shopping-lists` | Crea una lista vacía (`name`, `weekStart` opcionales). `201`. |
| `DELETE /nutrition/shopping-lists/{shoppingListId}` | Borra la lista (cascada a ítems). `204`. |
| `POST /nutrition/shopping-lists/{shoppingListId}/items` | Agrega un ítem (`ingredientId` del catálogo o `itemName` libre). `201`. |
| `PUT /nutrition/shopping-lists/{shoppingListId}/items/{itemId}` | Marca/desmarca comprado (`{"checked": true}`). |
| `DELETE /nutrition/shopping-lists/{shoppingListId}/items/{itemId}` | Borra el ítem. `204`. |

Errores comunes: `404 NOT_FOUND` (plan/lista/ítem inexistente o de otro usuario),
`400 VALIDATION_ERROR`, `401 UNAUTHORIZED` sin JWT.

## Módulo progress (implementado)

Base path: `/api/v1/progress` (todos los endpoints requieren `Authorization: Bearer <JWT>`).
Mismo patrón simple que los sub-recursos de `user` (sin agregados): cada registro es
independiente.

| Endpoint | Descripción |
|---|---|
| `GET /progress/entries` | Métricas del usuario, más recientes primero. Query param opcional `metricType` (weight, bmi, body_fat, waist, chest, arm, hip, thigh, neck, calf, water_percent, muscle_mass, bone_mass, visceral_fat, resting_hr). |
| `POST /progress/entries` | Registra una métrica (`metricType`, `value`, `unit`, `measuredAt` opcional → default ahora). `201`. |
| `DELETE /progress/entries/{entryId}` | Borra un registro. `204`. |
| `GET /progress/photos` | Fotos de progreso, más recientes primero. |
| `POST /progress/photos` | Registra una foto (`photoUrl`, `angle` opcional: front/side/back). `201`. |
| `DELETE /progress/photos/{photoId}` | Borra una foto. `204`. |

Errores: `404 NOT_FOUND` (registro/foto de otro usuario), `400 VALIDATION_ERROR`,
`401 UNAUTHORIZED` sin JWT.

## Módulo notification (implementado)

Base path: `/api/v1/notifications` (todos los endpoints requieren `Authorization: Bearer <JWT>`).
Solo persistencia + API; el envío real (push/email) llega con la infraestructura de
Fase 4/6 (FCM, RabbitMQ).

| Endpoint | Descripción |
|---|---|
| `GET /notifications` | Notificaciones del usuario. Query param opcional `status` (pending, sent, delivered, failed, read). |
| `POST /notifications` | Crea una notificación (`type`, `title`, `body`, `data` JSON libre, `channel`: push/email/in_app, `scheduledAt` opcional). `201`, `status: pending`. |
| `PUT /notifications/{notificationId}/read` | Marca como leída (`status: read`, setea `readAt`). |
| `DELETE /notifications/{notificationId}` | Borra la notificación. `204`. |
| `GET /notifications/reminders` | Recordatorios recurrentes del usuario. |
| `POST /notifications/reminders` | Crea un recordatorio (`reminderType`: workout/meal/water/medication/measurement/weekly_report, `scheduleCron` y/o `scheduleConfig` JSON libre, `enabled`). `201`. |
| `PUT /notifications/reminders/{reminderId}` | Reemplaza el recordatorio (incl. `enabled` para pausarlo). |
| `DELETE /notifications/reminders/{reminderId}` | Borra el recordatorio. `204`. |
| `GET /notifications/preferences` | Preferencias de notificación del usuario (alta/baja por evento+canal). |
| `PUT /notifications/preferences` | Crea o actualiza una preferencia (`eventType`, `channel`, `enabled`) — upsert por esa clave, no duplica filas. |

Errores: `404 NOT_FOUND` (notificación/recordatorio de otro usuario), `400 VALIDATION_ERROR`,
`401 UNAUTHORIZED` sin JWT.

## Módulo admin (implementado)

Base path: `/api/v1/admin` (JWT requerido, **no** hay gate por `ROLE_ADMIN` todavía —
ver nota en `docs/00-progress.md` § módulo admin). Gobernanza interna: sin pantalla
propia en ningún frontend (ni mobile ni web).

| Endpoint | Descripción |
|---|---|
| `GET /admin/roles` | Roles con sus `permissionCodes` resueltos. |
| `POST /admin/roles` | Crea un rol custom (`code`, `name`, `description`). `201`. |
| `DELETE /admin/roles/{roleId}` | Borra el rol. `409 CONFLICT` si es un rol del sistema (`is_system=true`, ej. ROLE_USER). |
| `GET /admin/permissions` | Catálogo de permisos. |
| `POST /admin/permissions` | Crea un permiso (`code`, `name`, `description`). `201`. |
| `POST /admin/roles/{roleId}/permissions/{permissionId}` | Asigna el permiso al rol. `204`. |
| `DELETE /admin/roles/{roleId}/permissions/{permissionId}` | Desasigna. `204`. |
| `GET /admin/users/{userId}/roles` | Roles asignados a un usuario. |
| `POST /admin/users/{userId}/roles/{roleId}` | Asigna el rol al usuario. `204`. |
| `DELETE /admin/users/{userId}/roles/{roleId}` | Desasigna. `204`. |
| `GET /admin/audit-logs` | Auditoría, filtros opcionales `userId`/`entityType`. Solo lectura: nada escribe todavía (ningún módulo audita operaciones críticas aún). |

Errores: `404 NOT_FOUND` (rol/permiso inexistente), `409 CONFLICT` (borrar rol del
sistema), `401 UNAUTHORIZED` sin JWT.

## Módulo ai (implementado — persistencia + generación real vía Ollama)

Base path: `/api/v1/ai` (JWT requerido). Desde la Fase 4 hay integración real con
LangChain4j (Strategy Pattern, `AiProviderStrategy` — `ollama`, `groq`, `openrouter` y
`gemini`; agregar otro proveedor cloud es implementar la interfaz sin tocar el resto).
No expone un endpoint genérico de "generar" — cada módulo consumidor (`workout` y
`nutrition`; sustituciones y ajuste de calorías pendientes) expone su propio endpoint
específico (ver `POST /workouts/generate` y `POST /nutrition/meal-plans/generate`
arriba) que internamente llama al caso de uso `AiUseCase.generate(userId, promptSlug,
variables)` de este módulo, que resuelve el prompt activo, llama al **proveedor
activo** (tabla `ai_provider_configs`, editable desde el panel admin sin redeploy — ver
más abajo) y audita en `ai_generation_logs`.

| Endpoint | Descripción |
|---|---|
| `GET /ai/prompts` | Prompts. Query params opcionales `slug`, `activeOnly`. |
| `GET /ai/prompts/{promptId}` | Detalle de un prompt. |
| `POST /ai/prompts` | Crea una nueva versión de un prompt (`slug`, `content`, `provider`/`model` opcionales — default `ollama`/`llama3.2`, `params` JSON libre). La versión **se autoincrementa** por `slug`, no se pasa en el body. `201`, queda activo. |
| `PUT /ai/prompts/{promptId}/active` | Activa/desactiva esa versión (`{"active": true|false}`). |
| `GET /ai/conversations` | Conversaciones del usuario (con sus mensajes). Query param opcional `topic`. |
| `GET /ai/conversations/{conversationId}` | Detalle con mensajes. |
| `POST /ai/conversations` | Crea una conversación (`topic` opcional: nutrition/workout/general). `201`. |
| `PUT /ai/conversations/{conversationId}/archive` | Marca la conversación como archivada. |
| `POST /ai/conversations/{conversationId}/messages` | Agrega un mensaje (`role`: user/assistant/system/tool, `content`, `provider`/`model`/`tokenUsage` opcionales). `201`. |
| `GET /ai/generation-logs` | Auditoría de generaciones (contexto de entrada, salida cruda, `durationMs`, `status`). Filtros opcionales `userId`/`promptSlug`. Se escribe una fila por cada intento de `POST /workouts/generate`, éxito o error. |

**Panel admin de proveedores** — base path `/api/v1/admin/ai/providers`, requiere
`ROLE_ADMIN` (`403 FORBIDDEN` si no). Los 4 proveedores son filas fijas sembradas por
migración (V11), no se crean por API, solo se editan/activan:

| Endpoint | Descripción |
|---|---|
| `GET /admin/ai/providers` | Lista los 4 proveedores con su config. `apiKey` nunca se devuelve, solo `hasApiKey: boolean`. |
| `PUT /admin/ai/providers/{provider}` | Edita `displayName`/`baseUrl`/`model`/`enabled`. `apiKey` opcional: vacío/ausente = mantiene la key ya guardada (se cifra con AES/GCM antes de persistir). |
| `POST /admin/ai/providers/{provider}/activate` | Lo activa (desactiva cualquier otro). `409 CONFLICT` si el proveedor está `enabled: false`. |
| `POST /admin/ai/providers/{provider}/test` | Llama al proveedor con un prompt corto sin tocar la config activa. `200` siempre — el resultado real va en el body (`{"ok": boolean, "message": string}`). |

Errores: `404 NOT_FOUND` (prompt/conversación inexistente o de otro usuario),
`400 VALIDATION_ERROR`, `401 UNAUTHORIZED` sin JWT.

## Eventos de dominio (borrador inicial)

| Evento | Routing key | Emisor | Consumidores |
|---|---|---|---|
| `UserProfileUpdated` | `user.profile.updated` | user | ai, nutrition, workout |
| `UserCreated` | `user.created` | auth | user, notification |
| `WorkoutGenerated` | `workout.generated` | workout | progress, notification |
| `MealPlanGenerated` | `nutrition.mealplan.generated` | nutrition | progress, notification |
| `ProgressUpdated` | `progress.updated` | progress | ai, analytics |
| `NotificationRequested` | `notification.requested` | cualquiera | notification |

> Pendiente definir payloads exactos en Fase 2.
