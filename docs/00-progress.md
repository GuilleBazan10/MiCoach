# KineticOs — Registro de progreso

> **LEER PRIMERO.** Este archivo es el "termómetro" del proyecto. Cualquier IA (o persona)
> que continúe el desarrollo debe empezar leyendo este documento para saber exactamente qué
> está hecho, qué falta y dónde encontrar el contexto. **Actualízalo al cerrar cada fase.**

## Estado general

| Fase | Descripción | Estado |
|---|---|---|
| 0 | Cimientos (estructura, infra, docs base) | ✅ COMPLETADA |
| 1 | Modelo de base de datos + migraciones Flyway | ✅ COMPLETADA |
| 2 | Backend módulo a módulo (Java/Spring) | 🔄 EN CURSO (shared ✅, auth ✅, user ✅, workout ✅, restantes pendientes) |
| 3 | Frontend Flutter | 🔄 EN CURSO (core ✅, auth ✅, profile ✅, workout ✅; nutrition/progress pendientes — sin módulo backend aún) |
| 4 | Integración IA (LangChain4j + Ollama) | ⬜ Pendiente |
| 5 | Testing completo | ⬜ Pendiente |
| 6 | CI/CD y despliegue | ⬜ Pendiente |
| 7 | Documentación final (README, manuales) | ⬜ Pendiente |

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
  `backend/app/build/libs/kineticos.jar` (~91 MB). Gradle descargó el JDK 21
  automáticamente (toolchain + plugin foojay).
- Git inicializado (commit pendiente de hacer).

### Requisitos de entorno detectados en esta máquina
- ⚠️ **El daemon de Gradle necesita JVM 17+** (Spring Boot 3.3). En este equipo (Linux)
  el `java` activo de sdkman es JDK 8, pero hay JDK 21 instalado:
  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew ...` (o `sdk use java 21`).
  En Windows, el fix es `setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21..."`.
- **Gradle** no instalado → se usa el wrapper (funciona, ya descargó 8.10.2).
- **PostgreSQL** se levanta con `docker compose up -d postgres` (puerto 5432, user/db
  `kineticos`). Las credenciales de Flyway salen del `.env` de la raíz.
- **Flutter** no instalado. Antes de correr la app:
  `cd mobile && flutter create . --org com.kineticos --project-name kineticos_mobile`
  (genera las carpetas android/ios/web/).

### Acción recomendada antes de Fase 1
Ejecutar una vez (consulta `docs/01-architecture.md` sección "Renombrar el proyecto"
si el nombre KineticOs va a cambiar, y hazlo ANTES de la Fase 1 para no renombrar
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

### Siguiente entrega
- Módulo `nutrition` (ingredientes, recetas, planes de comida, sustituciones).
  Orden en `docs/00-progress.md` Fase 2: `shared → auth → user → workout → nutrition → ...`.

## Fase 3 — Frontend (EN CURSO)

`core` (tema, router, red, storage) → auth → profile → workout → nutrition → progress.
Una feature por entrega, consumiendo la API real (no mocks) contra el backend ya
implementado. `nutrition` y `progress` quedan pendientes hasta que existan sus módulos
backend (Fase 2).

### Entorno de desarrollo Flutter (2026-08-12)

- Flutter no estaba instalado en la máquina de desarrollo. Se instaló manualmente (sin
  sudo) descargando el SDK estable 3.44.9 y extrayéndolo en `~/development/flutter`
  (agregar `~/development/flutter/bin` al `PATH`). `flutter create . --org com.kineticos
  --project-name kineticos_mobile --platforms=web,linux,android` generó las carpetas de
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

### Siguiente entrega

- Verificación manual completa del flujo (register → login → perfil → crear rutina →
  sesión) en un navegador real.
- Features `nutrition` y `progress` cuando existan sus módulos backend.

## Fase 4 — IA (PLAN)

LangChain4j + Ollama de base. Strategy Pattern para alternar proveedores. Prompts
versionados en recursos del módulo `ai`. Generación de rutinas, planes, sustituciones,
ajuste de calorías.

## Fase 5 — Testing (PLAN)

Testcontainers (PostgreSQL, Redis, RabbitMQ reales) en integración. Widget/unit tests en
Flutter. Cobertura objetivo ≥70% en dominio.

## Fase 6 — CI/CD (PLAN)

GitHub Actions (build, test, lint). Dockerfile multi-stage para backend y Flutter web.
Compose de "producción" con Nginx. Kubernetes solo como plan documentado.

## Fase 7 — Documentación (PLAN)

README completo con el "porqué" de cada herramienta. Manual técnico y funcional.
Revisar y completar todos los ADRs.

---

## Contexto imprescindible para continuar

1. **Arquitectura:** Monolito modular (Gradle multi-módulo), NO microservicios por ahora.
   Cada módulo es un bounded context extraíble a microservicio. Ver ADR-001.
2. **Regla de oro:** sin secretos en código, todo vía `.env` (ver `.env.example`).
3. **Convención de paquete:** `com.kineticos` + nombre de módulo.
4. **Renombrar el proyecto:** ver sección "Renombrar el proyecto" en `docs/01-architecture.md`.
5. **Continuar con otra IA:** entregarle este archivo + `docs/01-architecture.md` + ADRs.
