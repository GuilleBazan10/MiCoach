# Architecture Decision Records (ADR)

Registro de decisiones de arquitectura del proyecto. Cada ADR documenta el **contexto**,
la **decisión** y las **consecuencias**. Se numera secuencialmente.

## Índice

| ID | Decisión | Estado |
|---|---|---|
| [ADR-001](ADR-001-monolito-modular.md) | Monolito modular (no microservicios) | Aceptada |
| [ADR-002](ADR-002-stack-tecnologico.md) | Stack tecnológico completo | Aceptada |
| [ADR-003](ADR-003-frontend-web-react.md) | Frontend web separado en React (Flutter queda mobile-only) | Aceptada |

## Convención

Nuevo ADR: copiar la plantilla siguiente y numerar el siguiente ID libre (003, ...):

```markdown
# ADR-XXX — Título corto

- **Fecha:** YYYY-MM-DD
- **Estado:** Propuesta | Aceptada | Deprecada

## Contexto
(¿Qué problema resuelve? ¿Por qué importa ahora?)

## Decisión
(¿Qué se elige y por qué sobre las alternativas?)

## Consecuencias
(Positivas y negativas. Costes, riesgos, aprendizaje.)
```
