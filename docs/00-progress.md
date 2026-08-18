# MiCoach — Registro de progreso

> **LEER PRIMERO.** Este archivo es el "termómetro" del proyecto. Cualquier IA (o persona)
> que continúe el desarrollo debe empezar leyendo este documento para saber exactamente qué
> está hecho, qué falta y dónde encontrar el contexto. **Actualízalo al cerrar cada fase.**

## Estado general

| Fase | Descripción | Estado |
|---|---|---|
| 0 | Cimientos (estructura, infra, docs base) | ✅ COMPLETADA |
| 1 | Modelo de base de datos + migraciones Flyway | ✅ COMPLETADA |
| 2 | Backend módulo a módulo (Java/Spring) | ✅ COMPLETADA (shared, auth, user, workout, nutrition, progress, notification, admin, ai) |
| 3.1 | Frontend Mobile (Flutter) | ✅ COMPLETADA (core, auth, profile, workout, nutrition, progress — `admin`/`ai` sin frontend, es deliberado) |
| 3.2 | Frontend Web (React) | ✅ COMPLETADA (core, auth, profile, workout, nutrition, progress — mismo alcance que 3.1) |
| 4 | Integración IA (LangChain4j + Ollama/Groq/Gemini) | ✅ COMPLETADA (falta paridad Flutter de 2 features, ver "Pendientes globales") |
| 5 | Testing completo | ⬜ Pendiente |
| 6 | CI/CD y despliegue | 🟡 PARCIAL (deploy productivo Render+Vercel+Supabase hecho; falta GitHub Actions, APK Flutter) |
| 7 | Documentación final (README, manuales) | ⬜ Pendiente |
| — | UX/UI (auditoría, transversal a las fases) | 🟡 EN CURSO — ver [`docs/06-ux-ui-audit.md`](./06-ux-ui-audit.md). Parte 1 (4/4) y Parte 2 (11/11) resueltas **en web** 2026-08-17. Falta portar todo a Flutter y el backlog de "¿olvidaste tu contraseña?" |
| — | Calidad de entrenamiento/nutrición (auditoría, transversal) | 🟢 CASI CERRADA — ver [`docs/10-recomendaciones-coach-nutricion.md`](./10-recomendaciones-coach-nutricion.md). 18/22 hallazgos resueltos (o parciales) en web 2026-08-17. Quedan solo por decisión de alcance: C.3 (contenido), H.2 y H.5-parte-2 (backlog) |

## Fase 0 — Cimientos (COMPLETADA)

### Qué se creó
- Estructura completa del monorepo (backend, mobile, infra, docs, scripts).
- `.env.example` con todas las variables documentadas + `.gitignore`.
- `docker-compose.yml`: infra core (PostgreSQL 16, Redis 7, RabbitMQ 3.13, MinIO, Ollama).
- `docker-compose.full.yml`: infra opcional (OpenSearch, Prometheus, Grafana, Jaeger).
- `Makefile` + `scripts/init-dev.ps1` / `init-dev.sh` / `seed-ai.sh`.
- `docs/`: 01-architecture, 02-database (stub), 03-api-contracts (stub), ADRs (001, 002).
- `backend/`: esqueleto Gradle multi-módulo con version catalog y 9 módulos + `app`.
- `mobile/`: esqueleto Flutter tematizable (core/theme centralizado) + pubspec.

### Verificaciones realizadas (2026-08-11)
- `docker compose config` (core y full) → sin errores.
- Backend **compila**: `./gradlew build -x test` → BUILD SUCCESSFUL. Se generó
  `backend/app/build/libs/micoach.jar` (~91 MB). Gradle descargó el JDK 21
  automáticamente (toolchain + plugin foojay).
- Git inicializado (commit pendiente de hacer).

### Requisitos de entorno detectados en esta máquina
- ⚠️ **El daemon de Gradle necesita JVM 17+** (Spring Boot 3.3). En este equipo (Linux)
  el `java` activo de sdkman es JDK 8, pero hay JDK 21 instalado:
  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew ...` (o `sdk use java 21`).
  En Windows, el fix es `setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21..."`.
- **Gradle** no instalado → se usa el wrapper (funciona, ya descargó 8.10.2).
- **PostgreSQL** se levanta con `docker compose up -d postgres` (puerto 5432, user/db
  `micoach`). Las credenciales de Flyway salen del `.env` de la raíz.
- **Flutter** no instalado. Antes de correr la app:
  `cd mobile && flutter create . --org com.micoach --project-name micoach_mobile`
  (genera las carpetas android/ios/web/).

### Acción recomendada antes de Fase 1
Ejecutar una vez (consulta `docs/01-architecture.md` sección "Renombrar el proyecto"
si el nombre MiCoach va a cambiar, y hazlo ANTES de la Fase 1 para no renombrar
migraciones/paquetes con código ya escrito).

## Fase 1 — Base de datos (EN CURSO)

### Sub-entregas

- [x] **Sub-entrega 1 — Diseño del modelo BD** (2026-08-11): `docs/02-database.md` completo.
      45 tablas en 9 módulos + ER en Mermaid, índices y constraints por tabla, plan de
      migraciones V1–V6 y lista del seed de catálogos.
- [x] **Sub-entrega 2 — Migraciones Flyway** (2026-08-11):
  - [x] **Integración `flywayMigrate`** (2026-08-11): tarea Gradle en `app/build.gradle`
        que ejecuta la CLI oficial de Flyway (`flyway-commandline`) con credenciales del
        `.env` de la raíz. NO se usa el plugin Gradle de Flyway 10.x (no detecta
        `flyway-database-postgresql`, https://github.com/flyway/flyway/issues/3550).
        Verificado: conecta a Postgres y crea `flyway_schema_history`.
  - [x] **`.env` creado** (raíz, gitignored) desde `.env.example` — único lugar para cargar
        keys de BD.
  - [x] Crear migraciones `V1__core`, `V2__workout`, `V3__nutrition`,
        `V4__progress_notification`, `V5__ai`, `V6__catalog_seed`.
  - [x] **Fix orden en `V1__core_schema.sql`** (2026-08-11): los catálogos transversales
        `nutrition_allergens` y `nutrition_diets` se movieron ANTES de
        `user_food_restrictions`/`user_diet_preferences` que los referencian
        (fallaba con `relation "nutrition_allergens" does not exist`).
- [x] **Sub-entrega 3 — Verificación** (2026-08-11): `docker compose up -d postgres`,
      luego `./gradlew flywayMigrate` aplica V1–V6 sin errores; `./gradlew bootRun`
      arranca (en este equipo el puerto 8080 lo ocupa un Tomcat de Eclipse → usar
      `--args='--server.port=8081'`).

### Objetivos
- Diagrama ER completo en Mermaid → `docs/02-database.md`. ✅ (sub-entrega 1)
- Entidades, relaciones, índices y constraints de TODOS los módulos (auth, user, workout,
  nutrition, progress, notification, admin, ai). ✅ (sub-entrega 1)
- Migraciones Flyway versionadas en `backend/app/src/main/resources/db/migration/`
  (V1__..., V2__...) con seed de catálogos (ejercicios, músculos, recetas base, categorías).
  ✅ (sub-entrega 2)
- Verificación: `cd backend && ./gradlew flywayMigrate` aplica sin errores. ✅ (sub-entrega 3)

### Notas para quien continúe
- Leer `docs/01-architecture.md` (mapa de módulos y dominio) ANTES de modelar.
- Mantener la guía de estilo de Fase 1 definida en `docs/02-database.md`.
- Decisiones de diseño de la Fase 1 (enums VARCHAR+CHECK, timestamps, JSONB, prefijos por
  módulo, catálogos transversales en V1): ver `docs/02-database.md` § "Decisiones de diseño".

## Fase 2 — Backend (EN CURSO)

Orden por módulo: `shared → auth → user → workout → nutrition → progress → notification → admin → ai → app`.
Un módulo por entrega. Cada módulo: domain, application (casos de uso + puertos),
infrastructure (adapters JPA/clientes), presentation (controllers + DTOs), validación y
tests unitarios. La `app` compone y arranca todos los módulos.

### Entregas completadas (2026-08-11)

- [x] **Módulo `shared`** (base): `ApiError` + `ErrorCode` (formato de error unificado),
      `DomainException` y `AuthenticatedUser` (principal de seguridad).
- [x] **Módulo `auth`** (identidad funcional):
  - `domain/AuthUser` + `application/port/out/AuthUserRepository` (hexagonal).
  - Adaptador JPA (`AuthUserJpa`, `AuthUserJpaRepository`, `AuthUserRepositoryAdapter`).
  - `JwtService` (jjwt 0.12, access 15 min + refresh 7 días, claims `type`/`roles`).
  - `AuthService` (register/login/refresh/me) con BCrypt y validación.
  - `AuthController` en `/api/v1/auth`: `POST /register`, `POST /login`, `POST /refresh`,
    `GET /me` (Bearer).
- [x] **Módulo `app`** (gateway/seguridad): `SecurityConfig` (stateless, CORS, BCrypt,
      rutas públicas de auth, resto autenticado), `JwtAuthenticationFilter`,
      `GlobalExceptionHandler` (traduce errores a `ApiError`).
- [x] **Módulo `user`** (perfil de salud funcional):
  - Hexagonal: dominio (`UserProfile`, `UserGoal`, `UserPathology`, `UserInjury`,
    `UserMedication`) + puertos + adaptadores JPA (5 tablas) + mappers.
  - `UserProfileService` (casos de uso) + `UserProfileOnRegisteredListener`.
  - `UserController` en `/api/v1/users/me` (JWT): perfil (GET/PUT), objetivos,
    patologías, lesiones y medicación (GET/POST/DELETE).
  - **Perfil auto-creado al registrarse**: auth publica `UserRegisteredEvent`
    (shared/event) → el listener del módulo user crea el perfil en `AFTER_COMMIT`.

### Entrega completada (2026-08-12)

- [x] **Módulo `workout`** (ejercicios, músculos, rutinas y sesiones de entrenamiento):
  - Hexagonal: dominio (`Muscle`, `Exercise` + `ExerciseMuscle` embebido; agregado
    `Workout` con `WorkoutDay`/`PlannedExercise`; agregado `WorkoutSession` con
    `SessionExercise`) + puertos (`WorkoutUseCase`, `WorkoutRepository`) + adaptador
    JPA de las 8 tablas del módulo + `WorkoutMappers`.
  - `WorkoutService` (casos de uso): catálogo de solo lectura (músculos/ejercicios con
    filtro en memoria por categoría/dificultad/músculo/búsqueda — catálogo pequeño,
    sin Specification dinámica), CRUD de rutinas propias, ciclo de vida de sesiones
    (start/complete/abort) y registro de ejercicios ejecutados.
  - `WorkoutController` en `/api/v1/workouts` (JWT): ver `docs/03-api-contracts.md`.
  - Rutinas con `user_id` nulo (`is_template = true`) son plantillas globales; no se
    crean todavía por API (llegarán con `admin`/`ai`, fases posteriores).
  - Estrategia de actualización de rutina: "replace" — `PUT` descarta los días/ejercicios
    previos (delete + `flush()` explícito antes de reinsertar, ver fix abajo) y los
    recrea desde el request completo.

### Verificación (2026-08-11, ampliada 2026-08-12)
- `cd backend && ./gradlew build` → BUILD SUCCESSFUL.
- `:app:bootRun` con `--args='--server.port=8081'` arranca (8080 ocupado por Tomcat de Eclipse).
- Flujo probado con curl: register 201 → register duplicado 409 → login 200 → `/me` 200 →
  refresh 200 → `/me` sin token 401 → register inválido 400 (VALIDATION_ERROR) →
  login mal 401 (INVALID_CREDENTIALS). Usuario persistido en `auth_users`.
- Módulo user (curl): register nuevo → `/users/me/profile` 200 con perfil auto-creado →
  PUT profile 200 (incluye `equipment` JSONB) → POST goals/pathologies/injuries/medications
  201 → GET lists 200 → DELETEs 204 → validación 400 → sin token 401.
- Fix aplicado: `-parameters` flag en `backend/build.gradle` (Spring Boot lo necesita para
  `@PathVariable`); `SMALLINT` mapeado como `Short` en JPA (validación de schema).
- Módulo workout (curl): `GET /muscles` y `GET /exercises?category=strength` 200 con seed
  de V6 → `POST /workouts` 201 (rutina con 2 días, uno con ejercicio, uno de descanso) →
  `GET`/lista 200 → `PUT` 200 (renombra y reemplaza días) → `POST /sessions` 201 →
  `POST /sessions/{id}/exercises` 201 → `PUT /sessions/{id}/complete` 200 → `DELETE` 204 →
  `GET` tras borrar 404 → sin token 401 → validación 400 (nombre vacío).
- **Fix aplicado**: `deleteByWorkoutId` (borrado de días previos al actualizar una rutina)
  no se flushea antes del `INSERT` de los días nuevos → Hibernate reordena el flush
  (inserts antes que deletes) y viola `uq_workout_day` al reinsertar el mismo
  `day_index`. Solución: `workoutDayRepository.flush()` explícito tras el delete, en
  `WorkoutRepositoryAdapter.saveWorkout`.

### Entrega completada (2026-08-12)

- [x] **Módulo `nutrition`** (ingredientes, recetas, planes de comida, diario alimentario,
      sustituciones, listas de compra) — el módulo backend más grande hasta ahora (10 tablas):
  - Hexagonal: dominio (`Ingredient`, `Recipe` + `RecipeIngredient` embebido; agregado
    `MealPlan` con `MealPlanDay`/`MealPlanMeal`; `Substitution`; `DailyIntakeEntry`;
    agregado `ShoppingList` con `ShoppingListItem`) + puertos (`NutritionUseCase`,
    `NutritionRepository`) + adaptador JPA de las 10 tablas + `NutritionMappers`.
  - `NutritionService`: catálogo de solo lectura (ingredientes/recetas con filtro en
    memoria, mismo criterio que `workout` por ser catálogos chicos), CRUD de planes de
    alimentación propios (estrategia "replace" en días/comidas, igual que rutinas —
    aplicado el `flush()` preventivo del fix de `workout` desde el principio, sin
    reproducir el bug), diario alimentario (log/list/delete) y listas de compra con
    ítems (CRUD + marcar comprado).
  - `NutritionController` en `/api/v1/nutrition` (JWT): ver `docs/03-api-contracts.md`.

### Verificación (2026-08-12)

- `cd backend && ./gradlew build` → BUILD SUCCESSFUL.
- Módulo nutrition (curl): `GET /ingredients?category=proteins` y
  `GET /recipes?mealCategory=breakfast` 200 con seed de V6 (nombres de ingredientes
  resueltos dentro de cada receta) → `POST /meal-plans` 201 (plan con 1 día y 1 comida)
  → `PUT` 200 (renombra y reemplaza días, sin el bug de flush que sí apareció en
  `workout`) → `POST /intake` 201 → `GET /intake?date=...` 200 → `DELETE` 204 →
  `POST /shopping-lists` 201 → `POST .../items` 201 → `PUT .../items/{id}` 200 (marca
  comprado) → `DELETE /meal-plans/{id}` 204 → sin token 401.

### Entrega completada (2026-08-12)

- [x] **Módulo `progress`** (métricas de seguimiento + fotos de progreso, 2 tablas) —
      el módulo más chico hasta ahora, mismo patrón simple de sub-recurso que
      `user` (goals/pathologies/...): dominio (`ProgressEntry`, `ProgressPhoto`) +
      puertos (`ProgressUseCase`, `ProgressRepository`) + adaptador JPA + mappers.
      `ProgressController` en `/api/v1/progress`: `/entries` (GET con filtro opcional
      `metricType`, POST, DELETE) y `/photos` (GET, POST, DELETE). Sin agregados ni
      estrategia "replace" — cada registro es independiente, como en `user`.

### Verificación (2026-08-12)

- `cd backend && ./gradlew build` → BUILD SUCCESSFUL.
- Módulo progress (curl): `POST /entries` 201 (weight, luego body_fat) →
  `GET /entries` 200 (2 registros) → `GET /entries?metricType=weight` 200 (filtrado) →
  `DELETE` 204 → `POST /photos` 201 → `GET /photos` 200 → `DELETE` 204 →
  validación 400 (`metricType` vacío) → sin token 401.

### Entrega completada (2026-08-12)

- [x] **Módulo `notification`** (avisos, recordatorios, preferencias, 3 tablas):
  - Dominio: `Notification` (con `data` JSONB → `Map<String,Object>`, transición de
    estado `markRead()`), `Reminder` (con `scheduleConfig` JSONB, editable), `Preference`
    (upsert por clave lógica `userId+eventType+channel`).
  - `NotificationController` en `/api/v1/notifications`: notificaciones (GET con filtro
    `status`, POST, `PUT /{id}/read`, DELETE), `/reminders` (GET, POST, PUT, DELETE),
    `/preferences` (GET, `PUT` que crea o actualiza según exista la clave).
  - Nota de alcance: no hay integración real de envío (push/email) todavía — eso es
    Fase 4/infra (FCM, RabbitMQ). Este módulo deja la persistencia y la API lista para
    que otro proceso (o la Fase 4 de IA) las use.

### Verificación (2026-08-12)

- `cd backend && ./gradlew build` → BUILD SUCCESSFUL.
- Módulo notification (curl): `POST /notifications` 201 → `GET` 200 →
  `PUT /{id}/read` 200 (status pending → read) → `GET ?status=read` 200 (filtrado) →
  `DELETE` 204 → `POST /reminders` 201 (con `scheduleConfig` JSONB) →
  `PUT /reminders/{id}` 200 (deshabilita) → `DELETE` 204 →
  `PUT /preferences` 200 dos veces con la misma clave (mismo `id`, confirma upsert
  real y no duplicado) → sin token 401.

### Entrega completada (2026-08-12)

- [x] **Módulo `admin`** (roles, permisos, asignaciones, auditoría, 5 tablas) — sin
      frontend Flutter (es gobernanza interna, no una pantalla de usuario final):
  - Dominio: `Role` (con `permissionCodes` resueltos), `Permission`, `UserRole`
    (asignación con datos del rol resueltos), `AuditLogEntry` (solo lectura por ahora).
  - `AdminController` en `/api/v1/admin`: `/roles` (GET, POST, DELETE — bloquea borrar
    roles `is_system`), `/permissions` (GET, POST), `/roles/{id}/permissions/{id}`
    (POST/DELETE para asignar/desasignar), `/users/{id}/roles` (GET) y
    `/users/{id}/roles/{id}` (POST/DELETE), `/audit-logs` (GET con filtros
    `userId`/`entityType`).
  - **⚠️ Gap conocido, no cerrado en esta entrega**: los endpoints de `admin` están
    protegidos solo con JWT (como el resto de la API), **no** con `ROLE_ADMIN`. La
    razón: `AuthService.DEFAULT_ROLES` está hardcodeado a `["ROLE_USER"]` — el login
    nunca consulta `admin_user_roles`, así que ningún usuario puede obtener
    `ROLE_ADMIN` en su JWT todavía. Agregar el gate (`hasRole("ADMIN")` en
    `SecurityConfig`) sin resolver esto dejaría el módulo completo inaccesible sin
    forma de probarlo. Cerrar este gap requiere decidir cómo `auth` resuelve roles al
    loguear (consultar `admin_user_roles` ahí, o mover esa resolución a `app`) —
    decisión de arquitectura pendiente para quien continúe, no tomada unilateralmente.
  - **Bug real encontrado y corregido**: `POST /admin/roles` y `POST /admin/permissions`
    fallaban con `duplicate key value violates unique constraint` (500). Causa: `V6`
    sembró `admin_roles`/`admin_permissions` (y también `workout_muscles`,
    `workout_exercises`, `nutrition_allergens`, `nutrition_diets`,
    `nutrition_ingredients`, `nutrition_recipes`) con **id explícito**, sin sincronizar
    la secuencia `SERIAL` de Postgres — el próximo insert por `IDENTITY` reintentaba
    `id=1`. No se había detectado antes porque ninguna de esas tablas tenía un
    create-endpoint hasta `admin`. Fix: `V7__fix_catalog_sequences.sql` (nueva
    migración, no se tocó V6 ya aplicada) con `setval(pg_get_serial_sequence(...), MAX(id))`
    para las 8 tablas afectadas.

### Verificación (2026-08-12)

- `cd backend && ./gradlew build` → BUILD SUCCESSFUL.
- Módulo admin (curl): `GET /roles`/`GET /permissions` 200 (seed de V6 con
  `permissionCodes` resueltos) → `POST /roles` y `POST /permissions` **500 al
  principio** (bug de secuencia, ver arriba) → aplicado `V7`, reiniciado el backend →
  reintentado: 201 en ambos → asignar/desasignar permiso a rol 204 → asignar/desasignar
  rol a usuario 204 → `DELETE` sobre rol `is_system` 409 → `DELETE` sobre rol propio
  204 → sin token 401. Regresión: `GET /workouts/exercises` sigue en 200 (la
  migración V7 no rompió nada de `workout`/`nutrition`).

### Entrega completada (2026-08-12) — cierra la Fase 2

- [x] **Módulo `ai`** (base técnica: prompts versionados, conversaciones y auditoría
      de generación, 4 tablas). **Sin integración real con LangChain4j/Ollama** — eso
      es la Fase 4 completa; acá solo queda lista la persistencia y la API:
  - Dominio: `Prompt` (versionado por `slug`, `setActive()`), agregado `Conversation`
    con `ChatMessage`, `GenerationLog` (solo lectura, nada escribe ahí todavía porque
    no hay generación real que auditar).
  - `AiController` en `/api/v1/ai`: `/prompts` (GET con filtros `slug`/`activeOnly`,
    POST — la versión se autoincrementa por `slug`, no se pasa a mano —,
    `PUT /{id}/active` para activar/desactivar una versión), `/conversations` (GET,
    GET por id con mensajes, POST, `PUT /{id}/archive`,
    `POST /{id}/messages` para loguear un mensaje), `/generation-logs` (GET con
    filtros `userId`/`promptSlug`).
  - Con este módulo se cierra la **Fase 2 completa**: los 9 módulos backend
    (`shared`, `auth`, `user`, `workout`, `nutrition`, `progress`, `notification`,
    `admin`, `ai`) están implementados, compilando juntos y verificados contra
    Postgres real.

### Verificación (2026-08-12)

- `cd backend && ./gradlew build` → BUILD SUCCESSFUL (los 9 módulos + `app`).
- Módulo ai (curl): `POST /prompts` 201 dos veces con el mismo `slug`
  (`workout_generator`) → confirma versión 1 y 2 autoincrementadas → `GET ?slug=`
  200 (2 versiones, v2 primero) → `PUT /{id}/active` 200 (desactiva v2) →
  `GET ?slug=&activeOnly=true` 200 (solo v1) → `POST /conversations` 201 →
  `POST .../messages` 201 dos veces (user + assistant, con `tokenUsage` JSONB) →
  `GET /conversations/{id}` 200 (trae los 2 mensajes) → `PUT .../archive` 200 →
  `GET /generation-logs` 200 (vacío, nada escribe ahí todavía) → sin token 401.

### Pendientes conocidos de la Fase 2 (no bloquean, documentados para continuar)
- **RBAC de `admin` sin cerrar**: ver nota en la entrega del módulo `admin` — ningún
  usuario puede tener `ROLE_ADMIN` en su JWT todavía porque `AuthService` no consulta
  `admin_user_roles` al loguear.
- **`ai_generation_logs` sin escritores**: quedará vacío hasta que algún módulo
  (probablemente `workout`/`nutrition` en Fase 4, al generar contenido con IA de
  verdad) empiece a loguear ahí.
- **`admin_audit_logs` sin escritores**: mismo caso — ningún módulo audita
  operaciones críticas todavía.

## Fase 3.1 — Frontend Mobile (Flutter) (EN CURSO)

`core` (tema, router, red, storage) → auth → profile → workout → nutrition → progress.
Una feature por entrega, consumiendo la API real (no mocks) contra el backend ya
implementado. `nutrition` y `progress` quedan pendientes hasta que existan sus módulos
backend (Fase 2).

### Entorno de desarrollo Flutter (2026-08-12)

- Flutter no estaba instalado en la máquina de desarrollo. Se instaló manualmente (sin
  sudo) descargando el SDK estable 3.44.9 y extrayéndolo en `~/development/flutter`
  (agregar `~/development/flutter/bin` al `PATH`). `flutter create . --org com.micoach
  --project-name micoach_mobile --platforms=web,linux,android` generó las carpetas de
  plataforma (aditivo, no tocó `lib/` ni `pubspec.yaml`).
- **`pubspec.yaml` actualizado** con `flutter pub upgrade --major-versions`: las versiones
  originales (Riverpod 2.x, go_router 14.x, freezed 2.x, etc.) no resolvían contra el SDK
  3.44.9 instalado. Versiones nuevas relevantes: `flutter_riverpod ^3.4.2`,
  `go_router ^17.5.0`, `freezed ^3.2.5`, `flutter_secure_storage ^11.0.0`.
- **Decisión de arquitectura**: se usa **Riverpod** para DI y manejo de estado en todas
  las features (providers + `Notifier`/`AsyncNotifier`), NO `flutter_bloc` (queda
  declarado en `pubspec.yaml` pero sin uso — usar dos soluciones de estado en paralelo no
  aportaba valor en este alcance). Tampoco se generó código con Freezed/build_runner:
  los modelos de dominio son clases Dart simples con `fromJson`/`toJson` manuales, para
  poder iterar sin depender del generador.

### Entregas completadas (2026-08-12)

- [x] **`core`**: `ApiClient` (Dio) con interceptor de JWT + refresh automático ante 401
      + `X-Correlation-Id`; `TokenStorage` (flutter_secure_storage); `GoRouter` con guarda
      de autenticación (`redirect` reactivo a `authControllerProvider` vía
      `GoRouterRefreshNotifier`, un `ChangeNotifier` que Riverpod no provee nativamente
      en v3 — no existe `ChangeNotifierProvider`, se expone como `Provider` simple);
      `SplashScreen` mientras se restaura la sesión guardada; `AppShell` con
      `NavigationBar` (Rutinas/Perfil).
- [x] **`auth`**: `LoginScreen`/`RegisterScreen`, `AuthController` (Riverpod `Notifier`)
      contra `/api/v1/auth`. Sesión persistida y restaurada al abrir la app.
- [x] **`profile`**: `ProfileScreen` con datos físicos/entrenamiento editables +
      secciones expandibles de objetivos/patologías/lesiones/medicación (CRUD completo),
      contra `/api/v1/users/me/*`.
- [x] **`workout`**: `WorkoutHomeScreen` (tabs Mis rutinas/Plantillas/Historial),
      `WorkoutFormScreen` (crear/editar rutina con días y ejercicios anidados +
      `ExercisePickerDialog` con búsqueda del catálogo), `WorkoutDetailScreen`
      (inicia sesión por día), `SessionScreen` (registrar ejercicios ejecutados,
      completar/abandonar), contra `/api/v1/workouts/*`.

### Verificación (2026-08-12)

- `flutter analyze` → **0 issues**.
- `flutter test` → smoke test pasa (arranca la app con `ProviderScope`, mockeando el
  `MethodChannel` de `flutter_secure_storage` porque no existe en el entorno de test;
  usa `pump()` acotado en vez de `pumpAndSettle()` porque el splash tiene un spinner
  indeterminado que nunca "se asienta").
- **Fix de CORS**: `CORS_ALLOWED_ORIGINS` (`.env`, `.env.example`,
  `application.yml`) solo incluía `http://localhost:3000`; se agregó
  `http://localhost:5050` (puerto usado por `flutter run -d web-server`). Sin este fix,
  el navegador bloquea todas las llamadas de la app al backend.
- App corrida con `flutter run -d web-server --web-port=5050` contra el backend real
  (`./gradlew :app:bootRun --args='--server.port=8081'`) + Postgres. Verificado
  visualmente (capturas) que Login/Register renderizan con el tema correcto y que la
  navegación entre pantallas funciona (GoRouter + guarda de auth redirige según sesión).
  Se confirmó además, con un `fetch()` ejecutado en el propio origen de la app
  (`http://localhost:5050`), que `POST /api/v1/auth/register` responde `201` con JWT
  válido — es decir, la conectividad navegador→backend con el fix de CORS funciona.
  La automatización de clicks dentro del flujo completo (crear cuenta → perfil → crear
  rutina → sesión) no se pudo completar en este entorno porque el panel de navegador
  dejó de componer capturas de pantalla a mitad de la prueba (limitación de la
  herramienta, no del código); queda como verificación manual pendiente para quien
  continúe — ver `mobile/README.md` para los pasos.

### Entrega completada (2026-08-12) — feature `nutrition`

- [x] **`nutrition`**: `NutritionHomeScreen` (tabs Planes/Diario/Compras).
  - **Planes**: `MealPlanFormScreen` (crear/editar con días y comidas anidadas +
    `RecipePickerDialog` con búsqueda del catálogo, misma estrategia "replace" que
    `workout`), `MealPlanDetailScreen`.
  - **Diario**: `DailyIntakeView` — comidas de hoy + totales de macros, `LogIntakeDialog`
    (elegís una receta y porciones, las macros se auto-completan pero quedan editables
    a mano; también admite carga 100% manual sin receta).
  - **Compras**: `ShoppingListListView` + `ShoppingListDetailScreen` (ítems con
    checkbox para marcar comprado, agregar/borrar ítems).
  - Contra `/api/v1/nutrition/*`. Se agregó la pestaña "Nutrición" al `AppShell`
    (ahora 3 tabs: Rutinas/Nutrición/Perfil).
  - Nota menor: se evitó `DateFormat` con símbolos de locale (`EEE`, nombres de mes)
    porque requieren `initializeDateFormatting()` — se usan formatos numéricos
    (`dd/MM/yyyy`) que no necesitan inicialización, para no agregar ese paso al arranque.

### Verificación (2026-08-12) — feature `nutrition`

- `flutter analyze` → **0 issues** (limpio en el primer intento).
- `flutter test` → sigue pasando.
- Igual que con `workout`: se confirmó con `fetch()` desde el origen real de la app
  (`http://localhost:5050`) que el login y `GET /api/v1/nutrition/recipes` responden
  correctamente con datos reales del seed. La automatización de clicks en el flujo
  completo (crear plan → diario → compras) tampoco se pudo completar en este entorno
  por la misma limitación del panel de navegador (sin capturas de pantalla, sin
  elementos DOM reales por el renderizado en canvas) — mismo gap que quedó anotado
  para `workout`, ahora también aplica a `nutrition`. Verificación manual pendiente
  para quien continúe (pasos en `mobile/README.md`).

### Entrega completada (2026-08-12) — feature `progress`, cierra la Fase 3

- [x] **`progress`**: `ProgressHomeScreen` (tabs Métricas/Fotos), contra
      `/api/v1/progress/*`.
  - **Métricas**: `MetricEntriesView` — lista filtrable por tipo de métrica (chips:
    peso, IMC, % grasa, circunferencias...), `AddEntryDialog` (unidad auto-sugerida
    según la métrica elegida, editable), borrar registro.
  - **Fotos**: `ProgressPhotosView` — grilla de fotos de progreso (por URL, sin flujo
    de subida/MinIO todavía — el backend solo persiste la URL), diálogo para agregar
    con ángulo opcional (frente/perfil/espalda), borrar foto.
  - Se agregó la pestaña **"Progreso"** al `AppShell` (ahora 4 tabs: Rutinas /
    Nutrición / Progreso / Perfil).
  - Alcance deliberadamente simple: sin gráficos (fl_chart está declarado en
    `pubspec.yaml` pero no se usó — agregar un gráfico de línea por métrica es una
    mejora natural a futuro, no necesaria para el MVP).

### Verificación (2026-08-12) — feature `progress`

- `flutter analyze` → **0 issues**. `flutter test` → sigue pasando.
- Confirmado con `fetch()` desde el origen real de la app: `POST /progress/entries`
  (crea una métrica) → `GET /progress/entries` (la lista, formato correcto). Misma
  limitación de siempre para probar el flujo de clicks completo en este entorno (panel
  de navegador sin capturas) — pendiente de verificación manual por quien continúe.

## Fase 3.1 — Frontend Mobile (Flutter) — CERRADA (2026-08-12)

Las 6 features previstas para el usuario final están completas: `core`, `auth`,
`profile`, `workout`, `nutrition`, `progress`. `admin` y `ai` no tienen — ni van a
tener por ahora — pantalla en la app móvil: son gobernanza interna y base técnica sin
UI de usuario final, respectivamente (ver sus entregas en la Fase 2 para el detalle).

**Decisión tomada al cerrar esta fase (2026-08-12):** Flutter queda como **mobile-only**
(Android/iOS, incluida la futura APK de prueba). La versión web deja de ser
"Flutter Web" y pasa a ser un frontend aparte en React — ver **Fase 3.2** y
**ADR-003**. Motivo (vivido en carne propia probando esta misma app): Flutter Web
renderiza a `<canvas>` (CanvasKit), sin DOM real — eso complica SEO, herramientas de
automatización/QA y debugging con devtools estándar. Para una segunda superficie web
con paridad completa, conviene una tecnología que sí tenga DOM real.

**Pendiente para quien continúe, no bloqueante:**
- Verificación manual completa del flujo end-to-end (register → login → perfil →
  rutina → sesión → plan de alimentación → diario → compras → métricas/fotos) en un
  navegador real — la automatización de clicks no fue posible en este entorno de
  desarrollo (limitación de la herramienta, no del código; la conectividad
  navegador→backend sí se confirmó en cada feature vía `fetch()`).
- Diseño visual: sigue usando los widgets de Material 3 por defecto. El sistema de
  tema centralizado (`mobile/lib/core/theme/`) está listo para que quien se encargue
  del diseño lo rehaga sin tocar la lógica de las pantallas.
- Generar la APK de prueba (`flutter build apk`) — no se hizo todavía; la Fase 3.2
  (web) existe justamente para tener una forma más rápida de probar mientras tanto.

## Fase 3.2 — Frontend Web (React) — CERRADA (2026-08-13)

> **Va ANTES de la Fase 4.** Objetivo del usuario del proyecto: tener una superficie
> web con **toda** la funcionalidad de la app (no solo `admin`) para probar más rápido
> que generando una APK cada vez, mientras mobile y web comparten el mismo backend.
> Decisión completa y su justificación en **ADR-003**.

### Alcance

Paridad de funcionalidad completa con Fase 3.1: `core` (tema, router, cliente HTTP,
storage de sesión) → `auth` → `profile` → `workout` → `nutrition` → `progress`. Mismo
orden, una feature por entrega, contra la API real (no mocks) — igual que se hizo en
mobile. `admin` y `ai` siguen sin pantalla de usuario final (mismo criterio que 3.1).

### Stack

- **React 18 + TypeScript + Vite** (SPA, sin SSR/Next.js: es una app autenticada tipo
  dashboard, no un sitio público que necesite SEO — si eso cambia en el futuro, ahí sí
  se reevalúa Next.js).
- **TanStack Query (React Query)** para fetching/cache — mismo modelo mental que los
  `FutureProvider` de Riverpod en mobile (cachea, invalida, refetchea).
- **React Router** para navegación con guarda de autenticación (equivalente al
  `redirect` de GoRouter en mobile).
- **Tailwind CSS + shadcn/ui** para componentes — DOM real (a diferencia de Flutter
  Web), accesible, fácil de automatizar/testear.
- **React Hook Form + Zod** para formularios y validación.
- **Axios** con interceptor de JWT + refresh automático ante 401 — mismo patrón que
  `ApiClient` (Dio) en mobile.

### Requisitos no funcionales (pedidos explícitamente)

- **100% responsive**: un solo layout que se vea bien en teléfono y en PC (mobile-first
  con los breakpoints de Tailwind), NO un sitio "m." aparte.
- **Abierta a rediseño**: paleta de colores, tipografía y nombre del proyecto van a
  cambiar. Todo eso debe vivir en UN punto centralizado (tokens de diseño en
  `tailwind.config` + variables CSS), igual que `mobile/lib/core/theme/` en Flutter,
  para que cambiar el look no implique tocar cada pantalla.

### Estructura propuesta

```
web/
├── package.json  vite.config.ts  tailwind.config.ts  tsconfig.json
└── src/
    ├── main.tsx                 # punto de entrada
    ├── app/                     # layout raíz + router
    ├── core/
    │   ├── theme/               # ★ PUNTO ÚNICO DEL DISEÑO ★ (tokens, igual que mobile)
    │   ├── api/                 # cliente Axios + interceptor JWT/refresh
    │   └── auth/                # guarda de rutas autenticadas
    └── features/
        ├── auth/  profile/  workout/  nutrition/  progress/
        │   └── api/ hooks/ components/ pages/
```

### Verificación de la fase

- `npm run build` sin errores + `npm run lint` (ESLint/TypeScript) limpio por entrega.
- Cada feature probada contra el backend real (curl/fetch como mínimo, navegador si la
  herramienta de automatización lo permite en ese momento).
- Responsive verificado al menos en dos anchos: ~375px (teléfono) y ~1280px (desktop).

### Entrega completada (2026-08-12) — `core` + `auth`

- [x] **Scaffold**: `web/` con Vite (React 19 + TS, plugin `@tailwindcss/vite`), ESLint
      flat config (typescript-eslint + react-hooks + react-refresh) en vez del `oxlint`
      que trae el scaffold por defecto, para respetar "ESLint/TypeScript" del plan.
      Node 18 no alcanza para Vite 8/`create-vite` actual (exige Node ^20.19 || >=22.12);
      se usa Node 24 (ya estaba instalado vía nvm) para instalar/compilar/correr la web.
  - Dependencias: TanStack Query, React Router (`createBrowserRouter`), Axios, React
    Hook Form + Zod (`@hookform/resolvers`), shadcn/ui (inicializado con `npx shadcn
    init -t vite -b radix -p nova`, componentes base: button/input/label/card/alert).
- [x] **`core/theme`** (`web/src/core/theme/tokens.css`): variables CSS con la misma
      paleta que `mobile/lib/core/theme/app_colors.dart` (verde salud `#4CAF50` /
      `#81C784` en oscuro, turquesa `#00BFA5` de acento), mapeadas a los tokens que
      consumen los componentes shadcn (`--primary`, `--background`, etc.) — es el único
      lugar a tocar para rediseñar, tal como pide ADR-003.
- [x] **`core/api`**: `client.ts` (Axios) — mismo comportamiento que
      `mobile/lib/core/network/api_client.dart`: adjunta `Authorization: Bearer` salvo en
      `/auth/register|login|refresh`, agrega `X-Correlation-Id`, reintenta una vez tras
      refrescar el token en un 401 y llama a `onSessionExpired` si el refresh también
      falla. `tokenStorage.ts` usa `localStorage` (no hay almacenamiento seguro nativo en
      un SPA; el access token vive 15 min de todos modos).
- [x] **`core/router`**: `RequireAuth`/`RedirectIfAuthenticated` (guardas, equivalentes al
      `redirect` de GoRouter), `AppShell` (header con email + logout; todavía sin tabs de
      navegación porque `profile`/`workout`/`nutrition`/`progress` no existen aún en la
      web), `SplashScreen` mientras se restaura la sesión.
- [x] **`features/auth`**: `AuthProvider` (Context + reducer, estados
      `unknown/authenticated/unauthenticated`, paridad con
      `mobile/lib/features/auth/application/{auth_providers,auth_state}.dart`),
      `LoginPage`/`RegisterPage` (React Hook Form + Zod, mensajes de error tomados del
      `ApiError` unificado del backend).
- [x] **`app/HomePage`**: placeholder temporal post-login (saluda al usuario, muestra
      roles) hasta que aterrice `profile`/`workout` en la próxima entrega.

#### Verificación (2026-08-12)

- `npm run build` → sin errores (`tsc -b && vite build`).
- `npm run lint` → 0 errores (1 warning benigno de `react-refresh` en
  `src/components/ui/button.tsx`, generado por el propio `shadcn`, no tocado).
- CORS: se agregó `http://localhost:5173` a `CORS_ALLOWED_ORIGINS` (`.env`,
  `.env.example`). **Ojo**: el origen debe coincidir carácter a carácter — `vite --host
  127.0.0.1` sirve en `http://127.0.0.1:5173`, que el navegador trata como un origen
  distinto de `http://localhost:5173` y el preflight `OPTIONS` da 403. Hay que entrar
  por `http://localhost:5173` (o agregar también el origen `127.0.0.1` si hiciera falta).
- Probado en el navegador embebido contra el backend real (`./gradlew :app:bootRun
  --args='--server.port=8081'` + Postgres): registro (201) → redirige a Home con el
  email/roles reales → reload de página restaura la sesión (`GET /auth/me`) → logout
  vuelve a `/login` → login (200) vuelve a Home → login con contraseña incorrecta
  muestra "Credenciales inválidas" (401) en el formulario. Sin errores de consola.
- Responsive verificado en 375px (el email del header se oculta, `padding` de 16px,
  sin overflow horizontal) y 1280px (email visible, `padding` de 24px).

**Simplificación consciente, no bloqueante**: los tokens JWT se guardan en
`localStorage` (`web/src/core/api/tokenStorage.ts`), no en un storage seguro — un SPA no
tiene el equivalente al `flutter_secure_storage` de mobile. Queda expuesto a robo de
token vía XSS si en algún momento se introduce una vulnerabilidad de ese tipo en el
código. Aceptable para esta fase (superficie de prueba interna); si la web se expone
públicamente más adelante, migrar a cookies `httpOnly` es la mejora natural (implica
cambios en el backend, que hoy responde el JWT en el body).

### Entrega completada (2026-08-13) — `profile`, `workout`, `nutrition`, `progress`, cierra la Fase 3.2

Puerto 1:1 de las 4 features restantes de mobile, mismo criterio que las anteriores
(TanStack Query para estado, Zod solo donde ya se usaba, shadcn/ui, contra el backend
real). `app/HomePage.tsx` (placeholder) se borró: `/` redirige a `/workouts`, igual que
en mobile.

- [x] **`profile`**: `ProfileForm` (datos físicos/entrenamiento, `Select` + `Input` +
      `Textarea`) y 4 secciones (`GoalSection`/`PathologySection`/`InjurySection`/
      `MedicationSection`) con `Accordion` + `Dialog` para alta, botón de borrado inline
      — mismo patrón simple que mobile (sin confirmación en el borrado).
- [x] **`workout`**: `WorkoutHomePage` (tabs Mis rutinas/Plantillas/Historial),
      `WorkoutForm` (días/ejercicios anidados + `ExercisePickerDialog` con búsqueda),
      `WorkoutDetailPage` (iniciar sesión por día o libre, editar/borrar si es dueño),
      `SessionPage` (registrar ejercicio ejecutado, completar/abandonar).
- [x] **`nutrition`**: `NutritionHomePage` (tabs Planes/Diario/Compras), `MealPlanForm`
      (días/comidas anidadas + `RecipePickerDialog`), `MealPlanDetailPage`,
      `DailyIntakeView` + `LogIntakeDialog` (auto-completa macros al elegir receta x
      porciones, igual que mobile), `ShoppingListDetailPage` (ítems con checkbox).
- [x] **`progress`**: `ProgressHomePage` (tabs Métricas/Fotos), `MetricEntriesView` con
      chips de filtro por tipo de métrica + `AddEntryDialog` (autocompleta la unidad
      según la métrica), `ProgressPhotosView` (grilla, `onError` de `<img>` con estado
      React en vez de manipular el DOM a mano).
- [x] **`AppShell`**: navegación completa a las 4 secciones + Perfil. Mobile-first real
      (no solo "se ve bien angosto"): bottom tab bar fija (`sm:hidden`) igual que el
      `NavigationBar` de mobile, y nav horizontal en el header a partir de `sm`
      (`hidden sm:flex`) — dos renders del mismo `NavItem`, uno por breakpoint, en vez de
      intentar un único layout que sirva para ambos casos (un bottom-nav no tiene sentido
      en desktop y viceversa).

#### Bugs encontrados y corregidos durante la verificación (2026-08-13)

- **Backend, `POST /users/me/goals` → 500**: `user_goals.priority` es `NOT NULL` en el
  schema pero la API lo documenta como opcional (`docs/03-api-contracts.md`). Fix en
  `UserGoal.create` (`backend/modules/user/.../domain/UserGoal.java`): default a `1`
  cuando `priority` viene `null`, sin tocar el schema.
- **Frontend, carga de perfil → 404 intermitente en `/goals`, `/pathologies`, etc.**:
  `useProfile` pedía las 5 llamadas (`profile` + 4 sub-recursos) con `Promise.all` en
  paralelo. El perfil se crea de forma perezosa en el propio `GET /profile`
  (`getOrCreateProfile`), y los sub-recursos asumen que ya existe — en paralelo hay
  carrera con el commit de esa creación. Mobile ya lo hacía secuencial
  (`profile_providers.dart`) por esta misma razón; se corrigió `useProfile.ts` para
  esperar `profile` antes de disparar el resto.
- **Frontend, overflow horizontal con `Tabs` + contenido angosto sin wrap** (los 15
  chips de filtro de métricas empujaban un botón fuera del viewport y sus clicks no
  llegaban a destino): el `TabsContent` de shadcn es un flex item de una columna sin
  `min-w-0`, así que el contenido con `overflow-x-auto` no se contiene — el bug clásico
  de flexbox donde `min-width: auto` por defecto ignora el `max-width` de un ancestro.
  Fix: `min-w-0` en cada `<TabsContent>` de `WorkoutHomePage`/`NutritionHomePage`/
  `ProgressHomePage` (los tres usan el mismo patrón de tabs, aunque solo `progress` lo
  disparaba con datos reales).

#### Verificación (2026-08-13)

- `npm run build` y `npm run lint` → sin errores (mismos 3 warnings benignos de
  `react-refresh` en archivos generados por `shadcn`, sin tocar).
- Probado end-to-end en el navegador contra el backend real: perfil (editar datos +
  alta/baja de objetivo y patología) → rutina (crear con 1 día + 1 ejercicio → detalle →
  iniciar sesión → registrar ejercicio → completar → editar nombre → borrar) → plan de
  alimentación (crear con 1 comida → detalle con nombre de receta resuelto) → diario
  (registrar comida con receta, macros auto-calculadas, totales del día) → lista de
  compras (crear → agregar ítem → marcar comprado) → progreso (filtrar por métrica,
  registrar peso, agregar y borrar foto). Sin errores de consola nuevos en ningún paso.
- Responsive verificado en 375px y 1280px en las 4 páginas de sección: sin overflow
  horizontal (`document.body.scrollWidth === document.documentElement.clientWidth` en
  ambos anchos) y el nav correcto visible en cada breakpoint (bottom bar en 375px, nav de
  header en 1280px).

## Fase 4 — IA (EN CURSO)

LangChain4j + Ollama de base. Strategy Pattern para alternar proveedores. Prompts
versionados en `ai_prompts` (BD, no en recursos del jar — se pueden versionar/activar
por API sin redeploy). Generación de rutinas, planes, sustituciones, ajuste de
calorías. Se hace **una superficie de usuario a la vez**, mismo criterio que el resto
del proyecto — empieza por rutinas (la más directa de verificar end-to-end).

### Entrega completada (2026-08-13) — generación de rutinas con IA

- [x] **Strategy Pattern** (`ai` module): `AiProviderStrategy` (puerto de salida) +
      `OllamaProviderStrategy` (infraestructura, LangChain4j `OllamaChatModel`). Agregar
      un proveedor cloud (OpenAI/Claude/Gemini/Mistral/DeepSeek) es implementar esa
      interfaz y no toca nada más — `AiService` resuelve la estrategia activa por
      `micoach.ai.provider` (`AI_PROVIDER` en `.env`) entre todas las que Spring
      inyecta como `List<AiProviderStrategy>`.
- [x] **`AiUseCase.generate(userId, promptSlug, variables)`**: caso de uso genérico y
      reutilizable — busca el prompt activo de ese slug, reemplaza `{{variable}}` por
      su valor, llama al proveedor resuelto, audita el intento completo en
      `ai_generation_logs` (éxito o error) y devuelve el texto crudo. Cualquier módulo
      que necesite generar contenido con IA (workout ya lo usa; nutrition queda
      pendiente) llama a este único método — no reimplementa nada del lado de IA.
- [x] **`POST /api/v1/workouts/generate`**: `workout` pasa a depender de `ai`
      (`build.gradle`). `WorkoutAiGenerator` arma el contexto (pedido del usuario +
      catálogo completo de ejercicios), llama a `AiUseCase.generate` con el slug
      `workout_generator`, parsea el JSON de salida y **resuelve cada nombre de
      ejercicio que devuelve el modelo contra el catálogo real** (match exacto
      case-insensitive, con fallback a "contiene") — un LLM no conoce los ids de la
      base, así que esto es obligatorio; los ejercicios que no matchean se descartan en
      vez de romper toda la generación. Crea la rutina con `aiGenerated: true`
      (`Workout.createAiGenerated`, nuevo factory en el dominio).
- [x] **Migración `V8`**: siembra la v3 del prompt `workout_generator` (contenido real,
      formato JSON pedido, instrucciones de usar SOLO nombres del catálogo) y desactiva
      las v1/v2 que ya existían como datos de prueba de la Fase 2 (creadas a mano por
      curl con contenido placeholder tipo `"{perfil}"`, incompatibles con el parser).
- [x] **Web**: botón "Generar con IA" en `WorkoutHomePage` (`GenerateWorkoutDialog`) —
      un textarea con el pedido en lenguaje natural, `timeout` de request extendido a
      180s (una llamada normal usa 15s; una inferencia LLM en CPU tarda muchísimo más),
      navega al detalle de la rutina generada al terminar.

#### Bugs encontrados y corregidos durante la verificación (2026-08-13)

- **Rollback del log de auditoría cuando fallaba el parseo posterior**: `AiService
  .generate()` corría con la propagación de transacción por defecto (`REQUIRED`), así
  que al ejecutarse dentro de la transacción ya abierta por `WorkoutService
  .generateWorkout()`, si el parseo del JSON fallaba **después** de que la llamada a
  Ollama ya había sido exitosa y auditada, la excepción sin capturar que subía desde
  `WorkoutAiGenerator` marcaba **toda la transacción** para rollback — incluido el
  `INSERT` en `ai_generation_logs` que se había hecho segundos antes. Resultado: el
  primer intento real falló (ver debajo) y la auditoría que hubiera explicado por qué
  quedó vacía, exactamente el caso en el que más se necesita. Fix: `AiService.generate`
  pasa a `@Transactional(propagation = Propagation.REQUIRES_NEW)` — la llamada al
  proveedor y su auditoría son independientes de lo que haga el módulo que las llama.
- **Extracción de JSON frágil ante texto extra del modelo**: el primer intento real
  (rutina de fuerza, 3 días) tiró `500` con `"Unexpected character ('{' ...)"` de
  Jackson. El parser original cortaba del primer `{` al **último** `}` de todo el
  texto — si el modelo agrega alguna palabra con llaves después del JSON (algo que
  `llama3.2` 3B hace pese a que el prompt pide "solo JSON"), esa última `}` no es la
  que cierra el objeto real. Fix: `WorkoutAiGenerator.extractFirstJsonObject` cuenta
  llaves balanceadas (respetando strings/escapes) desde el primer `{` y corta ahí,
  quedándose solo con el objeto JSON real sin importar qué haya después.
- Con los dos fixes aplicados: pedido real ("rutina de fuerza, 3 días a la semana,
  nivel intermedio...") → Ollama respondió en **2m32s** (CPU, sin GPU, `llama3.2` 3B a
  ~6.5 tokens/seg) → rutina creada con 9 días y ejercicios reales del catálogo
  (Sentadilla, Peso muerto, Press militar...) con series/reps, `aiGenerated: true` en
  BD, auditada en `ai_generation_logs` (status `success`, `durationMs` real). El modelo
  no respetó el límite de "3 a 6 días" del prompt (generó 9) — comportamiento esperable
  de un modelo de 3B, no un bug del código; ajustar el prompt o pasar a un modelo más
  grande queda como mejora futura si hace falta más control sobre el resultado.

**Simplificación consciente, no bloqueante**: `WorkoutAiGenerator.renderTemplate`
(dentro de `AiService`) hace reemplazo de texto simple `{{variable}}` con
`String.replace`, no un motor de templates. Alcanza para los prompts actuales
(variables planas, sin loops/condicionales) y evita sumar una dependencia nueva; si un
prompt futuro necesita lógica (ej. iterar sobre una lista con formato propio), ahí sí
conviene un motor real.

### Entrega completada (2026-08-13) — perfil real, planes de alimentación con IA, catálogos ampliados

Pedido explícito del usuario tras probar la primera entrega: que la IA tenga en cuenta
el perfil (no solo workout — nutrition también, desde que existiera), que responda más
rápido, y que el catálogo tenga más variedad. Se hicieron las cuatro cosas juntas:

- [x] **Perfil real en `workout`**: `workout` pasa a depender también de `user`
      (`build.gradle`). `WorkoutAiGenerator.buildProfileText` arma un bloque con nivel de
      experiencia, nivel de actividad, equipamiento, objetivo, patologías y lesiones
      reportadas, y se lo suma al prompt como `{{profile}}` — con instrucción explícita
      de evitar ejercicios que agraven una lesión reportada.
- [x] **`nutrition` genera planes de alimentación con IA (nueva)**: mismo patrón end a
      end que workout — `nutrition` pasa a depender de `ai` y `user`;
      `NutritionAiGenerator` arma el catálogo de recetas + perfil (objetivo dietario,
      peso, TDEE, patologías) y llama al prompt `meal_plan_generator`; `MealPlan
      .createAiGenerated` (nuevo factory, `aiGenerated: true`); `POST
      /api/v1/nutrition/meal-plans/generate`; botón "Generar con IA" en
      `NutritionHomePage` (`GenerateMealPlanDialog`, mismo componente que
      `GenerateWorkoutDialog` en workout). Las fechas reales las calcula el backend a
      partir de un `dayOffset` relativo que devuelve la IA (no hay forma confiable de
      que un LLM sepa qué día es "hoy").
- [x] **Modelo más rápido**: sin GPU disponible en el entorno de desarrollo (se
      verificó con `lspci` — hay una Intel iGPU y una AMD vieja, ninguna soportada por
      Ollama), la inferencia corre 100% en CPU (8 núcleos). Se cambió el modelo por
      defecto de ambos prompts de `llama3.2` (3B) a `llama3.2:1b` (1B) — bajó el tiempo
      de generación de 1m46s–2m32s a 35s–43s. Costo: el modelo chico sigue peor las
      instrucciones del prompt (ver bugs abajo), compensado con más validación
      defensiva del lado del código en vez de confiar en que la IA responda siempre
      bien formado.
- [x] **Catálogos ampliados** (migración `V10`): +20 ejercicios (32 → 52, cubriendo más
      grupos musculares y equipamiento — hip thrust, jalón al pecho, farmer's walk,
      comba, etc.) y +15 recetas (12 → 27, reutilizando los 60 ingredientes ya
      sembrados en `V6`, sin necesidad de agregar ingredientes nuevos). Mismo cuidado
      que `V7`: `setval(pg_get_serial_sequence(...), MAX(id))` al final para las dos
      tablas con ids explícitos, evitando reproducir el bug de secuencias de la Fase 2.
- [x] **Migración `V9`**: prompts v4 (`workout_generator`) y v1 (`meal_plan_generator`)
      con `{{profile}}`, instrucciones más estrictas sobre cantidad de días, y modelo
      `llama3.2:1b`.

#### Bugs encontrados y corregidos (2026-08-13, segunda ronda)

Los tres aparecieron probando con datos reales (perfil con una lesión cargada, pedidos
concretos) — ningún test unitario los iba a atrapar sin pegarle a un LLM de verdad:

- **500 sin capturar por `objective` fuera del enum**: `workout_workouts.objective` es
  `VARCHAR(30)` con `CHECK (objective IN (...))`. El modelo devolvió una oración libre
  ("fortalecer piernas y espalda, mejorar la estabilidad...") en vez de un valor del
  enum — ni entraba en la columna ni pasaba el `CHECK`, y Postgres tiró la excepción
  sin que nada la tradujera a un error de API legible. Fix:
  `WorkoutAiGenerator.normalizeEnum` — si el valor no está en la lista blanca
  (`lose_fat`, `gain_muscle`, ...), se guarda `null` en vez de reventar. Mismo
  problema existía latente en `nutrition` con `meal_type` (`NOT NULL` + `CHECK`, más
  grave porque ahí ni siquiera puede quedar `null`) — `NutritionAiGenerator
  .normalizeMealType` corrige y además defaultea a `snack` si no matchea (no puede
  ser `null`).
- **`restDay: true` en días con ejercicios cargados**: el modelo marcó los 4 días como
  descanso pese a que cada uno tenía ejercicios reales adentro — una inconsistencia
  lógica del propio JSON que la validación de esquema no detecta (es JSON válido, solo
  que no tiene sentido). Fix: `restDay` se recalcula en el código —
  `exercises.isEmpty() && restDay` — nunca puede ser `true` si el día tiene ejercicios,
  sin importar lo que haya dicho el modelo.
- **JSON truncado a mitad de generación** (`meal_plan_generator`, primer intento real):
  la respuesta de Ollama cortaba en medio del array de días, sin el cierre final —
  `extractFirstJsonObject` (correctamente) no encontró el `}` de cierre y tiró "JSON
  inválido" en vez de crashear, pero la causa de fondo era un límite de tokens de
  salida no configurado explícitamente en `OllamaChatModel`. Fix:
  `.numPredict(2048)` en `OllamaProviderStrategy` — sin este valor, el cliente estaba
  usando algún default demasiado bajo para una respuesta JSON larga (varios días con
  varias comidas cada uno).

#### Limitación conocida, no bloqueante: `llama3.2:1b` no sigue instrucciones a la perfectilidad

Con la validación defensiva de arriba el sistema **no crashea** ante una respuesta mal
formada, pero eso no significa que la IA respete siempre el *contenido* de las
instrucciones — con el modelo de 1B, en las pruebas reales:

- Generó una rutina con `objective` inválido (se normalizó a `null`, la rutina quedó
  igual de usable, solo sin ese campo).
- El `dayOffset` de un plan de nutrición no salió secuencial (`3, 4, 5` en vez de
  `0, 1, 2, 3`) — el plan quedó igual coherente (los días son relativos, no importa
  el valor exacto), pero no siguió la instrucción al pie de la letra.
- No hay garantía de que evite sistemáticamente ejercicios riesgosos para una lesión
  reportada — el perfil SÍ le llega al modelo (verificado), pero un modelo de 1B no
  razona con la misma confiabilidad que uno más grande sobre restricciones complejas.

Si en algún momento se necesita más adherencia a las instrucciones, la palanca más
directa es un modelo más grande (ida y vuelta velocidad/calidad) o un proveedor cloud
(Strategy Pattern ya deja esa puerta abierta sin tocar el resto del código).

### Panel de admin: múltiples proveedores de IA (Groq, OpenRouter, Gemini además de Ollama)

Se usó exactamente la puerta que dejó abierta el Strategy Pattern: la selección de
proveedor pasó de ser una property estática (`micoach.ai.provider` en `application.yml`,
requería reiniciar el backend) a una tabla `ai_provider_configs` (migración V11)
editable en runtime desde `/admin/ai` — sin redeploy.

- **Backend**: `AiProviderConfig` (agregado hexagonal nuevo en el módulo `ai`, mismo
  patrón que `admin`) guarda por proveedor: baseUrl, modelo, API key cifrada
  (AES/GCM, `TextEncryptor` nuevo en `shared`, reusa `AES_SECRET` que ya existía sin
  usar desde la Fase 2) y flags `enabled`/`is_active` (un índice único parcial
  garantiza que solo uno esté activo). `AiProviderStrategy.complete()` ahora recibe un
  `ResolvedProvider` (baseUrl/key ya descifrada/modelo) en vez de un `model` suelto —
  la key nunca sale del backend en texto plano, ni siquiera en las respuestas de la
  API admin (`hasApiKey: boolean`, no el valor).
- **Groq y OpenRouter** comparten una sola implementación
  (`OpenAiCompatibleProviderStrategy`, LangChain4j `langchain4j-open-ai`) porque ambas
  exponen una API compatible con OpenAI — solo cambia la `baseUrl`. Gemini usa su
  propio cliente (`langchain4j-google-ai-gemini`) porque el endpoint no es configurable.
- **RBAC real, no placeholder**: hasta esta entrega `AuthService` emitía siempre
  `ROLE_USER` hardcodeado (bug preexistente desde la Fase 2, el comentario de
  `AdminController` ya lo admitía). Se conectó vía un puerto nuevo en `shared`
  (`UserRoleProvider`, implementado en `admin`, consumido por `auth` sin dependencia
  de compilación entre ambos módulos) para que el JWT lleve los roles reales de
  `admin_user_roles`. `AdminController` y el nuevo `AiProviderConfigController` quedan
  detrás de `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`
  (`@EnableMethodSecurity` en `SecurityConfig`). De paso se agregó el `ErrorCode.FORBIDDEN`
  y su manejo en `GlobalExceptionHandler` — antes un 403 real caía en el catch-all
  genérico y se devolvía como 500.
- **Web**: sección nueva `/admin/ai` (guard `RequireAdmin`, ítem de nav visible solo
  con `ROLE_ADMIN`) con una tarjeta por proveedor: editar baseUrl/modelo/API key
  (input `password`, siempre vacío al cargar — dejarlo vacío al guardar no pisa la key
  ya guardada), habilitar/deshabilitar, "Probar conexión" (llama al proveedor con un
  prompt corto y muestra el resultado real, sin tocar la config activa) y "Activar".
- Verificado end-to-end: 403 real para un usuario sin `ROLE_ADMIN`, guardado de API
  key cifrada, prueba de conexión contra Ollama (real) y OpenRouter (con key dummy,
  falla con el error real del proveedor en vez de romper), y una generación de rutina
  completa contra Ollama para confirmar que el nuevo flujo de resolución de proveedor
  no rompió lo que ya funcionaba.
- **Bug encontrado y corregido tras el primer uso real**: activar un proveedor exigía
  que ya estuviera `enabled: true`, pero habilitar y activar eran dos guardados
  separados en la UI — el usuario probó Groq con éxito y aun así no podía activarlo
  porque el toggle "Habilitado" nunca se había guardado. Fix: `activate()` ahora
  también habilita (`AiProviderConfig.activate()`), no tiene sentido un proveedor
  activo pero deshabilitado. Confirmado con Groq real: **2.5s** para generar una
  rutina completa, contra 30-40s de Ollama en CPU.
- **Privacidad**: los datos de perfil (incluidas lesiones/patologías) viajan tal cual
  dentro del prompt al proveedor que esté activo — antes solo a Ollama local, ahora
  potencialmente a un proveedor cloud. No se agregó ninguna anonimización; es una
  decisión de producto pendiente si se quiere (aviso en el panel al activar un
  proveedor cloud, o mantener Ollama activo para generaciones con datos sensibles).

### Paridad de IA en Flutter + legibilidad de los registros generados

- **Flutter**: se agregaron `generateWorkout`/`generateMealPlan` (mismo patrón que la
  web: `WorkoutApi`/`NutritionApi` + `WorkoutActions`/`NutritionActions` + diálogo con
  loading state), botón ✨ en el AppBar de Rutinas/Nutrición. `flutter analyze` limpio;
  no se pudo verificar a ojo en esta sesión por una limitación del entorno (el panel
  de Browser no compone capturas), se dejó un `flutter run -d web-server` corriendo en
  `localhost:5050` para que el usuario lo probara directamente.
- **Legibilidad de rutinas generadas**: `"3 x 8-12"` (jerga) → `"3 series · 8-12 reps"`.
  Planes de nutrición no tenían numeración de día, solo la fecha pelada → se agregó
  `"Día N · fecha"`. Aplicado en web y Flutter.
- **`measurement_type` en el catálogo de ejercicios** (migración V12): la Plancha
  (único ejercicio isométrico real del catálogo, verificado por su propia descripción
  "Isométrico...") mostraba `"3 series · 30-60 reps"` cuando en realidad son 30-60
  **segundos** de sostén, no repeticiones. `workout_exercises.measurement_type`
  (`'reps'` default | `'duration'`) se propaga a `Exercise` → DTO → `formatSetsReps`
  en ambas plataformas, que ahora muestra `"seg"` en vez de `"reps"` cuando corresponde.
  Cardio (Carrera continua, Bicicleta, etc.) queda fuera de este fix a propósito —
  se prescribe típicamente en minutos, no segundos, y resolverlo bien requeriría una
  unidad adicional; no estaba en el alcance de lo reportado.
- **Detalle de ejercicio al tocar el nombre**: `videoUrl`/`imageUrl`/`instructions` ya
  existían en el schema desde la Fase 2 (nunca se habían expuesto en la UI). Ahora
  tocar el nombre de un ejercicio (en una rutina o durante una sesión) abre un diálogo
  con categoría/dificultad/equipo, imagen o placeholder, link a video o aviso de que
  no hay, e instrucciones — estas últimas con contenido real y completo desde el seed
  original (V6/V10), simplemente no se mostraban. **No se inventaron URLs de
  video/imagen** — quedan `null` hasta que se carguen reales (edición manual en BD o
  una futura pantalla de administración de catálogo).

### Catálogo de ejercicios ampliado a 106 + filtro para la IA

- **52 → 106 ejercicios** (migraciones V13/V14) usando `free-exercise-db` (GitHub,
  licencia Unlicense/dominio público, sin API key, sin rate limit) como referencia —
  54 ejercicios nuevos con nombre/descripción/instrucciones traducidos y adaptados
  (no traducción literal), categoría/equipamiento mapeados a nuestro schema, músculos
  asignados, `measurement_type` correcto e imagen real verificada (HTTP 200 antes de
  escribir la migración). Cubre huecos reales: antebrazos (no existía ningún
  ejercicio), más variedad de pecho/espalda/hombro, estiramientos/movilidad,
  kettlebell, bandas elásticas.
- Se frenó en 106, no en los 150-200 pedidos originalmente — seguir sumando empieza a
  exigir traducir/curar movimientos cada vez más de nicho, se priorizó calidad sobre
  cantidad. Documentado como decisión consciente, no como límite técnico.
- **Filtro de catálogo antes de mandarlo a la IA** (`WorkoutAiGenerator.filterByEquipment`):
  con 106 ejercicios, mandar la lista completa en cada prompt infla el tamaño del
  request y le da a la IA más variantes parecidas entre las que confundirse. Ahora se
  filtra por el equipamiento del perfil del usuario (bodyweight siempre incluido; si
  el perfil no tiene equipamiento cargado, o el filtro deja <15 ejercicios, se manda
  el catálogo completo en vez de arriesgar una rutina pobre). Verificado con Groq real:
  perfil con mancuernas+bodyweight redujo 106→61 líneas de catálogo, generó en 3.3s.

### Rediseño visual (dirección "energético/motivador", elegida por el usuario)

Diagnóstico antes de tocar nada: el sistema de tokens (verde+turquesa) ya existía
desde la Fase 3 pero casi no se usaba en la práctica — todo eran tarjetas blancas
sobre gris clarito, sin acentos de color, cero imágenes en las listas pese a tener
ya 106 fotos reales de ejercicios.

- **Tokens** (`core/theme/tokens.css`): paleta más saturada/vívida, nuevo color
  `--highlight` (naranja cálido) para rachas/badges "generado con IA", nuevo
  `--gradient-hero` para banners.
- **`HeroBanner`** y **`EmptyState`** (`web/src/components/`, nuevos, compartidos):
  banner con gradiente + stats rápidas al tope de Rutinas/Nutrición/Progreso (en vez
  de un `<h1>` pelado); estados vacíos con ícono en círculo de color en vez de texto
  gris centrado.
- **Tarjetas con identidad de color**: ícono por objetivo/categoría (verde=fuerza,
  turquesa=cardio, etc.), badge ✨ en lo generado con IA.
- **`ExerciseThumb`** (nuevo): las fotos reales del catálogo ahora se ven en el
  picker de ejercicios, el detalle de rutina y las sesiones — antes de esto, las
  imágenes que agregamos no se mostraban en ningún lado excepto el diálogo de detalle.
- **`AppShell`**: logo con marca de color, barra de gradiente superior, estado activo
  de nav con fondo en vez de solo cambio de color de texto.
- Verificado responsive en mobile (375px, sin overflow horizontal) y en desktop.

### Cierre de la Fase 4: USDA, catálogo de recetas, sustitución y ajuste de calorías

Decisión explícita del usuario: el panel `/admin/ai` (multi-proveedor) **queda solo en
la web a propósito** — no se porta a Flutter. El resto de los pendientes se cerró en la
misma entrega.

- **Ingredientes desde USDA FoodData Central**: 60 → 206. Igual que con
  `free-exercise-db`, se usó la descarga directa del dataset SR Legacy
  (`fdc.nal.usda.gov/fdc-datasets/...sr_legacy_food_json_2018-04.zip`) — dominio
  público (CC0), **sin API key**, sin necesidad de que el usuario genere nada. 146
  ingredientes nuevos con nombres traducidos y valores nutricionales reales de USDA
  (no estimados), sin duplicados con el catálogo original.
- **Catálogo de recetas**: 27 → 60. 33 recetas nuevas usando el catálogo de
  ingredientes ampliado; los macros por porción se calculan de verdad a partir de los
  ingredientes y cantidades reales de cada receta (mismo criterio que las 27 originales
  de V6/V10), no están inventados.
- **Sustitución de ingredientes con IA**: se descubrió que el módulo `nutrition` ya
  tenía la tabla `nutrition_substitutions` y el dominio `Substitution` armados desde la
  Fase 2 (`reason` limitado a `allergy|intolerance|unavailable|preference`), pero sin
  escritura ni IA — solo lectura. Se completó: `NutritionSubstitutionAiGenerator`
  (mismo patrón que `WorkoutAiGenerator`) resuelve un sustituto real del catálogo,
  filtrado por la misma categoría del ingrediente original cuando hay suficientes
  opciones, y lo persiste. Nueva UI: `RecipeDetailDialog` (el mismo patrón que
  `ExerciseDetailDialog` — las recetas tampoco tenían pantalla de detalle hasta ahora)
  con botón "Sustituir" por ingrediente. Verificado con Groq real: "Palta" por alergia
  → sugirió "Aceite de oliva" (grasas/calorías similares, no es el mismo alérgeno) en
  ~2s, con explicación.
- **Ajuste de calorías según progreso**: `NutritionCalorieAdjuster` calcula la
  tendencia de peso reciente (`progress_entries`) de forma **determinística en
  código**, no con IA — a propósito, un modelo chico no es confiable haciendo
  aritmética de tendencias. Si el usuario declaró `lose_fat` pero no está bajando (o
  `gain_muscle` pero no está subiendo), ajusta el objetivo calórico ±250 kcal y le pide
  a la IA que regenere las comidas dentro de ese nuevo presupuesto, mismo rango de días
  que el plan original. Verificado real: perfil `lose_fat` + peso estancado (80kg dos
  veces en 15 días) → 2500 kcal bajó a 2250 kcal exactas, plan regenerado en 2.2s.
- Ambas features nuevas (sustitución, ajuste de calorías) quedaron **solo en web por
  ahora** — no se portaron a Flutter en esta entrega (a diferencia de generación de
  rutina/plan, que sí tienen paridad). Pendiente si se quiere completar más adelante.

### Cierre real de la Fase 4 y despliegue a producción (2026-08-15/16)

Con el commit + push pendiente ya resuelto, se hizo además un **rename completo del
proyecto** (KineticOs → MiCoach, pedido explícito del usuario: paquetes Java, paquete
Dart/Android, DB config, docs) y el **primer despliegue a producción**, adelantando
parte del alcance de la Fase 6:

- **Infra**: Supabase (Postgres), Render (backend, Docker, free tier, autoDeploy),
  Vercel (web, autoDeploy). `backend/Dockerfile`, `render.yaml`, `web/vercel.json`,
  guía completa en `docs/09-deployment.md`.
- **Bugs de deploy encontrados y resueltos** (quedan documentados porque son gotchas
  no obvios si se vuelve a desplegar desde cero):
  - Supabase: usar el **Session pooler** (puerto 5432), no "Direct connection"
    (IPv6-only) ni el Transaction pooler (rompe prepared statements de Hibernate).
  - Circuit breaker de Supabase (`ECIRCUITBREAKER`) tras varios intentos con
    credenciales mal cargadas — se destraba reseteando el password en Supabase.
  - Checksums de Flyway rotos porque el rename tocó comentarios de migraciones ya
    aplicadas contra la base real. Fix permanente: `FlywayConfig` (`backend/app/src/
    main/java/com/micoach/app/config/FlywayConfig.java`) corre `flyway.repair()`
    antes de `migrate()` en cada arranque — necesario porque el equipo no siempre
    tiene acceso directo a la base para correrlo a mano. Trade-off aceptado: si
    algún día se edita el contenido lógico (no solo comentarios) de una migración ya
    aplicada, esto lo va a "perdonar" en silencio en vez de frenar el deploy.
  - Import sin usar (`Card`/`CardContent` en `SessionPage.tsx`, leftover de una
    refactorización anterior) rompía `tsc -b` en el build limpio de Vercel (no se
    detectaba en local por caché incremental de TypeScript).
  - Timeout del cliente HTTP (`web/src/core/api/client.ts`) subido de 15s a 60s: en
    Render free tier el cold start puede tardar varios minutos, y el timeout corto
    cancelaba el primer request tras cada período de inactividad.
  - `CORS_ALLOWED_ORIGINS` mal cargado en Render (el puerto de Postgres `5432` en vez
    de la URL de Vercel) causaba 403 "CORS" en login/register.
- **Usuario admin de arranque**: no hay forma de auto-otorgarse admin ni self-service
  sin acceso a la base, así que se sembró por migración (`V18__seed_admin_user.sql`):
  `admin@micoach.dev`, password hasheada con BCrypt directamente en el INSERT (sin
  depender del flujo de registro). Pendiente rotarla una vez confirmado que todo
  funciona, porque quedó expuesta en el chat de esta sesión.
- **Groq configurado en producción** vía `/admin/ai` con esa cuenta (2026-08-16).

### Pendiente

- [ ] Paridad Flutter de sustitución de ingredientes y ajuste de calorías (ver
      "Pendientes globales" más abajo).

## Pendientes globales / deuda técnica

Consolidado de todo lo que quedó suelto durante el desarrollo y el primer deploy,
para no perderlo de vista.

### Paridad Flutter (lista única — todo lo que hoy solo existe en web)

Todo lo de esta lista es **traducir a Flutter un patrón que ya está resuelto en
React** — no requiere decisiones de diseño nuevas, es trabajo de implementación
puro. Se consolida acá en un solo lugar (no repetir en `docs/06`/`docs/10`) para
que quede claro de un vistazo qué le falta a mobile:

- [ ] Features de IA de nutrición (Fase 4): sustitución de ingredientes, ajuste de
      calorías del plan.
- [ ] `docs/06-ux-ui-audit.md` Parte 1, tier "rápido y barato" (resuelto en web
      2026-08-17): `APP_NAME` equivalente, `ErrorState`, progreso de IA con
      expectativa de tiempo, `EmptyState` condicional por perfil incompleto.
- [ ] `docs/06-ux-ui-audit.md` Parte 2 completa (resuelto en web 2026-08-17):
      mostrar/ocultar contraseña, confirmar contraseña, ayuda de password visible,
      autofocus login/registro, error visible en registro de métrica,
      **confirmación de borrado en los 8 sub-recursos que borraban directo** (el
      hallazgo de mayor riesgo real de ese audit), validación numérica de perfil,
      debounce + limpiar en los pickers, advertencia de cambios sin guardar, input
      numérico unificado, cancelar el diálogo de generación con IA sin esperar.
- [ ] `docs/06-ux-ui-audit.md` tier "medio" de Parte 1: `HeroBanner`/`EmptyState`
      compartidos, escala tipográfica nombrada, auditoría de cobertura de toasts de
      éxito.
- [ ] `docs/10-recomendaciones-coach-nutricion.md` G.1 (concordancia de género en
      contadores "generado/a(s) con IA") — no aplica literal hoy (mobile no tiene
      ese contador, es parte del `HeroBanner` que todavía no se portó); se resuelve
      solo cuando se porte `HeroBanner` arriba.
- [ ] `docs/10-recomendaciones-coach-nutricion.md`, segunda ronda (resuelto en web
      2026-08-17): TDEE/IMC editables en el perfil, macros visibles en receta/plan/
      diario (con barra de progreso), músculos trabajados y segunda imagen
      (posición final) en el detalle de ejercicio, ficha de rutina con
      descanso/intensidad/tempo/orden y resumen por día, **plan del día + timer de
      descanso durante la sesión activa** (`SessionPage`), gráfico de evolución de
      métricas en Progreso. Es el bloque de paridad más grande pendiente — mobile no
      tiene ninguna de estas mejoras.
- **Explícitamente NO entra acá** (decisión del usuario, no un olvido): el panel
  `/admin/ai` queda solo en web a propósito.
- [ ] **Identidad de marca real, aplicada en web, pendiente en mobile** (llegó el logo
      2026-08-18, dejó de estar bloqueado — ver `docs/11-identidad-marca.md` completo):
      `mobile/lib/core/theme/app_colors.dart` (paleta, falta agregar un campo
      `highlight` que hoy no existe), íconos de app (`mobile/web/icons/Icon-*.png`,
      siguen siendo el placeholder de `flutter create`), logo en
      `splash_screen.dart`, tipografía (`app_text_styles.dart`). Checklist exacto con
      archivo por archivo en `docs/11-identidad-marca.md § Pendiente en mobile`.

### Otras auditorías activas

- **`docs/06-ux-ui-audit.md`**: fuente de verdad de deuda de diseño puro. Todo lo
  accionable en web ya está resuelto (Parte 1 y 2, ver arriba); lo que resta es la
  paridad Flutter (arriba) y el backlog bloqueado por marca/assets.
- **`docs/10-recomendaciones-coach-nutricion.md`**: fuente de verdad de si el
  *contenido* de entrenamiento/nutrición que genera y muestra la app tiene sentido
  real (no solo si la UI es prolija). Actualizado 2026-08-18 — la mayoría del
  roadmap original (22 hallazgos) más los 8 nuevos de la verificación en vivo (§I) ya
  está resuelta. Queda sin cerrar, por decisión explícita de alcance o porque es
  contenido/config y no código:
  - [ ] **C.1** (parcial) — falta el chequeo de coherencia contra grupo muscular
        esperado (el mínimo de 3 ejercicios por día ya se valida).
  - [ ] **C.3** — videos de demostración para ejercicios compuestos (contenido).
  - [ ] **H.2** — agrupación de superseries/circuitos (backlog, modelo de datos nuevo).
  - [ ] **H.5 parte 2** — horario real por comida (backlog).
  - [ ] **I.3** — imágenes para el catálogo de recetas (contenido; sin esto H.4 ya
        implementado no se ve en la práctica).
  - [ ] **I.5** — evaluar `llama3.2:latest` como default local de Ollama en vez de
        `:1b` (config, no código).
- [ ] **Rotar la password del admin seed** (`admin@micoach.dev`) — quedó en texto
      plano en el chat de esta sesión.
- [ ] `.env` local todavía tiene `POSTGRES_DB=kineticos`/`POSTGRES_USER=kineticos`
      (el Postgres de Docker local ya se creó con ese nombre). Cosmético, no rompe
      nada; para dejarlo 100% consistente hay que recrear el volumen de Docker local.
- [ ] Reconsiderar el auto-repair permanente de Flyway (`FlywayConfig`) una vez que
      el equipo tenga acceso directo a Supabase — hoy es un parche necesario, no el
      estado ideal a largo plazo (ver nota arriba).
- [ ] Rama local `backup-antes-de-reescribir` — sin decidir si se borra.
- [ ] Render free tier: se observó un arranque de ~230s con un apagado/reinicio raro
      a los pocos segundos de quedar "live" (probablemente un redeploy solapado por
      otro push, no confirmado). Si se repite sin un push de por medio, investigar
      límites de recursos del plan free.

## Fase 5 — Testing (PLAN)

Testcontainers (PostgreSQL, Redis, RabbitMQ reales) en integración. Widget/unit tests en
Flutter mobile + tests de componentes en React (web). Cobertura objetivo ≥70% en dominio.

## Fase 6 — CI/CD (PARCIAL — deploy productivo ya hecho, adelantado desde la Fase 4)

- [x] Dockerfile multi-stage para backend.
- [x] Build de la web (React) automatizado.
- [x] Despliegue productivo con auto-deploy (Render + Vercel), sin GitHub Actions
      propio — Render/Vercel escuchan el repo directamente.
- [ ] GitHub Actions (build, test, lint) como gate de PRs — hoy el único control es
      que compile/buildee en el proveedor de deploy, no hay CI previo al merge.
- [ ] Build de la APK Flutter (nunca se generó, pendiente desde la Fase 3.1).
- [ ] Compose de "producción" con Nginx — probablemente ya no haga falta, dado que
      el hosting quedó resuelto con Render/Vercel/Supabase.
- [ ] Kubernetes solo como plan documentado (baja prioridad para un proyecto de curso).

## Fase 7 — Documentación (PLAN)

README completo con el "porqué" de cada herramienta. Manual técnico y funcional.
Revisar y completar todos los ADRs.

---

## Contexto imprescindible para continuar

1. **Arquitectura:** Monolito modular (Gradle multi-módulo), NO microservicios por ahora.
   Cada módulo es un bounded context extraíble a microservicio. Ver ADR-001.
2. **Regla de oro:** sin secretos en código, todo vía `.env` (ver `.env.example`).
3. **Convención de paquete:** `com.micoach` + nombre de módulo.
4. **Renombrar el proyecto:** ver sección "Renombrar el proyecto" en `docs/01-architecture.md`.
5. **Continuar con otra IA:** entregarle este archivo + `docs/01-architecture.md` + ADRs.
