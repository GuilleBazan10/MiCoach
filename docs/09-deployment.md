# Deployment — Supabase + Render + Vercel (free tier)

> Guía operativa para poner MiCoach en producción con servicios gratuitos y
> auto-deploy en cada push a `main`. No requiere pipeline propio: Render y
> Vercel escuchan al repo de GitHub directamente.

## Arquitectura

```
GitHub (main) ──push──▶ Render (backend, Docker)  ──▶ Supabase (Postgres)
             └──push──▶ Vercel (web, estático)     ──▶ Render (API REST)
```

- **Supabase**: ya está la base de datos arriba (según lo que comentaste).
- **Render**: corre el backend Spring Boot en un contenedor Docker. Al bootear
  corre Flyway automáticamente contra Supabase (`spring.flyway.enabled=true`),
  así que las migraciones V1-V17 se aplican solas en el primer deploy.
- **Vercel**: sirve el build estático de `web/` (Vite + React).
- **Redis / RabbitMQ / MinIO / Ollama** (del `docker-compose.yml` local): hoy
  ningún flujo de código los usa realmente todavía (son starters declarados
  para fases futuras), así que **no hace falta contratar nada para ellos**.
  Ya deshabilité sus health indicators (`application.yml`) para que
  `/actuator/health` no reporte 503 por servicios que nadie llama.
- **Ollama no es viable en Render free** (necesita CPU/RAM sostenidos que el
  free tier no da). En producción tenés que activar Groq o Gemini desde el
  panel `/admin/ai` — ya soportás ambos.

## 0. Prerrequisito: subir lo pendiente

Tenés bastante trabajo sin commitear (Fase 4 completa: panel de IA, USDA,
recetas, sustitución de ingredientes, ajuste de calorías, rediseño web, fix
de tabs). Render y Vercel despliegan desde GitHub, así que antes de cualquier
paso de acá abajo necesitamos comitear y pushear eso. Si querés, lo armamos
en un PR aparte antes de seguir — avisame.

## 1. Supabase — obtener la connection string correcta

En **Project Settings → Database → Connection string**, usá el **Session
pooler** (no "Direct connection" ni "Transaction pooler"):

- Direct connection: en proyectos nuevos suele ser **solo IPv6**, y Render no
  siempre tiene salida IPv6 → falla la conexión.
- Transaction pooler (puerto 6543): no soporta prepared statements bien, y
  Hibernate/JPA los necesita → errores intermitentes.
- **Session pooler (puerto 5432)**: IPv4-compatible y sostiene conexiones
  largas como las de un pool de Hikari. Es la que hay que usar acá.

De esa cadena (`postgresql://postgres.xxxx:PASSWORD@aws-0-<region>.pooler.supabase.com:5432/postgres`)
sacás las variables que necesita el backend:

| Variable | Valor |
|---|---|
| `POSTGRES_HOST` | `aws-0-<region>.pooler.supabase.com` |
| `POSTGRES_PORT` | `5432` |
| `POSTGRES_DB` | `postgres` |
| `POSTGRES_USER` | `postgres.xxxx` (usuario completo, con el proyecto incluido) |
| `POSTGRES_PASSWORD` | la que pusiste al crear el proyecto |

Ya agregué soporte SSL al datasource (`POSTGRES_SSLMODE`, default `prefer`
para no romper el Postgres local sin SSL).

## 2. Render — backend

Ya dejé listo `backend/Dockerfile` y `render.yaml` en la raíz del repo.

1. [dashboard.render.com](https://dashboard.render.com) → **New +** →
   **Blueprint** → conectá el repo de GitHub (`GuilleBazan10/MiCoach`).
2. Render detecta `render.yaml` y arma el servicio `micoach-backend`
   (Docker, plan free, health check en `/actuator/health`, auto-deploy on).
3. Completá a mano las env vars marcadas como secretas (Render te las va a
   pedir al aplicar el blueprint):
   - `POSTGRES_HOST`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` (del paso 1)
   - `CORS_ALLOWED_ORIGINS` — dejalo en blanco por ahora, lo completamos en el paso 4 con la URL de Vercel.
   - `JWT_SECRET` y `AES_SECRET` los genera Render solo (`generateValue: true`).
4. Deploy. El primer boot va a tardar (build de Gradle + Flyway corriendo
   V1→V17 contra Supabase). Mirá los logs hasta ver `Started MiCoachApplication`.
5. Notá la URL que te da Render, algo como `https://micoach-backend.onrender.com`.

**Free tier**: el servicio se duerme a los 15 min sin tráfico y el próximo
request tarda ~30-50s en despertar (cold start). Para un proyecto de curso
está bien: si más adelante molesta, un cron externo pegándole a
`/actuator/health` cada 10 min lo mantiene despierto (o pagás el plan starter).

## 3. Vercel — web

1. [vercel.com/new](https://vercel.com/new) → importá el mismo repo.
2. Como es un monorepo, en **Root Directory** elegí `web`.
3. Framework preset: Vite (lo detecta solo). Build command `npm run build`,
   output `dist` (defaults, no hace falta tocarlos).
4. Environment variable:
   - `VITE_API_BASE_URL` = `https://micoach-backend.onrender.com/api/v1`
     (la URL de Render del paso 2, con `/api/v1` al final).
5. Deploy. Vercel te da una URL tipo `https://micoach-web.vercel.app`.

Ya agregué `web/vercel.json` con el rewrite a `index.html` que necesita el
router (usa `createBrowserRouter`, así que sin esto las rutas internas dan
404 al refrescar o entrar por link directo).

## 4. Conectar los dos extremos

En Render, editá `CORS_ALLOWED_ORIGINS` del backend y agregá la URL real de
Vercel:

```
CORS_ALLOWED_ORIGINS=https://micoach-web.vercel.app
```

Guardar dispara un redeploy automático del backend (variable cambiada).

## 5. Activar un proveedor de IA en producción

Ollama no corre en Render free. Entrá a `https://<tu-url-vercel>/admin/ai`
con un usuario `ROLE_ADMIN` y activá Groq (o Gemini) con tu API key — se
guarda cifrada en la tabla `ai_provider_configs`, no hace falta redeploy.

## 6. Cómo queda automatizado

No hace falta GitHub Actions para esto: tanto Render como Vercel quedan con
`autoDeploy` activo por default al conectar el repo — cada `git push` a
`main` dispara build + deploy en ambos, en paralelo, sin intervención manual.

## Troubleshooting

- **502/503 en Render apenas deployado**: mirá los logs — casi siempre es
  Flyway fallando contra Supabase (host/user/pass mal copiados del pooler) o
  el `POSTGRES_HOST` apuntando a la IPv6 "Direct connection" en vez del
  Session pooler.
- **CORS error en el navegador**: falta la URL exacta de Vercel (con
  `https://`, sin `/` al final) en `CORS_ALLOWED_ORIGINS`.
- **La web pega a `localhost:8081`**: falta `VITE_API_BASE_URL` en Vercel, o
  quedó cacheado un build viejo — redeploy.
- **Rutas internas dan 404 al refrescar en Vercel**: revisá que
  `web/vercel.json` haya llegado al deploy (Root Directory mal seteado en
  Vercel hace que no lo vea).
