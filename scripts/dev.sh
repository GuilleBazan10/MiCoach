#!/usr/bin/env bash
# =====================================================================
# MiCoach — Sube/baja rápido TODO el ambiente local: Docker (infra) +
# backend (Spring Boot) + frontend (Vite). Reemplaza la secuencia manual
# de "docker compose up -d" + "cd backend && ./gradlew bootRun" (en otra
# terminal) + "cd web && ./dev.sh" (en otra terminal más).
#
# Uso:
#   scripts/dev.sh up        # levanta todo (idempotente — si algo ya
#                             # está corriendo, lo deja como está)
#   scripts/dev.sh down      # baja todo (no borra datos de Docker)
#   scripts/dev.sh status    # qué está corriendo ahora
#   scripts/dev.sh logs [backend|frontend]   # sigue el log en vivo
#   scripts/dev.sh restart   # down + up
#
# NOTA IMPORTANTE sobre el nombre del proyecto de Docker: docker-compose.yml
# declara `name: micoach`, pero los datos de desarrollo reales de esta
# máquina (usuarios, rutinas, planes, config de Groq) viven bajo el
# proyecto "kineticos" (probablemente de antes de que se agregara ese
# `name:` — ver docs/01-architecture.md § 11 sobre el rename del
# proyecto). Este script fija el proyecto a "kineticos" a propósito, para
# no crear un segundo stack vacío en paralelo cada vez que se corre
# `docker compose up -d` sin -p. Si en algún momento se consolida todo
# bajo "micoach", cambiar COMPOSE_PROJECT acá abajo (y migrar los datos).
# =====================================================================
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

COMPOSE_PROJECT="kineticos"
LOG_DIR="$ROOT_DIR/.dev-logs"
BACKEND_PORT=8080
FRONTEND_PORT=5173
BACKEND_PATTERN="MiCoachApplication"
FRONTEND_PATTERN="$ROOT_DIR/web/node_modules/.bin/vite"
JAVA21_FALLBACKS=(
  "/usr/lib/jvm/java-21-openjdk-amd64"
  "/usr/lib/jvm/temurin-21-jdk-amd64"
)

mkdir -p "$LOG_DIR"

# --------------------------- helpers ---------------------------------

find_java21() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ] \
     && "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"21\.'; then
    echo "$JAVA_HOME"
    return
  fi
  for candidate in "${JAVA21_FALLBACKS[@]}"; do
    if [ -x "$candidate/bin/java" ]; then
      echo "$candidate"
      return
    fi
  done
  echo ""
}

wait_for_http() {
  local label="$1" url="$2" timeout="${3:-60}"
  local waited=0
  printf "   esperando %s" "$label"
  until curl -sf -o /dev/null "$url" 2>/dev/null; do
    sleep 2
    waited=$((waited + 2))
    printf "."
    if [ "$waited" -ge "$timeout" ]; then
      echo " TIMEOUT (${timeout}s) — revisá $LOG_DIR/${label}.log"
      return 1
    fi
  done
  echo " listo (${waited}s)"
}

wait_for_postgres_healthy() {
  local timeout=60 waited=0
  # container_name está hardcodeado a "micoach-postgres" en docker-compose.yml
  # (no sigue el -p/COMPOSE_PROJECT) — no cambia aunque cambie COMPOSE_PROJECT.
  printf "   esperando postgres"
  while true; do
    local status
    status="$(docker inspect --format '{{.State.Health.Status}}' "micoach-postgres" 2>/dev/null || echo "starting")"
    if [ "$status" = "healthy" ]; then
      echo " listo (${waited}s)"
      return 0
    fi
    sleep 2
    waited=$((waited + 2))
    printf "."
    if [ "$waited" -ge "$timeout" ]; then
      echo " TIMEOUT — seguí igual, el backend reintenta la conexión solo"
      return 0
    fi
  done
}

is_pattern_running() {
  pgrep -f "$1" >/dev/null 2>&1
}

# --------------------------- comandos ---------------------------------

cmd_up() {
  echo "== Docker (proyecto '$COMPOSE_PROJECT') =="
  docker compose -p "$COMPOSE_PROJECT" up -d
  wait_for_postgres_healthy

  echo "== Backend =="
  if is_pattern_running "$BACKEND_PATTERN"; then
    echo "   ya está corriendo"
  else
    local java_home
    java_home="$(find_java21)"
    if [ -z "$java_home" ]; then
      echo "   [ERROR] no encontré un JDK 21. Exportá JAVA_HOME apuntando a uno e intentá de nuevo."
      exit 1
    fi
    echo "   arrancando (JAVA_HOME=$java_home)..."
    (cd "$ROOT_DIR/backend" && JAVA_HOME="$java_home" nohup ./gradlew bootRun \
      > "$LOG_DIR/backend.log" 2>&1 &)
    wait_for_http "backend" "http://localhost:$BACKEND_PORT/actuator/health" 180
  fi

  echo "== Frontend =="
  if is_pattern_running "$FRONTEND_PATTERN"; then
    echo "   ya está corriendo"
  else
    echo "   arrancando..."
    (cd "$ROOT_DIR/web" && nohup ./dev.sh > "$LOG_DIR/frontend.log" 2>&1 &)
    wait_for_http "frontend" "http://localhost:$FRONTEND_PORT" 30
  fi

  echo ""
  cmd_status
}

cmd_down() {
  echo "== Frontend =="
  if is_pattern_running "$FRONTEND_PATTERN"; then
    pkill -f "$FRONTEND_PATTERN" 2>/dev/null || true
    echo "   detenido"
  else
    echo "   no estaba corriendo"
  fi

  echo "== Backend =="
  if is_pattern_running "$BACKEND_PATTERN"; then
    pkill -f "$BACKEND_PATTERN" 2>/dev/null || true
    echo "   detenido"
  else
    echo "   no estaba corriendo"
  fi

  echo "== Docker (proyecto '$COMPOSE_PROJECT') =="
  docker compose -p "$COMPOSE_PROJECT" down
  echo ""
  echo "Todo abajo. Los datos de Docker (Postgres, etc.) quedaron intactos"
  echo "en sus volúmenes — 'up' los va a encontrar tal cual la próxima vez."
}

cmd_status() {
  echo "== Estado =="
  echo "-- Docker (proyecto '$COMPOSE_PROJECT') --"
  docker compose -p "$COMPOSE_PROJECT" ps --format "table {{.Name}}\t{{.Status}}" 2>/dev/null \
    || echo "   (sin contenedores)"

  echo "-- Backend --"
  if is_pattern_running "$BACKEND_PATTERN"; then
    if curl -sf -o /dev/null "http://localhost:$BACKEND_PORT/actuator/health" 2>/dev/null; then
      echo "   UP   http://localhost:$BACKEND_PORT"
    else
      echo "   arrancando... (http://localhost:$BACKEND_PORT todavía no responde)"
    fi
  else
    echo "   abajo"
  fi

  echo "-- Frontend --"
  if is_pattern_running "$FRONTEND_PATTERN"; then
    echo "   UP   http://localhost:$FRONTEND_PORT"
  else
    echo "   abajo"
  fi
}

cmd_logs() {
  local target="${1:-}"
  case "$target" in
    backend) tail -f "$LOG_DIR/backend.log" ;;
    frontend) tail -f "$LOG_DIR/frontend.log" ;;
    *) tail -f "$LOG_DIR/backend.log" "$LOG_DIR/frontend.log" ;;
  esac
}

cmd_restart() {
  cmd_down
  echo ""
  cmd_up
}

# --------------------------- entrypoint -------------------------------

case "${1:-}" in
  up) cmd_up ;;
  down) cmd_down ;;
  status) cmd_status ;;
  logs) cmd_logs "${2:-}" ;;
  restart) cmd_restart ;;
  *)
    echo "Uso: $0 {up|down|status|logs [backend|frontend]|restart}"
    exit 1
    ;;
esac
