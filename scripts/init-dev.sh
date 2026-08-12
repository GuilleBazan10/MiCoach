#!/usr/bin/env bash
# =====================================================================
# KineticOs — Arranque inicial en Linux/macOS
# Uso:   bash scripts/init-dev.sh
# =====================================================================
set -e

echo "== KineticOs - Inicializacion =="

# 1. .env
if [ ! -f .env ]; then
    cp .env.example .env
    echo "[OK] .env creado a partir de .env.example. REVISA y completa los secretos."
else
    echo "[OK] .env ya existe."
fi

# 2. Docker disponible?
if ! command -v docker >/dev/null 2>&1; then
    echo "[ERROR] Docker no encontrado."
    exit 1
fi

# 3. JDK 17+ (Spring Boot 3 lo exige para correr Gradle)
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "[OK] JAVA_HOME=$JAVA_HOME ($("$JAVA_HOME/bin/java" -version 2>&1 | head -1))"
else
    echo "[AVISO] JAVA_HOME no apunta a un JDK valido."
fi
echo "        Spring Boot 3 requiere JDK 17+ para ejecutar Gradle. Instala JDK 21"
echo "        (Adoptium) si es necesario."

# 4. Levantar infraestructura core
echo "[..] Levantando infraestructura (postgres, redis, rabbitmq, minio, ollama)..."
docker compose up -d
echo "[OK] Infraestructura levantada."

echo ""
echo "Siguientes pasos (manuales):"
echo "  JDK 21   : si tu JAVA_HOME es < 17, instala Adoptium JDK 21 y exporta JAVA_HOME"
echo "  Backend : cd backend && ./gradlew bootRun"
echo "  Flutter : cd mobile && flutter create . --org com.kineticos --project-name kineticos_mobile"
echo "            flutter run"
echo ""
echo "Servicios disponibles:"
echo "  PostgreSQL : localhost:5432    MinIO UI : http://localhost:9001"
echo "  RabbitMQ UI: http://localhost:15672 (guest/guest)"
echo "  Ollama     : http://localhost:11434"
