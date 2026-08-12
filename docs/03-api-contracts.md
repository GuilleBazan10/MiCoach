# KineticOs — Contratos de API

> **Estado: FASE 2 (en curso).** Documento vivo: se completa a medida que se implementan
> los módulos. Implementado hasta ahora: **auth**, **user** (2026-08-11), **workout**
> (2026-08-12).

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
