# =====================================================================
# MiCoach — Arranque inicial en Windows (PowerShell)
# Uso:   powershell -ExecutionPolicy Bypass -File scripts\init-dev.ps1
# =====================================================================
$ErrorActionPreference = "Stop"

Write-Host "== MiCoach - Inicializacion (Windows) ==" -ForegroundColor Cyan

# 1. .env
if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "[OK] .env creado a partir de .env.example. REVISA y completa los secretos." -ForegroundColor Yellow
} else {
    Write-Host "[OK] .env ya existe." -ForegroundColor Green
}

# 2. Docker disponible?
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Docker no encontrado. Instala Docker Desktop." -ForegroundColor Red
    exit 1
}

# 3. JDK 17+ (Spring Boot 3 lo exige para correr Gradle)
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $jvm = & "$env:JAVA_HOME\bin\java.exe" -version 2>&1 | Select-Object -First 1
    Write-Host "[OK] JAVA_HOME=$env:JAVA_HOME ($jvm)" -ForegroundColor Green
} else {
    Write-Host "[AVISO] JAVA_HOME no apunta a un JDK valido." -ForegroundColor Yellow
}
Write-Host "        Spring Boot 3 requiere JDK 17+ para ejecutar Gradle. Si no lo tienes," -ForegroundColor Yellow
Write-Host "        instala JDK 21 (Adoptium) y ejecuta: `$env:JAVA_HOME = 'ruta_al_jdk21'" -ForegroundColor Yellow

# 4. Levantar infraestructura core
Write-Host "[..] Levantando infraestructura (postgres, redis, rabbitmq, minio, ollama)..."
docker compose up -d
if ($LASTEXITCODE -ne 0) { Write-Host "[ERROR] Fallo al levantar Docker." -ForegroundColor Red; exit 1 }
Write-Host "[OK] Infraestructura levantada." -ForegroundColor Green

# 4. Backend: comprobar JDK 21 (Gradle lo descarga solo si usas toolchain)
Write-Host ""
Write-Host "Siguientes pasos (manuales):" -ForegroundColor Yellow
Write-Host "  JDK 21   : si tu JAVA_HOME es < 17, instala Adoptium JDK 21 y setea JAVA_HOME" -ForegroundColor Yellow
Write-Host "  Backend : cd backend ; .\gradlew.bat bootRun"
Write-Host "  Flutter : cd mobile  ; flutter create . --org com.micoach --project-name micoach_mobile"
Write-Host "            flutter run"
Write-Host ""
Write-Host "Servicios disponibles:" -ForegroundColor Cyan
Write-Host "  PostgreSQL : localhost:5432    MinIO UI : http://localhost:9001"
Write-Host "  RabbitMQ UI: http://localhost:15672 (guest/guest)"
Write-Host "  Ollama     : http://localhost:11434"
