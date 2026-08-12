# KineticOs

Plataforma de salud y bienestar impulsada por IA: planes personalizados de entrenamiento
y alimentación, adaptados al perfil completo de cada usuario.

> **Estado actual — FASE 3 (frontend, en curso).** Backend: `shared`, `auth`, `user`,
> `workout` implementados. Frontend: `core`, `auth`, `profile`, `workout` implementados
> y corriendo contra la API real. Este README se ampliará en la Fase 7 con el manual
> completo y la justificación detallada de cada herramienta. Para el estado detallado,
> lee `docs/00-progress.md`; para el contexto técnico, `docs/01-architecture.md` y
> `docs/04-adr/`.

---

## ¿Qué es esto?

Una aplicación multiplataforma (Android, iOS, Web) que genera rutinas de entrenamiento y
planes de alimentación **personalizados con IA**. El perfil de cada usuario considera edad,
sexo, peso, altura, IMC, objetivos, nivel, patologías, lesiones, medicación, restricciones
alimentarias, horarios, equipamiento, tiempo disponible e historial de progreso. La IA
adapta automáticamente todas las recomendaciones.

## Stack en una frase

- **Backend:** Java 21 + Spring Boot 3, **monolito modular** (Gradle multi-módulo),
  PostgreSQL, Redis, RabbitMQ, MinIO, OpenSearch, Prometheus/Grafana/Jaeger.
- **Frontend:** Flutter (Bloc + Riverpod, GoRouter, Freezed, Dio, Offline First,
  Material 3, Dark Mode).
- **IA:** LangChain4j con **Strategy Pattern** para alternar proveedores
  (Ollama local gratis, OpenAI, Claude, Gemini, Mistral, DeepSeek).
- **DevOps:** Docker Compose, GitHub Actions, Nginx.

## Estructura del repositorio

```
KineticOs/
├── backend/    → Java 21 + Spring Boot (módulos por dominio + app que compone)
├── mobile/     → Flutter (Feature First, core tematizable)
├── infra/      → nginx, minio, plan k8s
├── docs/       → arquitectura, base de datos, contratos, ADRs, manuales, PROGRESO
├── scripts/    → arranque local (Windows/Linux/Mac) y seed de modelos IA
├── docker-compose.yml        → infra core (postgres, redis, rabbitmq, minio)
├── docker-compose.full.yml   → infra opcional (opensearch, prometheus, grafana, jaeger)
├── .env.example              → plantilla de variables de entorno
└── Makefile                  → atajos de comandos
```

## Arranque rápido (desarrollo)

Requisitos: **Docker**, **JDK 21**, **Gradle** (se usa el wrapper), **Flutter SDK**.

```bash
# 1. Copiar variables de entorno
cp .env.example .env        # Windows: copy .env.example .env

# 2. Levantar la infraestructura core (base de datos, redis, etc.)
docker compose up -d        # Windows: ejecutar como administrador si es necesario

# 3. Backend (descarga el JDK 21 automáticamente vía Gradle toolchain)
cd backend
./gradlew bootRun           # Windows: .\gradlew.bat bootRun

# 4. Flutter (las carpetas de plataforma ya están generadas)
cd ../mobile
flutter pub get
flutter run -d web-server --web-port=5050   # abrir http://localhost:5050
```

> Detalle completo (requisitos, CORS, cómo probar el flujo) en `mobile/README.md`.

> En Windows puedes usar `scripts\init-dev.ps1` que automatiza los pasos 1 y 2.

## Personalización del nombre y diseño

El nombre **KineticOs** y el diseño son **fáciles de cambiar**:

- **Diseño (tema):** todo está centralizado en
  `mobile/lib/core/theme/` (`app_theme.dart`, `app_colors.dart`, `app_text_styles.dart`).
  Cambia colores/tipografía en esos 3 archivos y toda la app se re-tematiza
  (claro/oscuro incluidos).
- **Renombrar el proyecto:** mira la guía en `docs/01-architecture.md` (sección
  "Renombrar el proyecto"), que lista TODOS los sitios donde aparece el nombre.

## ¿Cómo continuar el desarrollo con otra IA?

Si otro modelo va a seguir este proyecto, lo primero que debe leer es:

1. `docs/00-progress.md` — qué está hecho y qué sigue (checklist por fase).
2. `docs/01-architecture.md` — arquitectura y decisiones.
3. `docs/04-adr/` — registro de decisiones (por qué se eligió cada cosa).

> El prompt para IA generadora de código está versionado en `docs/08-ai-prompt-v2.md`.

## Documentación

| Documento | Contenido |
|---|---|
| `docs/00-progress.md` | Checklist de avance por fase |
| `docs/01-architecture.md` | Arquitectura completa, decisiones, diagramas |
| `docs/02-database.md` | Modelo de base de datos (Fase 1) |
| `docs/03-api-contracts.md` | Contratos REST y eventos (Fase 2) |
| `docs/04-adr/` | Architecture Decision Records |
| `docs/05-manuals/` | Manual técnico y funcional (Fase 7) |
| `docs/08-ai-prompt-v2.md` | Prompt v2 para IA generadora |

## Licencia

Proyecto educativo. Sin licencia comercial por ahora.
