# MiCoach

Plataforma de salud y bienestar impulsada por IA: planes personalizados de entrenamiento
y alimentación, adaptados al perfil completo de cada usuario.

> **Estado actual — Fases 2, 3.1 y 3.2 completas.** Backend: los 9 módulos (`shared`,
> `auth`, `user`, `workout`, `nutrition`, `progress`, `notification`, `admin`, `ai`)
> implementados y verificados. Frontend mobile (Flutter, Android/iOS) y frontend web
> (React) tienen paridad completa: `core`, `auth`, `profile`, `workout`, `nutrition`,
> `progress` implementados y corriendo contra la API real en ambos (`admin`/`ai` no
> tienen pantalla propia en ninguno de los dos, es deliberado) — ver
> `docs/04-adr/ADR-003-frontend-web-react.md` para el porqué de tener dos frontends
> separados en vez de Flutter Web. **Sigue la Fase 4: integración real de IA.** Este
> README se ampliará en la Fase 7 con el manual completo y la justificación detallada
> de cada herramienta. Para el estado detallado, lee `docs/00-progress.md`; para el
> contexto técnico, `docs/01-architecture.md` y `docs/04-adr/`.

---

## ¿Qué es esto?

Una plataforma de salud y bienestar con dos frontends (mobile y web) que genera
rutinas de entrenamiento y planes de alimentación **personalizados con IA**. El perfil
de cada usuario considera edad, sexo, peso, altura, IMC, objetivos, nivel, patologías,
lesiones, medicación, restricciones alimentarias, horarios, equipamiento, tiempo
disponible e historial de progreso. La IA adapta automáticamente todas las
recomendaciones.

## Stack en una frase

- **Backend:** Java 21 + Spring Boot 3, **monolito modular** (Gradle multi-módulo),
  PostgreSQL, Redis, RabbitMQ, MinIO, OpenSearch, Prometheus/Grafana/Jaeger.
- **Frontend mobile** (Android/iOS): Flutter (Riverpod, GoRouter, Dio, Material 3,
  Dark Mode).
- **Frontend web:** React + TypeScript + Vite (TanStack Query, React Router, Tailwind
  CSS + shadcn/ui), 100% responsive, misma funcionalidad que mobile. Por qué dos
  frontends separados en vez de Flutter Web:
  `docs/04-adr/ADR-003-frontend-web-react.md`.
- **IA:** LangChain4j con **Strategy Pattern** para alternar proveedores
  (Ollama local gratis, OpenAI, Claude, Gemini, Mistral, DeepSeek).
- **DevOps:** Docker Compose, GitHub Actions, Nginx.

## Estructura del repositorio

```
MiCoach/
├── backend/    → Java 21 + Spring Boot (módulos por dominio + app que compone)
├── mobile/     → Flutter, mobile-only Android/iOS (Feature First, core tematizable)
├── web/        → React (misma funcionalidad que mobile, responsive)
├── infra/      → nginx, minio, plan k8s
├── docs/       → arquitectura, base de datos, contratos, ADRs, manuales, PROGRESO
├── scripts/    → arranque local (Windows/Linux/Mac) y seed de modelos IA
├── docker-compose.yml        → infra core (postgres, redis, rabbitmq, minio)
├── docker-compose.full.yml   → infra opcional (opensearch, prometheus, grafana, jaeger)
├── .env.example              → plantilla de variables de entorno
└── Makefile                  → atajos de comandos
```

## Arranque rápido (desarrollo)

Requisitos: **Docker**, **JDK 21**, **Gradle** (se usa el wrapper), **Flutter SDK**
(mobile), **Node.js ≥20.19 o ≥22.12** (web — Vite 8 no arranca con versiones menores).

```bash
# 1. Copiar variables de entorno
cp .env.example .env        # Windows: copy .env.example .env

# 2. Levantar la infraestructura core (base de datos, redis, etc.)
docker compose up -d        # Windows: ejecutar como administrador si es necesario

# 3. Backend (descarga el JDK 21 automáticamente vía Gradle toolchain)
cd backend
./gradlew bootRun           # Windows: .\gradlew.bat bootRun

# 4. App mobile (Flutter — las carpetas de plataforma ya están generadas)
cd ../mobile
flutter pub get
flutter run -d web-server --web-port=5050   # abrir http://localhost:5050 (para probar rápido; la app final es mobile-only, ver ADR-003)

# 5. App web (React)
cd ../web
npm install
npm run dev   # abre http://localhost:5173 — entrar por localhost, no 127.0.0.1 (ver web/README.md § CORS)
```

> Detalle completo del mobile (requisitos, CORS, cómo probar el flujo) en
> `mobile/README.md`. Detalle completo de la web (stack, estructura, CORS) en
> `web/README.md`.

> En Windows puedes usar `scripts\init-dev.ps1` que automatiza los pasos 1 y 2.

## Personalización del nombre y diseño

El nombre **MiCoach** y el diseño son **fáciles de cambiar** — en ambos frontends:

- **Diseño (tema) mobile:** centralizado en
  `mobile/lib/core/theme/` (`app_theme.dart`, `app_colors.dart`, `app_text_styles.dart`).
  Cambia colores/tipografía en esos 3 archivos y toda la app se re-tematiza
  (claro/oscuro incluidos).
- **Diseño (tema) web:** centralizado en `web/src/core/theme/` (tokens de Tailwind +
  variables CSS) — mismo criterio, un solo lugar para cambiar el look completo.
- **Renombrar el proyecto:** mira la guía en `docs/01-architecture.md` (sección
  "Renombrar el proyecto"), que lista TODOS los sitios donde aparece el nombre,
  incluida la web.

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
| `mobile/README.md` | Cómo levantar y probar la app mobile (Flutter) |
| `web/README.md` | Stack, estructura y arranque de la app web (ver ADR-003) |

## Licencia

Proyecto educativo. Sin licencia comercial por ahora.
