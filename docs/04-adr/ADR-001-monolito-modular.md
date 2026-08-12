# ADR-001 — Monolito Modular (no microservicios)

- **Fecha:** 2026-08-11
- **Estado:** Aceptada

## Contexto

El proyecto requiere escalabilidad futura (paso a microservicios) pero es hoy un proyecto
de curso, no comercial, que debe ejecutarse en un portátil normal y demostrar buenas
prácticas. Un diseño inicial de 12 microservicios dispararía la complejidad operativa
(orquestación, red, descubrimiento, trazabilidad) sin aportar valor real en esta etapa.

## Decisión

Construir un **monolito modular**: un único artefacto Spring Boot (módulo `app`) que
compone módulos Gradle fuertemente aislados por **bounded context (DDD)**.

- Cada módulo (`auth`, `user`, `workout`, `nutrition`, `progress`, `notification`, `ai`,
  `admin`) es un paquete independiente con su propio `build.gradle`.
- Fronteras estrictas: un módulo no toca las tablas de otro; solo usa sus **puertos**.
- Comunicación de cambios relevantes por **eventos de dominio en RabbitMQ** (con Outbox)
  desde el día uno: esto fuerza contratos desacoplados y facilita la futura extracción.
- `shared` es el único módulo común.

**Alternativas evaluadas:**
- *Microservicios desde el inicio* → rechazado: sobrecoste operativo innecesario ahora;
  el monolito modular preserva el camino.
- *Mónolito sin fronteras* → rechazado: destruye la modularidad y el aprendizaje de DDD.

## Consecuencias

**Positivas:**
- Un proceso, un build, un despliegue. Arranca en un portátil sin problemas.
- Fronteras de dominio claras: el código ya "suena" a microservicios.
- Migración incremental: extraer un módulo = moverlo a repo propio y exponer sus eventos
  (ya publicados) + su API por el gateway.

**Negativas:**
- Un único punto de despliegue (aceptable ahora).
- El compilador no impide dependencias entre módulos: hay que respetarlas por convención
  (y refuerzo con reglas de arquitectura en tests, ver Fase 5).
- Cambios de versión de librerías compartidas afectan a todos los módulos a la vez.

**Plan de extracción (cuando haga falta):** `auth` primero (crítico), después `ai`
(carga variable), después el resto según demanda.
