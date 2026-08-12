#!/usr/bin/env bash
# =====================================================================
# KineticOs — Descarga los modelos de IA locales (Ollama)
# El modelo por defecto es llama3.2. Cámbialo en .env (OLLAMA_MODEL).
# =====================================================================
set -e

source_model="${OLLAMA_MODEL:-llama3.2}"
base_url="${OLLAMA_BASE_URL:-http://localhost:11434}"

if ! command -v curl >/dev/null 2>&1; then
    echo "[ERROR] curl no encontrado."
    exit 1
fi

echo "== Descargando modelo: $source_model =="
curl -s "$base_url/api/pull" -d "{\"name\": \"$source_model\"}" | tail -5

echo "[OK] Modelo listo. Verifica con: curl $base_url/api/tags"
