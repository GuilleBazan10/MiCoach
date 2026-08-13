# ADR-003 — Frontend web separado en React (Flutter queda mobile-only)

- **Fecha:** 2026-08-13
- **Estado:** Aceptada

## Contexto

El plan original (ADR-002) preveía un único frontend Flutter multiplataforma
(Android/iOS/Web) usando el soporte nativo de Flutter Web. Durante la Fase 3.1
(implementación del frontend mobile) se corrió esa misma app también como
`flutter run -d web-server` para probarla en el navegador, y ahí aparecieron
limitaciones reales, no hipotéticas:

- **Flutter Web renderiza a `<canvas>` (vía CanvasKit), sin DOM real.** Se intentó
  automatizar pruebas de UI (clicks, formularios) contra la app corriendo en el
  navegador y no fue posible de forma confiable: no hay `<input>`/`<button>` reales
  que un script pueda ubicar, y el árbol de accesibilidad que sí existe queda oculto
  por defecto y es frágil de activar/usar programáticamente.
- Esa misma ausencia de DOM real complica SEO (si en algún momento se necesitaran
  páginas públicas indexables) y el uso de herramientas estándar de devtools/QA que
  asumen HTML real.
- El usuario del proyecto además define un requisito nuevo: quiere una **web con
  funcionalidad completa** (no solo un panel de admin) para poder probar la app sin
  depender de generar una APK en cada cambio — es decir, la web deja de ser "un
  espejo secundario de Flutter" y pasa a ser una superficie de prueba de primer nivel,
  que además va a convivir con la app mobile usando el mismo backend.

## Decisión

1. **Flutter queda mobile-only** (Android/iOS). No se vuelve a usar
   `flutter run -d web-server` ni se genera build web de Flutter para producción.
2. **La web es un frontend nuevo e independiente en React**, con paridad de
   funcionalidad completa respecto a mobile (mismas 6 features: auth, profile,
   workout, nutrition, progress, sobre el mismo `core`). Se planifica como
   **Fase 3.2**, ejecutada **antes** de la Fase 4 (IA) — ver `docs/00-progress.md`.
3. **Stack de la web:** React + TypeScript + Vite (SPA, sin SSR/Next.js — es una app
   autenticada tipo dashboard, no un sitio público; se reevalúa si algún día hace
   falta contenido público indexable), TanStack Query (fetching/cache), React Router
   (navegación + guarda de auth), Tailwind CSS + shadcn/ui (componentes con DOM real),
   React Hook Form + Zod (formularios/validación), Axios con interceptor de JWT+refresh.
4. **Sin cambios en el backend.** La API REST ya era agnóstica de frontend (JWT,
   `CORS_ALLOWED_ORIGINS` parametrizado); agregar la web es solo sumar su origen a esa
   variable de entorno.
5. **Requisitos no funcionales explícitos del usuario:** la web debe ser 100%
   responsive (mobile-first, un solo layout para teléfono y desktop) y debe dejar el
   diseño (colores, tipografía, nombre del proyecto) centralizado en un solo lugar,
   porque se espera que cambien — mismo criterio que ya se aplicó en
   `mobile/lib/core/theme/`.

**Alternativas evaluadas y descartadas:**
- *Seguir con Flutter Web* → descartada por las limitaciones de DOM/automatización
  encontradas en la práctica (arriba). Seguía siendo la opción de "un solo código",
  pero ese beneficio no compensa las limitaciones para una web con paridad completa.
- *React Native + React web desde el inicio (en vez de Flutter)* → se descarta como
  cambio retroactivo: reescribir el frontend mobile ya construido (Fase 3.1 completa)
  tiene un costo alto para una ganancia marginal. Además, React Native y React web NO
  comparten componentes de UI (solo lógica), así que tampoco hubiera sido "un solo
  código" en la práctica.
- *Next.js en vez de Vite* → se descarta por ahora: SSR/SSG no aporta nada a una app
  detrás de login. Si en el futuro se necesita una landing pública, se reevalúa.

## Consecuencias

- **Positivas:** DOM real (mejor testabilidad/automatización, mejor accesibilidad,
  mejor soporte de devtools estándar); ecosistema React maduro y con mucha
  disponibilidad de gente que lo conoce; permite probar la app completa desde un
  navegador sin depender de compilar una APK; el backend no requiere ningún cambio.
- **Negativas / costos:** dos frontends para mantener en paralelo (mobile Flutter +
  web React), sin código compartido entre ellos — cualquier cambio de comportamiento
  debe implementarse dos veces (una por plataforma). Se manejan dos ecosistemas de
  build/testing distintos (Gradle/Flutter y npm/Vite) además del backend Gradle.
- **Aprendizaje:** convalida la regla general de "probar en el entorno real antes de
  comprometerse a una decisión de stack" — la limitación de Flutter Web no era
  evidente en el papel, apareció recién al usar la app de verdad.