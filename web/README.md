# MiCoach — App Web

## Estado

**Fase 3.2 — CERRADA (2026-08-13).** Las 6 features (`core`, `auth`, `profile`,
`workout`, `nutrition`, `progress`) están implementadas y verificadas contra el backend
real, con paridad completa respecto a mobile — ver `docs/00-progress.md` § Fase 3.2 para
el detalle de cada entrega.

> Por qué existe un frontend web separado de la app mobile (y no "Flutter Web"):
> `docs/04-adr/ADR-003-frontend-web-react.md`. Plan completo, alcance y checklist de
> verificación de esta fase: `docs/00-progress.md` § Fase 3.2.

## Objetivo

Paridad de funcionalidad **completa** con la app mobile (no un panel reducido):
`auth`, `profile`, `workout`, `nutrition`, `progress`. `admin` y `ai` quedan sin
pantalla propia, mismo criterio que en mobile (gobernanza interna / base técnica sin
UI de usuario final).

Sirve además como superficie de prueba más rápida que compilar una APK mientras se
sigue desarrollando — mobile y web comparten el mismo backend y los mismos datos.

## Stack (decidido, ver ADR-003)

- **React 19 + TypeScript + Vite 8** — SPA, sin SSR/Next.js (es una app autenticada
  tipo dashboard, no un sitio público que necesite SEO).
- **TanStack Query (React Query)** — fetching/cache/invalidación. Mismo rol que los
  `FutureProvider` de Riverpod en mobile.
- **React Router** — navegación + guarda de rutas autenticadas (equivalente al
  `redirect` de GoRouter en mobile).
- **Tailwind CSS + shadcn/ui** — componentes con DOM real (a diferencia de Flutter
  Web), accesibles, fáciles de automatizar/testear.
- **React Hook Form + Zod** — formularios y validación.
- **Axios** con interceptor de JWT + refresh automático ante 401 — mismo patrón que
  `ApiClient` (Dio) en mobile.

## Requisitos no funcionales (explícitos, no negociables)

1. **100% responsive**: un solo layout que se vea bien en teléfono y en PC
   (mobile-first con los breakpoints de Tailwind). NO un sitio "m." aparte.
2. **Abierta a rediseño**: colores, tipografía y el nombre del proyecto van a cambiar.
   Todo eso centralizado en UN lugar (`src/core/theme/` — tokens de Tailwind +
   variables CSS), igual que `mobile/lib/core/theme/` en Flutter, para que rediseñar
   no implique tocar cada pantalla.

## Estructura

Tailwind 4 no usa `tailwind.config.ts` (config CSS-first vía `@theme`, ver
`src/core/theme/tokens.css`) — el resto quedó como se planeó:

```
web/
├── package.json  vite.config.ts  tsconfig.json  components.json (shadcn)
└── src/
    ├── main.tsx  App.tsx         # punto de entrada + providers (Query/Auth/Router)
    ├── components/               # primitivos shadcn (ui/) + composiciones propias
    │                               (option-select.tsx, reutilizado por varias features)
    ├── core/
    │   ├── theme/                # ★ PUNTO ÚNICO DEL DISEÑO ★ (tokens.css)
    │   ├── api/                  # cliente Axios + interceptor JWT/refresh
    │   └── router/                # router raíz, AppShell (nav a las 4 secciones), guardas de auth
    └── features/
        └── auth/ profile/ workout/ nutrition/ progress/
            └── api/ application/ domain/ pages/ components/
```

## Orden de implementación (completo)

Mismo criterio que se usó en mobile: `core` primero, después una feature completa por
entrega, siempre contra la API real (nunca mocks):

`core → auth → profile → workout → nutrition → progress` — las 6 completas.

## Backend

No requiere ningún cambio para soportar este frontend. Solo hay que agregar el origen
de desarrollo de la web a `CORS_ALLOWED_ORIGINS` en el `.env` de la raíz (ver
`mobile/README.md` § CORS para el mismo procedimiento que se hizo con mobile) y
reiniciar el backend.

## Arrancar

Requiere **Node ≥20.19 o ≥22.12** (Vite 8 no arranca con Node 18 o menor —
`create-vite`/`vite` fallan con `SyntaxError` en ese caso). Si tenés varias versiones
con `nvm`: `nvm use 24` (o la LTS más nueva que tengas instalada).

```bash
cd web
npm install
npm run dev          # abre en http://localhost:5173 (puerto por defecto de Vite)
```

Backend corriendo en paralelo (ver `README.md` de la raíz, pasos 1-3) y con
`http://localhost:5173` en `CORS_ALLOWED_ORIGINS` del `.env` de la raíz (ya está agregado
por defecto). **Importante**: entrar por `http://localhost:5173`, no
`http://127.0.0.1:5173` — el navegador los trata como orígenes distintos y el segundo no
está en la lista blanca de CORS (el preflight `OPTIONS` da 403).

## Verificar build y lint

```bash
npm run build   # tsc -b && vite build
npm run lint    # eslint .
```
