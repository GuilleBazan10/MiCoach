# ADR-002 — Stack tecnológico completo

- **Fecha:** 2026-08-11
- **Estado:** Aceptada

## Contexto

Se necesita un stack 100% open-source / tier gratuito, de la base de datos al despliegue,
adecuado para un proyecto de curso que debe escalar. Cada herramienta elegida debe tener
un "porqué" defendible.

## Decisión

| Área | Herramienta | Por qué (resumen) |
|---|---|---|
| Lenguaje | Java 21 | LTS, ecosistema maduro, exigido por Spring Boot 3 |
| Framework | Spring Boot 3 | Estándar de facto, integraciones listas (Security, Data, Cloud) |
| Build | Gradle multi-módulo + version catalog | Soporte nativo de módulos → monolito modular |
| BD principal | PostgreSQL 16 | Open-source, ACID, JSONB, maduro |
| BD secundaria | (MongoDB descartado por ahora) | Añade valor solo con alta cardinalidad/analytics; se evita la complejidad extra sin necesidad real. Revisar en cada fase. |
| Caché/sesiones | Redis 7 | Velocidad, rate limiting, pub/sub |
| Eventos | RabbitMQ 3.13 | AMQP maduro, UI incluida, suficiente para eventos de dominio |
| Objetos | MinIO | S3-compatible open-source (videos, imágenes, GIF) |
| Búsqueda | OpenSearch | Fork Apache-2.0 de Elasticsearch (este cambió a licencia no-OS) |
| Tracing | Jaeger (solo él) | Zipkin es redundante; Jaeger es CNCF y cubre OTLP |
| Métricas | Prometheus + Grafana | Par estándar open-source |
| Auth | Propia (Spring Security + JWT + OAuth2 client) | Curso: demuestra el conocimiento; Keycloak añade un servidor más sin necesidad |
| IA | LangChain4j + Ollama | LangChain4j = mejor soporte de múltiples proveedores en Java; Ollama = modelos locales gratis |
| Frontend mobile | Flutter (Riverpod/GoRouter/Dio), mobile-only | Un solo código para Android/iOS, tematizable. Ya NO cubre web — ver ADR-003 |
| Frontend web | React + TypeScript + Vite (TanStack Query, React Router, Tailwind/shadcn) | DOM real (testeable/accesible), ecosistema maduro. Detalle y motivo completo en ADR-003 |
| Push | Firebase Messaging | Tier gratuito estándar para móvil |
| Infra | Docker Compose (+ K8s documentado) | Local sin fricción; K8s se documenta, no se implementa aún (justificación: complejidad sin necesidad) |
| CI/CD | GitHub Actions | Gratuito, nativo GitHub |
| Proxy | Nginx | Estándar, open-source |
| Testing | JUnit 5, Mockito, Testcontainers | Suite real con contenedores para integración |

**Alternativas evaluadas y descartadas:**
- *Elasticsearch* → licencia Elastic (no OS). Se usa OpenSearch.
- *Kafka* → potente pero sobre-dimensionado; RabbitMQ basta. Migrar si crece el volumen.
- *Keycloak* → excelente, pero para el curso conviene implementar auth propia.
- *Spring AI vs LangChain4j* → LangChain4j por madurez de "provider-agnostic" y prompts.

## Consecuencias

- Stack coherente y justificable académicamente; todo gratuito en desarrollo.
- Carga de infra en local: core ~2 GB RAM, opcional +~4 GB (OpenSearch/Grafana/Jaeger).
- Decisiones de "cuándo añadir" (Kafka, MongoDB, K8s) quedan documentadas para el futuro.
