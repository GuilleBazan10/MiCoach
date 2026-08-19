# =====================================================================
# MiCoach — Atajos de comandos
# En Windows usa los scripts de ./scripts (init-dev.ps1) o el wrapper.
# =====================================================================
.PHONY: help up down full-up full-down logs ps backend mobile migrate ai-seed clean dev-up dev-down dev-status dev-logs dev-restart

help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

# -p kineticos: docker-compose.yml declara `name: micoach`, pero los datos de
# desarrollo reales de esta máquina viven bajo el proyecto "kineticos" (ver
# scripts/dev.sh). Sin -p, "docker compose up -d" crea un segundo stack vacío
# en paralelo en vez de reusar el que ya tiene datos.
up: ## Levanta la infraestructura core
	docker compose -p kineticos up -d

down: ## Detiene la infraestructura core
	docker compose -p kineticos down

dev-up: ## Levanta TODO el ambiente local (docker + backend + frontend)
	./scripts/dev.sh up

dev-down: ## Baja TODO el ambiente local
	./scripts/dev.sh down

dev-status: ## Muestra qué está corriendo del ambiente local
	./scripts/dev.sh status

dev-logs: ## Sigue los logs de backend+frontend en vivo
	./scripts/dev.sh logs

dev-restart: ## Reinicia todo el ambiente local
	./scripts/dev.sh restart

full-up: ## Levanta observabilidad + OpenSearch (opcional)
	docker compose -f docker-compose.full.yml up -d

full-down: ## Detiene observabilidad + OpenSearch
	docker compose -f docker-compose.full.yml down

logs: ## Logs de la infraestructura
	docker compose -p kineticos logs -f

ps: ## Estado de los contenedores
	docker compose -p kineticos ps

backend: ## Arranca el backend (Spring Boot)
	cd backend && ./gradlew bootRun

migrate: ## Aplica migraciones Flyway sin arrancar la app
	cd backend && ./gradlew flywayMigrate

mobile: ## Ejecuta la app Flutter (web)
	cd mobile && flutter run -d chrome

ai-seed: ## Descarga los modelos de Ollama
	./scripts/seed-ai.sh

clean: ## Limpia builds
	cd backend && ./gradlew clean
	cd mobile && flutter clean
