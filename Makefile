# =====================================================================
# KineticOs — Atajos de comandos
# En Windows usa los scripts de ./scripts (init-dev.ps1) o el wrapper.
# =====================================================================
.PHONY: help up down full-up full-down logs ps backend mobile migrate ai-seed clean

help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2}'

up: ## Levanta la infraestructura core
	docker compose up -d

down: ## Detiene la infraestructura core
	docker compose down

full-up: ## Levanta observabilidad + OpenSearch (opcional)
	docker compose -f docker-compose.full.yml up -d

full-down: ## Detiene observabilidad + OpenSearch
	docker compose -f docker-compose.full.yml down

logs: ## Logs de la infraestructura
	docker compose logs -f

ps: ## Estado de los contenedores
	docker compose ps

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
