# 11 — Identidad de marca: Mi Coach Saludable

> Estado: paleta y tipografía **aplicadas en web** (2026-08-18). Pendiente portar a
> Flutter — ver § Pendiente en mobile al final. Este documento es la fuente de verdad;
> `docs/06-ux-ui-audit.md § 8` queda como el checklist histórico que motivó este trabajo
> (ya resuelto del lado web).

## Origen

El isotipo llegó como SVG ya terminado (escudo bicolor + figura humana + hoja, con
wordmark "Mi Coach Saludable"). Se guardó tal cual en el repo y se derivó de ahí toda
la paleta — ningún color de esta página es una decisión nueva, todos vienen de un
`stop-color` o `fill` real del archivo original.

**Perfil visual completo (referencia interactiva)**:
https://claude.ai/code/artifact/74ede62e-154f-4ee2-8207-04b4ec31a232

## Assets guardados

| Archivo | Qué es | Uso |
|---|---|---|
| `web/src/assets/brand/logo-full.svg` | Lockup completo (isotipo + wordmark + tagline), 800×400 | Pantallas grandes sin restricción de espacio (todavía sin usar — candidato natural: login/register) |
| `web/src/assets/brand/logo-mark.svg` | Solo el isotipo (escudo + figura + hoja), recortado a su bounding box real (`viewBox="77 60 250 290"`, medido con `getBBox()` en vivo, no a ojo) | `web/src/components/Logo.tsx`, usado en el header (`AppShell.tsx`) |
| `web/public/favicon.svg` | Mismo ísotipo que `logo-mark.svg`, sin el filtro de sombra (no se aprecia a 16-32px) | Favicon del navegador — reemplaza el placeholder violeta genérico del scaffold de shadcn |

## Paleta

Seis colores, cada uno con un rol fijo — no hay ninguno puramente decorativo.

| Nombre | Hex puro de marca | Rol en el isotipo |
|---|---|---|
| Harbor Blue | `#0284C7` → `#0F172A` (degradé diagonal) | Arco derecho del escudo + figura humana — protección médica / entrenamiento |
| Vital Green | `#16A34A` → `#22C55E` → `#4ADE80` (degradé diagonal) | Arco izquierdo del escudo + hoja interior — salud nutricional |
| Ember Orange | `#F97316` → `#FB923C` (degradé horizontal) | Único acento — la sílaba "-ble" de "Saludable" |
| Ink Navy | `#0F2942` | Texto del wordmark |
| Slate Mist | `#64748B` | Tagline "SALUD & ENTRENAMIENTO ADAPTATIVO" |
| Paper White | `#FFFFFF` | Fondo del isotipo original |

### Mapeo semántico (el hallazgo más útil de este perfil)

Los dos colores del escudo ya coinciden 1 a 1 con los dos módulos centrales de la app —
no hubo que inventar una convención nueva, solo reconocerla:

| Color | Significa | Módulo de la app |
|---|---|---|
| Harbor Blue | Protección, entrenamiento, seguimiento clínico | Rutinas · Sesiones · Perfil médico |
| Vital Green | Nutrición, vitalidad | Planes de alimentación · Recetas |
| Ember Orange | El único acento — energía, destacado | Transversal: badges "generado con IA", rachas. Un único CTA de este color por pantalla, nunca dos a la vez |

## Ajuste de contraste WCAG (por qué el token no es el hex "puro")

`docs/06-ux-ui-audit.md § 9 ítem 9` dejó pendiente explícitamente un chequeo de
contraste "una vez que hubiera paleta final" — se hizo acá, con números reales
(fórmula de luminancia relativa WCAG 2.1, no estimado a ojo):

| Par | Hex puro de marca | Contraste con blanco | ¿Pasa AA? | Token usado en `tokens.css` | Contraste del token |
|---|---|---|---|---|---|
| `--primary` | `#16A34A` | 3.30:1 | Falla texto normal (14px `font-medium` en botones no es "large text") | `#15803D` (verde-700) | 5.02:1 ✅ |
| `--accent` | `#0284C7` | 4.10:1 | Falla por poco | `#0369A1` (mismo tono que el `figureGrad` del isotipo — no es un color nuevo) | 5.93:1 ✅ |
| `--highlight` | `#F97316` | 2.80:1 | Falla incluso el umbral de UI/texto grande (3:1) | `#C2410C` (naranja-700) | 5.18:1 ✅ |

**Importante**: esto no es "abandonar" el color de marca — es la misma familia de color,
un paso más oscuro, y solo aplica a los tokens donde hay **texto blanco encima**
(`--primary-foreground`/`--accent-foreground`/`--highlight-foreground: #ffffff`, botones
y badges). El hex puro sigue siendo el correcto para el isotipo en sí y para cualquier
superficie grande sin texto encima (ej. la franja de 3px de `--gradient-hero` en el
header, que no lleva texto directo — pero si `HeroBanner.tsx` lo usa de fondo con texto
sí necesita el token ajustado, por eso `--gradient-hero` también usa los tonos
ajustados).

Modo oscuro: en vez de ajustar por contraste, se reusan los propios *stops claros* del
degradé de marca (`#4ADE80` verde, y `#FB923C` naranja — ya están en el SVG original) y
`#0F172A` (el stop oscuro del degradé azul) como ink de texto — todos con contraste
>7:1, sin inventar ningún hex nuevo fuera de lo que ya trae el isotipo.

## Tipografía

`Plus Jakarta Sans` en los 4 pesos que usa el propio SVG (`Inter`/`system-ui` como
fallback, igual que el `font-family` del logo original). Todavía no está cargada en la
app (el `web/index.html` actual usa `@fontsource-variable/geist`) — es la única pieza de
este perfil que no se aplicó en código, queda en el backlog de §8 de
`docs/06-ux-ui-audit.md` (tabla "Tipografía de marca").

| Uso | Peso | Tracking |
|---|---|---|
| Wordmark | 700 · Bold | -0.5px |
| Énfasis ("-ble") | 700 · Bold, color Ember Orange | -0.5px |
| Tagline | 600 · Semibold, mayúsculas | 2px |

## Cambios aplicados en web (2026-08-18)

- **`web/src/core/theme/tokens.css`** — `--primary`, `--accent`, `--highlight`,
  `--foreground`, `--gradient-hero`, `--ring`, `--sidebar-primary`, `--sidebar-ring`
  actualizados en `:root` y `.dark` (ver tabla de contraste arriba para los valores
  exactos). `--background`/`--card`/`--muted`/`--chart-*` **no se tocaron** — son un
  sistema de neutros y datos separado del color de marca, cambiarlos es una decisión de
  UI más grande que no estaba pedida acá.
- **`web/src/components/Logo.tsx`** (nuevo) — componente `<Logo variant="mark" | "full" />`.
- **`web/src/core/router/AppShell.tsx`** — el badge con ícono `Zap` de `lucide-react`
  (placeholder genérico, documentado como tal en `docs/06-ux-ui-audit.md § 1.1`) se
  reemplazó por `<Logo className="size-8" />` en el header.
- **`web/public/favicon.svg`** — reemplaza el ícono violeta abstracto por defecto del
  scaffold de shadcn (sin relación con la marca) por el isotipo real.
- Verificado en vivo en el navegador: el logo carga (`complete: true`, sin imagen rota),
  los tokens nuevos se leen correctamente vía `getComputedStyle`, `tsc --noEmit` sin
  errores.

**No se tocó** `LoginPage.tsx`/`RegisterPage.tsx` — no estaban en la tabla de impacto de
`docs/06-ux-ui-audit.md § 8` (que solo lista favicon + header como los dos puntos de
contacto web), así que se mantuvo el cambio acotado a esos dos. Si se quiere sumar el
lockup completo (`logo-full.svg`, ya guardado y listo) a login/register, es un cambio
chico aparte.

## Pendiente en mobile (Flutter)

Nada de esto se tocó — `docs/00-progress.md § Paridad Flutter` ya tenía esto marcado
como **"Bloqueado hasta tener assets de marca reales"**; ahora que llegaron, esto es lo
que falta portar, con la misma tabla de impacto que ya preveía
`docs/06-ux-ui-audit.md § 8`:

- [ ] **`mobile/lib/core/theme/app_colors.dart`** — hoy tiene `seed`/`primary` en
  `#4CAF50` (claro) / `#81C784` (oscuro) y `accent` en `#00BFA5`, sin ningún equivalente
  a `--highlight`. Actualizar `AppColors.light`/`AppColors.dark` con los mismos valores
  que ya quedaron en `tokens.css` (ver tabla de arriba) — **agregar un campo
  `highlight`/`onHighlight` nuevo a la clase**, no existe hoy.
- [ ] **Logo/ícono de app** — `mobile/web/icons/Icon-*.png` son los placeholders que
  genera `flutter create`, nunca se reemplazaron (confirmado en
  `docs/06-ux-ui-audit.md § 1.1`). Regenerar desde `logo-mark.svg` con una herramienta
  de app icons (ej. `flutter_launcher_icons`), más ícono nativo Android/iOS si se genera
  un build firmado.
- [ ] **Splash screen** — `mobile/lib/core/router/splash_screen.dart` ya existe como
  pantalla mientras se restaura la sesión; es el lugar natural para el isotipo
  (`logo-mark.svg` o `logo-full.svg` según el espacio disponible).
- [ ] **Tipografía** — `mobile/lib/core/theme/app_text_styles.dart`, mismo pendiente que
  en web (Plus Jakarta Sans todavía no está cargada en ningún lado).

**Instrucción para quien porte esto** (ya la tenía `docs/06-ux-ui-audit.md § 8`, se
repite acá porque sigue siendo válida): actualizar únicamente `app_colors.dart` — el
resto de la UI de Flutter ya consume esos valores por diseño. Si algún widget no cambia
solo al tocar `app_colors.dart`, es señal de un color hardcodeado por fuera del sistema
de tokens — vale la pena flaguearlo como bug de arquitectura, no parchearlo local.
