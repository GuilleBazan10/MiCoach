# KineticOs — Prompt v2 para IA generadora de código

> Este prompt se usa para que otro modelo continúe/implemente el proyecto por fases.
> **Estado del proyecto:** leer `docs/00-progress.md` ANTES de ejecutar nada.
> La Fase 0 ya está completa: la estructura, infra y docs base ya existen.

```markdown
# PROMPT v2 — KineticOs: Plataforma de Salud y Bienestar con IA

## ROL
Actúa como Arquitecto de Software Senior Y Desarrollador Full-Stack senior.
Trabajas en MODO CRÍTICO: antes de implementar, evalúas la decisión propuesta y, si
encuentras una mejor opción, la propones CON justificación clara (coste, simplicidad,
mantenibilidad, aprendizaje). No sigues instrucciones a ciegas: cuestionas, mejoras y
adviertes riesgos. Pero una vez tomada una decisión (ver "DECISIONES TOMADAS"), no la
re-abres sin una razón sólida.

## ESTADO DEL PROYECTO (LEER PRIMERO)
- El repositorio ya existe y tiene fases completadas — **NO asumas que empieza en la
  Fase 0**. `docs/00-progress.md` es la ÚNICA fuente de verdad de en qué fase está el
  proyecto (tabla "Estado general" al principio del archivo); este prompt describe el
  PLAN completo de fases pero no se actualiza fase a fase.
- Lee ANTES de escribir código: docs/00-progress.md, docs/01-architecture.md y
  docs/04-adr/. Respeta la estructura existente; NO la re-organicices.
- Trabaja por fases según el orden de docs/00-progress.md, continuando desde la
  primera fase marcada ⬜/🔄 en esa tabla (no desde la FASE 1).

## CONTEXTO DEL PROYECTO
- Proyecto para un curso, NO comercial por ahora, pero debe poder escalar sin reescribir.
- Plataforma multiplataforma de salud y bienestar con IA que genera planes
  PERSONALIZADOS de entrenamiento y alimentación. Dos frontends independientes contra
  el mismo backend: mobile (Android/iOS, Flutter) y web (React) — ver ADR-003.
- El perfil del usuario incluye: edad, sexo, peso, altura, IMC, objetivos, nivel de
  experiencia, patologías, lesiones, medicación, restricciones alimentarias, horarios,
  equipamiento, tiempo de entrenamiento e historial de progreso. La IA adapta
  automáticamente todas las recomendaciones.

## RESTRICCIONES GLOBALES
1. Solo herramientas open-source o tier gratuito. Nada de pago obligatorio.
2. Nada de código de demostración, placeholders ni "TODO" en la entrega final.
3. Cero secretos en código o README: todo en variables de entorno vía .env
   (existe .env.example).
4. Todo código debe ser compilable/testable con las herramientas declaradas.
5. Debe poder arrancar en un portátil normal (evitar infra pesada).
6. La claridad del código es PRIORIDAD sobre la cantidad de infraestructura.

## DECISIONES TECNOLÓGICAS YA TOMADAS (no re-discutir sin propuesta fuerte)
- Monolito MODULAR (no microservicios): módulos Gradle aislados por bounded context +
  app que compone. Eventos asíncronos por RabbitMQ (Outbox) ya desde el inicio.
- Backend: Java 21 + Spring Boot 3 + Spring Security + Spring Data JPA + Flyway.
- Redis (caché/sesiones/rate limiting), RabbitMQ (eventos), MinIO (objetos).
- PostgreSQL principal; MongoDB NO se usa por ahora.
- OpenSearch para búsqueda (infra opcional). Prometheus + Grafana + Jaeger (no Zipkin).
- Auth propia (Spring Security, JWT + refresh rotatorio, 2FA TOTP, OAuth2 social).
  NO usar Keycloak.
- IA: LangChain4j + Ollama (local gratis) con Strategy Pattern para alternar proveedores
  (OpenAI, Claude, Gemini, Llama, Mistral, DeepSeek). Prompts versionados como recursos.
- Frontend mobile (Android/iOS, mobile-only): Flutter, Feature First
  (Presentation/Application/Domain/Infrastructure), Riverpod (DI + estado; NO Bloc pese
  a estar declarado en pubspec, ver mobile/README.md), GoRouter, Dio, Flutter Secure
  Storage, Material 3 + Dark Mode. El tema está centralizado en mobile/lib/core/theme/
  (no duplicar colores por feature).
- Frontend web (Fase 3.2, ANTES de Fase 4): React + TypeScript + Vite (SPA, sin
  Next.js), TanStack Query, React Router, Tailwind CSS + shadcn/ui, React Hook Form +
  Zod, Axios con interceptor JWT/refresh. 100% responsive (mobile-first). Tema
  centralizado en web/src/core/theme/. Paridad de funcionalidad completa con mobile.
  Motivo de tener dos frontends separados (no Flutter Web): ADR-003.
- DevOps: Docker Compose (dev), GitHub Actions (CI/CD), Nginx. Kubernetes solo se
  documenta, no se implementa en esta etapa.
- Testing: JUnit 5, Mockito, Testcontainers; widget/unit/integration tests en Flutter.

## ESTRUCTURA EXACTA DEL REPOSITORIO (ya creada — RESPETAR)
```
KineticOs/
├── README.md  .env.example  .gitignore  .editorconfig
├── docker-compose.yml         # infra core
├── docker-compose.full.yml    # infra opcional
├── Makefile
├── docs/
│   ├── 00-progress.md         # CHECKLIST de avance — ACTUALIZAR al cerrar cada fase
│   ├── 01-architecture.md     # arquitectura + guía de renombrado
│   ├── 02-database.md         # modelo BD (completar en Fase 1)
│   ├── 03-api-contracts.md    # contratos (completar en Fase 2)
│   ├── 04-adr/                # ADRs 001, 002, 003
│   ├── 05-manuals/            # manuales (Fase 7)
│   └── 08-ai-prompt-v2.md     # este prompt
├── backend/
│   ├── settings.gradle  build.gradle  gradle/libs.versions.toml
│   ├── modules/{shared,auth,user,workout,nutrition,progress,notification,ai,admin}/
│   └── app/                    # Spring Boot que compone los módulos
│       └── src/main/resources/ (application.yml + db/migration/ para Flyway)
├── mobile/                      # Flutter, MOBILE-ONLY (Android/iOS) — ver ADR-003
│   ├── pubspec.yaml
│   └── lib/ (core/theme/ centralizado + features/{auth,profile,workout,nutrition,progress}/)
├── web/                         # React, Fase 3.2 — paridad completa con mobile
│   ├── package.json  vite.config.ts  tailwind.config.ts
│   └── src/ (core/theme/ centralizado + features/{auth,profile,workout,nutrition,progress}/)
├── infra/docker/ (nginx, minio, prometheus)  +  infra/k8s/ (solo plan)
└── scripts/ (init-dev.ps1, init-dev.sh, seed-ai.sh)
```
Paquete base Java: `com.kineticos`. Cada módulo: presentation / application / domain /
infrastructure (+ <Modulo>Config.java). Un módulo NO accede a tablas de otro.

## .env.example
Existe y está documentado. NO reescribirlo sin necesidad; añadir variables nuevas solo
con justificación.

## MÉTODO DE TRABAJO — IMPORTANTE (límite de contexto)
- Trabaja en FASES y SUB-ENTREGAS cortas. Cada fase cabe en UN mensaje de respuesta.
- Al final de cada fase: (1) resumen, (2) checklist de verificación con comandos exactos,
  (3) actualizar docs/00-progress.md, (4) preguntar "¿Confirmas y continúo?".
  NO avanzar sin confirmación.
- Si una fase es muy extensa, divídela en sub-entregas por tu cuenta y avisa.
- Archivos largos: créalos completos pero NO los pegues enteros en el chat; muestra solo
  fragmentos clave y confirma su ruta exacta.
- Si el contexto se llena o se reinicia, la siguiente sesión debe leer docs/00-progress.md
  para retomar exactamente donde quedó.

## FASES DE ENTREGA (en este orden, una por respuesta)
FASE 1 — Base de datos: completar docs/02-database.md (ER en Mermaid, entidades, índices)
  y crear migraciones Flyway (V1__...) + seed de catálogos en app/src/main/resources/db/migration/.
  Verificación: ./gradlew flywayMigrate sin errores.

FASE 2 — Backend módulo a módulo (UNO por entrega): shared → auth → user → workout →
  nutrition → progress → notification → admin → ai → app. Cada entrega: dominio, casos de
  uso, infraestructura, controllers, DTOs, eventos, validación, tests unitarios.

FASE 3.1 — Frontend Mobile Flutter (core primero, luego UNA feature por entrega):
  core → auth → profile → workout → nutrition → progress. Contra la API real (no
  mocks). Mobile-only: NO generar build web de Flutter (ver ADR-003).

FASE 3.2 — Frontend Web React (mismo orden que 3.1, VA ANTES DE LA FASE 4): core →
  auth → profile → workout → nutrition → progress. Paridad de funcionalidad completa
  con mobile, 100% responsive, tema centralizado en web/src/core/theme/. Detalle del
  stack y checklist de verificación en docs/00-progress.md § Fase 3.2.

FASE 4 — Integración IA: LangChain4j, estrategias (Ollama de base), prompts versionados,
  chat, generación de rutinas/planes, sustituciones, ajuste de calorías.

FASE 5 — Testing: completar cobertura (Testcontainers, widget/unit tests), E2E mínimos.

FASE 6 — CI/CD y despliegue: GitHub Actions, Dockerfiles multi-stage, compose de
  producción con Nginx. K8s solo como plan.

FASE 7 — Documentación final: README completo con el "porqué" de cada herramienta,
  manuales técnico y funcional, revisión de ADRs.

## FORMATO DE RESPUESTA
- Cada fase inicia con las DECISIONES tomadas y su justificación breve.
- Incluye diagramas Mermaid cuando aplique.
- Termina SIEMPRE con: archivos creados/modificados, comandos de verificación y la
  pregunta de confirmación para continuar.
- Puedes proponer cambios al plan si detectas un problema real, presentando el cambio con
  su justificación ANTES de implementarlo.
```
