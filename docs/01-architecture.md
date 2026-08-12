# KineticOs — Arquitectura

> Documento vivo. Se actualiza cuando cambian decisiones importantes.
> Las decisiones individuales se registran en `docs/04-adr/`.

## 1. Resumen

KineticOs es una plataforma de salud y bienestar con IA. Arquitectura elegida:
**Monolito Modular** con **Clean Architecture / Hexagonal** por módulo, comunicación
asíncrona con **eventos** y preparada para extraer microservicios en el futuro sin
reescribir código.

```mermaid
flowchart TB
    subgraph Clients["Clientes"]
        M[Mobile App Flutter<br/>Android · iOS]
        W[Web Flutter]
    end

    subgraph Backend["Backend — Monolito Modular (Java 21 + Spring Boot 3)"]
        GW[app — Composición y arranque<br/>Security · Gateway HTTP]
        subgraph Modules["Módulos (bounded contexts)"]
            AUTH[auth]
            USER[user]
            WK[workout]
            NUT[nutrition]
            PROG[progress]
            NOTIF[notification]
            AI[ai]
            ADMIN[admin]
            SHARED[shared]
        end
        BUS[Event Bus — RabbitMQ]
    end

    subgraph Infra["Infraestructura"]
        PG[(PostgreSQL)]
        RD[(Redis)]
        MINIO[(MinIO)]
        OS[(OpenSearch)]
        MON[(Prometheus · Grafana · Jaeger)]
    end

    M --> GW
    W --> GW
    GW --> Modules
    Modules <--> BUS
    Modules --> PG
    Modules --> RD
    MOD_API{{LangChain4j}} --> AI
    AI --> OLLAMA[Ollama / Proveedores cloud]
    NUT --> MINIO
    WK --> MINIO
    Modules --> OS
    GW -. métricas .-> MON
```

## 2. Decisión clave: Monolito Modular (no microservicios)

**Por qué:** proyecto de curso, no comercial aún, pero con ambición de crecer.

- **Ventaja operativa:** corre en un portátil normal. Un solo proceso, un solo despliegue.
- **Ventaja académica:** demuestra DDD, Clean Architecture, módulos, eventos y buenas
  prácticas SIN el coste de 12 microservicios (red, descubrimiento, orquestación).
- **Camino a microservicios:** cada módulo Gradle es un bounded context con sus propios
  puertos, eventos y tablas. Para extraerlo basta mover el módulo a un repo propio y
  exponer sus eventos por RabbitMQ (ya lo hacemos) + su API por el gateway.
- Ver **ADR-001** para el razonamiento completo.

## 3. Mapa de módulos (bounded contexts)

| Módulo | Responsabilidad | Datos propios |
|---|---|---|
| `shared` | Utilidades, DTOs base, eventos base, errores, seguridad base | — |
| `auth` | Identidad: registro, login, 2FA TOTP, refresh token, OAuth2 social | `users` (solo credenciales) |
| `user` | Perfil completo: patologías, lesiones, medicación, restricciones, preferencias | `user_profiles` y relacionadas |
| `workout` | Ejercicios, músculos, rutinas, generación de entrenamientos | `exercises`, `workouts` |
| `nutrition` | Recetas, planes, macros, sustituciones, lista de compra | `recipes`, `meal_plans` |
| `progress` | Peso, IMC, medidas, fotos, histórico, gráficos | `progress_entries` |
| `notification` | Recordatorios, push (FCM), preferencias de aviso | `notifications`, `reminders` |
| `ai` | Estrategias de IA, prompts versionados, chat, generación | config y caché de respuestas |
| `admin` | Gestión, roles, permisos, auditoría | `roles`, `audit_logs` |
| `app` | Composición de módulos, arranque Spring Boot, migraciones | — |

**Reglas de frontera:**
- Un módulo NO accede a las tablas de otro. Solo a través de sus puertos/API.
- Cambios de estado relevantes se publican como **eventos de dominio** en RabbitMQ.
- `shared` es el único módulo del que todos dependen.

## 4. Estilo por módulo (Clean / Hexagonal)

```
modules/<modulo>/
└── src/main/java/com/kineticos/<modulo>/
    ├── <Modulo>Config.java          # wiring (beans, listeners)
    ├── application/                 # casos de uso + puertos (interfaces)
    │   ├── service/                 # implementaciones de casos de uso
    │   └── port/in/ out/            # puertos de entrada/salida
    ├── domain/                      # entidades, value objects, eventos de dominio
    ├── infrastructure/              # adapters: repositorios JPA, clientes REST, bus
    │   ├── persistence/
    │   ├── messaging/
    │   └── client/
    └── presentation/                # controllers REST + DTOs de entrada/salida
```

Dependencias SOLO hacia adentro (domain no conoce a nadie). Casos de uso dependen de
puertos (interfaces), no de adapters → sustituibles (test, cloud, etc.).

## 5. Comunicación

- **Síncrona (consultas/comandos):** REST/OpenAPI desde `app` (gateway HTTP).
- **Asíncrona (eventos de dominio):** RabbitMQ con **Outbox Pattern** (publicación fiable)
  y retries. Ejemplo: `user.updated` → dispara re-generación de planes en `ai`.
- **Correlación:** header `X-Correlation-Id` propagado en toda la cadena para tracing.

```mermaid
sequenceDiagram
    participant C as Cliente (app)
    participant W as app (gateway)
    participant U as user
    participant AI as ai
    participant Q as RabbitMQ
    C->>W: PATCH /users/{id} (perfil)
    W->>U: actualiza perfil
    U->>Q: publica UserProfileUpdated (Outbox)
    Q->>AI: consume UserProfileUpdated
    AI->>AI: regenera plan con IA
```

## 6. Datos

- **PostgreSQL** = fuente de verdad (modelo relacional). Migraciones **Flyway**.
- **Redis** = caché, sesiones, rate limiting, colas ligeras.
- **MinIO** = objetos (videos, imágenes, GIF) vía API S3.
- **OpenSearch** = búsqueda y analytics (infra opcional por ahora).
- **MongoDB** = NO se usa por ahora. Si surge necesidad real de documentos/analytics de
  alta cardinalidad, se añade justificándolo (ver ADR-002).

## 7. IA

- `ai` usa **LangChain4j** con **Strategy Pattern** (`AiProviderStrategy`): cada proveedor
  (Ollama, OpenAI, Claude, Gemini, Mistral, DeepSeek) implementa la misma interfaz.
- Prompts **versionados** como recursos (`/prompts/*.md`), testables.
- Ollama = opción local gratuita por defecto (`AI_PROVIDER=ollama`).

## 8. Seguridad

JWT corto + refresh token rotatorio, 2FA TOTP, OAuth2 (Google/Apple/Facebook), BCrypt,
AES para datos sensibles de salud, rate limiting (Redis), protección CSRF en web,
auditoría de operaciones críticas, logs sin datos personales (pseudonimización).

## 9. Frontend (Flutter)

Feature First: cada feature tiene `presentation/`, `application/`, `domain/`,
`infrastructure/`. Estado con **Bloc**, inyección con **Riverpod**, navegación con
**GoRouter** (guardas de auth y roles), red con **Dio**, storage seguro, Offline First,
Material 3 + Dark Mode, accesibilidad.

El **tema es centralizado** en `mobile/lib/core/theme/` para poder re-diseñar sin tocar
las features (ver abajo).

## 10. Renombrar el proyecto

El nombre actual es **KineticOs** (paquete `com.kineticos`). Para cambiarlo:

**Backend (Gradle/Java):**
1. `backend/settings.gradle` → `rootProject.name`.
2. Paquetes `com.kineticos.*` → renombrar con IDE (refactor) en `backend/app` y
   `backend/modules/*` (todavía no hay código, es rápido).
3. `backend/app/src/main/resources/application.yml` → `spring.application.name`.

**Frontend (Flutter):**
1. `mobile/pubspec.yaml` → `name:` y `description:`.
2. `mobile/lib/` → paquete import (ej: `package:kineticos_mobile/...`).
3. Tras `flutter create . --org com.kineticos --project-name <nuevo>`, los archivos
   android/ios/web se generan con el nuevo nombre.

**Infraestructura:**
1. `docker-compose.yml` y `docker-compose.full.yml` → `name:` y nombres de servicio.
2. `.env.example` → variables `*_DB`, `TOTP_ISSUER`, `MAIL_FROM`.

**Docs:** buscar `KineticOs` / `kineticos` en `docs/` y `README.md`.

> Si el nuevo nombre cambia también el paquete Java (ej: `com.miempresa.app`), actualiza
> también `--org` en el comando `flutter create` y el `groupId` de Gradle.

## 11. Proyectos de documentación relacionados

- `docs/02-database.md` — modelo de datos (Fase 1).
- `docs/03-api-contracts.md` — contratos REST/eventos (Fase 2).
- `docs/04-adr/` — decisiones de arquitectura.
