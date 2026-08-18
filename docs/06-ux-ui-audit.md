# Auditoría UX/UI — MiCoach

> **Para quien continúe (IA o persona) con desarrollo/arquitectura.** Este documento es
> una auditoría de diseño (UX/UI) sobre el estado real del código a **2026-08-17**, no
> un mockup ni una propuesta visual cerrada. Cada hallazgo indica **qué cambiar, por
> qué, dónde (archivo/línea) y con qué prioridad**, para que se pueda convertir
> directamente en tareas de implementación sin tener que releer todo el frontend.
>
> **Branding pendiente**: todavía no hay logo ni diseños de marca definitivos (ver
> §8). El nombre "MiCoach" y la paleta verde/turquesa actual son *placeholders*
> reconocidos como tales en el propio código (`web/src/core/theme/tokens.css:1-9`,
> `README.md` § "Personalización del nombre y diseño"). Este documento audita la
> **estructura y calidad UX** independientemente de esa paleta final, y deja marcado
> exactamente dónde entran los assets de marca cuando existan.
>
> **Actualización 2026-08-17 (Parte 2)**: se agregó una auditoría específica de
> formularios/inputs y "detalles que deberían existir por lógica" (ver §§11-19),
> hecha revisando código de ambas plataformas y probando la web levantada en local.
> No cambia nada de lo escrito en §§0-10, lo extiende.

## 0. Resumen ejecutivo

El proyecto ya tiene una base de diseño **mejor que la media** para su etapa: tokens
centralizados en un único punto por plataforma (`web/src/core/theme/tokens.css`,
`mobile/lib/core/theme/`), componentes reutilizables (`HeroBanner`, `EmptyState`),
sistema de componentes accesible de base (shadcn/ui sobre Radix), y mobile-first real
verificado en dos anchos. Lo que falta no es "empezar el diseño de cero", sino:

1. **Cerrar la brecha entre web y mobile** — web tiene un sistema visual con más
   intención (gradientes, iconografía, banners); mobile sigue en Material 3 por
   defecto (confirmado en `docs/00-progress.md:463-464`: *"sigue usando los widgets de
   Material 3 por defecto"*).
2. **Cubrir estados que hoy no existen**: loading states largos (generación con IA,
   1-3 min), onboarding/primer uso, estados de error de red distintos de "vacío".
3. **Definir identidad real** (nombre, logo, favicon) antes de que el placeholder se
   filtre a más lugares (íconos de PWA, splash screens, metadata).
4. **Accesibilidad**: hay buena base semántica pero cero verificación de contraste,
   foco visible y navegación por teclado.

No hay que rehacer nada existente — los tokens y componentes base son el punto de
apalancamiento correcto. El trabajo es **extender ese sistema**, no reemplazarlo.

---

## 1. Identidad de marca (branding)

### 1.1 Nombre y favicon son placeholders sin marcar visualmente como tales
- **Qué pasa**: el favicon actual (`web/public/favicon.svg`) es el logo genérico por
  defecto del scaffold de shadcn (figura abstracta violeta), no tiene relación con la
  paleta verde/turquesa del resto de la app. El ícono del header (`AppShell.tsx:36-42`)
  usa un rayo (`lucide-react` `Zap`) genérico sobre un cuadrado con gradiente — funciona
  como placeholder pero no es un logo, es un ícono de librería.
- **Por qué importa**: cuando lleguen los assets reales de marca, hay que tocar mínimo
  4 lugares independientes que hoy no están conectados entre sí (ver §8 para la lista
  completa) — si no se deja documentado, es fácil que quede desincronizado (web con
  logo nuevo, mobile con el ícono de Flutter por defecto).
- **Mobile aún peor**: `mobile/web/icons/Icon-*.png` son los íconos placeholder que
  genera `flutter create` automáticamente — nunca se reemplazaron. Es el ícono
  genérico de Flutter, ni siquiera un intento de marca.
- **Recomendación**: no accionar todavía (bloqueado por §8), pero cuando lleguen los
  assets, tratarlos como **un solo cambio atómico** que toque los 2 frontends a la vez,
  no como retoques sueltos por feature.

### 1.2 El nombre "MiCoach" aparece hardcodeado en 3+ lugares de UI, no solo en config — ✅ RESUELTO (2026-08-17)
- **Qué pasa**: `AppShell.tsx:42` (`<span>MiCoach</span>`), `LoginPage.tsx:42`
  (`Entrá a tu cuenta de MiCoach`), y previsiblemente `RegisterPage.tsx` tienen el
  nombre escrito a mano en JSX, no importado de una constante.
- **Riesgo concreto**: `docs/01-architecture.md § 11 "Renombrar el proyecto"` ya
  documenta cómo renombrar el paquete/proyecto a nivel Gradle/Flutter, pero **no cubre
  los strings de UI en React** — quien siga esa guía va a renombrar el backend y
  dejar "MiCoach" pegado en 3 pantallas del frontend web.
- **Recomendación**: crear una constante única `APP_NAME` (ej. `web/src/core/config.ts`
  o directamente una env var `VITE_APP_NAME`) y reemplazar los 3+ literales. Bajo
  costo, evita un bug de rebranding incompleto. Prioridad **alta pero barata** — se
  puede hacer ya, no depende de tener el logo final.

---

## 2. Sistema de diseño: paridad web ↔ mobile

### 2.1 Mobile no tiene equivalente de `HeroBanner`, `EmptyState` ni gradiente de marca
- **Evidencia**: `web/src/components/HeroBanner.tsx` y `EmptyState.tsx` son
  componentes con intención de diseño (gradiente `--gradient-hero`, iconografía en
  círculo, stats chips). El lado mobile (`mobile/lib/features/*/presentation/*.dart`)
  no tiene un widget compartido equivalente — cada pantalla probablemente usa
  `Text`/`Card` de Material 3 sin ese tratamiento visual (confirmado indirectamente por
  la nota de `docs/00-progress.md:463`).
- **Impacto en el usuario**: un usuario que usa la app en mobile (el target real según
  ADR-003 — mobile es la plataforma final, web es superficie de prueba) tiene una
  experiencia visualmente más pobre que quien prueba en web, que es exactamente al
  revés de la prioridad del proyecto.
- **Recomendación**: portar el patrón `HeroBanner`/`EmptyState` a Flutter como widgets
  en `mobile/lib/core/` (ej. `core/widgets/hero_banner.dart`,
  `core/widgets/empty_state.dart`), consumiendo `AppColors`/`AppSpacing` igual que hace
  el resto del tema. Es trabajo de implementación pura (el patrón visual ya está
  resuelto en React, solo hay que traducirlo a widgets Flutter) — buen candidato para
  delegar a un chat de desarrollo sin decisiones de diseño adicionales.

### 2.2 Mobile no tiene escala tipográfica explícita comparable a la de web
- **Evidencia**: `mobile/lib/core/theme/app_text_styles.dart` existe pero
  `app_theme.dart:50` solo lo aplica como `textTheme: text.textTheme(base.textTheme)`
  — hay que revisar si define pesos/tamaños propios o hereda Material 3 default. Web
  tampoco tiene un archivo de escala tipográfica dedicado (usa clases de Tailwind
  directamente en cada componente: `text-xl`, `text-lg`, `text-sm` repartidas sin una
  tabla central).
- **Recomendación**: definir una escala tipográfica nombrada y compartida
  conceptualmente entre plataformas (ej. `display`, `title`, `body`, `caption`,
  `label`) documentada en un único lugar por plataforma — en web como capa de
  utilidades Tailwind (`@theme` en `tokens.css` o clases `.text-display` etc.), en
  mobile ya existe el archivo correcto (`app_text_styles.dart`), solo falta que sea
  exhaustivo y que las pantallas lo usen en vez de estilos inline. Esto evita que cada
  desarrollador improvise tamaños nuevos feature por feature.

### 2.3 Los "gaps" de diseño quedan documentados en comentarios de código, no en un solo lugar
- **Qué pasa**: notas como "sigue usando los widgets de Material 3 por defecto" viven
  en `docs/00-progress.md`, mezcladas con el registro de avance técnico. Es información
  de diseño, pero está enterrada en un changelog de 1000+ líneas.
- **Recomendación**: este mismo documento (`docs/06-ux-ui-audit.md`) pasa a ser el
  punto de referencia de deuda de diseño. Cuando se cierre un ítem de este audit,
  marcarlo aquí (no solo en `00-progress.md`), para que alguien que solo quiera saber
  "¿qué falta de diseño?" no tenga que grepear el progreso técnico completo.

---

## 3. Estados de carga, vacío y error

### 3.1 Generación con IA no tiene un loading state proporcional a su duración real — ✅ RESUELTO EN WEB (2026-08-17)
- **Evidencia dura**: `docs/00-progress.md:719` documenta que una generación real con
  Ollama tardó **2m32s**. `GenerateWorkoutDialog` (mencionado en
  `docs/00-progress.md:692-695`) sube el timeout del cliente a 180s para tolerarlo.
  Nada en el código auditado indica un estado de progreso distinto de un spinner
  genérico de "cargando" mientras pasan hasta 3 minutos.
- **Por qué es un problema de UX real, no cosmético**: 2-3 minutos sin feedback
  progresivo es tiempo suficiente para que un usuario piense que la app se colgó y
  cierre la pestaña/app, perdiendo el request en curso. Es el punto de mayor riesgo de
  abandono de todo el flujo de IA (la feature más diferenciada del producto, según el
  propio README).
- **Recomendación concreta**:
  - Mensaje de progreso con expectativa de tiempo explícita ("Esto puede tardar hasta
    3 minutos — estamos generando tu rutina con IA"), no un spinner mudo.
  - Idealmente, mensajes que roten cada ~15-20s ("Analizando tu perfil...",
    "Seleccionando ejercicios...", "Ajustando series y repeticiones...") aunque sean
    aproximaciones — reduce la percepción de tiempo muerto sin requerir streaming real
    del backend. **Hecho en web**: `web/src/core/hooks/useRotatingMessage.ts` +
    copy "puede tardar hasta 3 minutos" en `GenerateWorkoutDialog` y
    `GenerateMealPlanDialog`. Falta el equivalente Flutter (queda dentro del ítem de
    paridad mobile ya trackeado en `docs/00-progress.md`).
  - Si se quiere una solución más robusta a futuro: exponer progreso real vía
    WebSocket/SSE desde `AiService`, pero eso es cambio de arquitectura backend, fuera
    de alcance de este audit — dejarlo como nota para quien priorice backend/IA, no
    bloquea la mejora de copy/UI de arriba.
  - Afecta: `GenerateWorkoutDialog` (web) y su equivalente en
    `mobile/lib/features/workout/presentation/widgets/generate_workout_dialog.dart`.

### 3.2 No hay estado de error diferenciado para fallos de red vs. resultados vacíos — ✅ RESUELTO EN WEB (2026-08-17)
- **Qué pasa**: `EmptyState.tsx` está diseñado para "no hay datos todavía" (mensaje +
  ícono neutro), pero no hay evidencia de un componente equivalente para "falló la
  petición" (timeout del backend en cold start de Render free tier — ver
  `docs/00-progress.md` commit reciente sobre subir el timeout a 60s, y el propio
  historial de commits: *"Sube el timeout del cliente HTTP a 60s (cold start de Render
  free tier)"*).
- **Por qué importa específicamente en este proyecto**: el deploy actual usa Render
  free tier, que duerme el backend tras inactividad — el cold start ya es un fallo de
  UX conocido y parcialmente mitigado a nivel de timeout, pero no a nivel de mensaje al
  usuario. Un usuario que abre la app y ve una pantalla vacía o un spinner de 60s sin
  explicación va a asumir que está rota.
- **Recomendación**: un componente `ErrorState` (paralelo a `EmptyState`) con mensaje
  específico para fallos de red/timeout ("No pudimos conectar con el servidor. Si es
  la primera vez que abrís la app hoy, puede tardar hasta un minuto en despertar.") y
  botón de reintentar. Aplica a ambas plataformas. **Hecho en web**:
  `web/src/components/ErrorState.tsx` + `NETWORK_ERROR_MESSAGE` en
  `core/api/apiError.ts`, cableado en las 6 vistas de lista (`WorkoutListView`,
  `SessionHistoryList`, `MealPlanListView`, `ShoppingListListView`,
  `MetricEntriesView`, `ProgressPhotosView`). Falta el equivalente Flutter.

### 3.3 Formularios: falta feedback de éxito consistente
- **Evidencia**: `LoginPage.tsx` maneja bien el error (`serverError` + `Alert
  variant="destructive"`), pero no hay evidencia de confirmación visual post-éxito más
  allá de la navegación (ej. crear una rutina, guardar el perfil). `sonner` está
  instalado como dependencia (`package.json`) — sugiere que hay intención de usar
  toasts, pero no está claro si se usa consistentemente en todas las mutaciones o solo
  en algunas.
- **Recomendación**: auditar (no rediseñar) el uso de `sonner`/toasts en las mutations
  de cada feature (`workout`, `nutrition`, `progress`, `profile`) y asegurar que **toda
  operación de escritura** (crear, editar, borrar) tenga confirmación visible, no solo
  las que un desarrollador individual recordó agregar. Esto es más una tarea de
  QA/consistencia que de diseño nuevo.

---

## 4. Onboarding y primer uso

### 4.1 No existe una pantalla de bienvenida/onboarding — se cae directo al formulario de perfil vacío — ✅ RESUELTO EN WEB (2026-08-17)
- **Qué pasa**: tras registrarse, el flujo (`docs/00-progress.md:625-628`) crea el
  perfil de forma perezosa y redirige a `/workouts`. Un usuario nuevo sin rutinas ni
  perfil completo llega a una pantalla que depende 100% del `EmptyState` genérico para
  explicar qué hacer.
- **Por qué importa en este producto específico**: la app pide datos sensibles y
  extensos (patologías, lesiones, medicación, objetivos, equipamiento — ver
  `README.md:22-27`) antes de que la IA pueda generar algo útil. Sin guía, un usuario
  nuevo no tiene por qué saber que **completar el perfil es el paso 1** para que la
  generación con IA funcione bien.
- **Recomendación**: no hace falta un wizard multi-paso complejo — alcanza con que el
  `EmptyState` de la pantalla de rutinas, cuando el perfil está incompleto, tenga un
  mensaje y CTA específico ("Completá tu perfil para generar rutinas personalizadas" →
  botón directo a `/profile`) en vez del mensaje genérico de "no hay rutinas todavía".
  Esto es lógica condicional simple sobre el `EmptyState` existente, no un componente
  nuevo. **Hecho en web**: `WorkoutListView.tsx` — si `!templates` y falta
  `experienceLevel`/`equipment` en el perfil, muestra CTA a `/profile` en vez del
  empty state genérico. `EmptyState.tsx` ganó un prop `action` reutilizable para esto.
  Falta el equivalente en Nutrición (el ajuste de calorías también depende del
  perfil) y en Flutter.

---

## 5. Accesibilidad

### 5.1 Buena base semántica, cero verificación real
- **A favor**: uso correcto de `<Label htmlFor>`, `aria-invalid`, `aria-label` en
  botones icon-only (`AppShell.tsx:53`: `aria-label="Cerrar sesión"`), Radix/shadcn
  como base (maneja foco y ARIA roles razonablemente bien out-of-the-box).
- **Sin verificar**: contraste de color (la paleta actual — verde `#1fa251` sobre
  blanco, texto sobre gradiente `--gradient-hero` — no tiene evidencia de haber pasado
  un chequeo WCAG AA), navegación completa por teclado (tabs, dialogs, dropdown menus),
  y comportamiento de foco visible al navegar sin mouse.
- **Recomendación**: antes de fijar la paleta final de marca (§8), correr un chequeo
  de contraste automatizado (axe DevTools o Lighthouse accessibility audit) sobre las
  pantallas principales. Es más eficiente hacerlo una vez con la paleta definitiva que
  dos veces (placeholder + final).

### 5.2 Toggle de dark mode: verificar que no rompe contraste
- **Evidencia**: `tokens.css` define un bloque `.dark` completo y consistente con
  mobile (`app_colors.dart` tiene su propio set `dark`), lo cual es correcto
  estructuralmente. No hay evidencia de que se haya verificado visualmente cada
  pantalla en modo oscuro (el progreso documentado solo verifica responsive en 375px/
  1280px, no light/dark × cada pantalla).
- **Recomendación**: pase de verificación visual en dark mode sobre las 4 secciones
  principales + auth, mismo criterio que ya se usó para responsive.

---

## 6. Consistencia de contenido (copy)

### 6.1 Tono y idioma consistentes, pero sin guía escrita
- **A favor**: todo el copy auditado está en español rioplatense consistente ("Entrá a
  tu cuenta", "Registrate", "¿No tenés cuenta?") — buena señal de coherencia.
- **Riesgo**: sin una guía de tono documentada, es fácil que features nuevas (o un
  chat de desarrollo distinto) introduzcan inconsistencias (formal "usted" vs. "vos",
  mezcla con inglés en botones, etc.).
- **Recomendación**: agregar una sección corta a este documento (o a un
  `docs/07-content-guidelines.md` si crece) con 4-5 reglas: voseo argentino, sin
  mezclar inglés salvo términos técnicos aceptados (login, IA), mensajes de error en
  positivo/accionable ("Revisá tus datos" en vez de "Error de validación"), longitud
  máxima sugerida para mensajes de toast/alert.

---

## 7. Panel de administración (`admin/ai`)

- **Estado actual**: existe una pantalla (`AiProvidersPage.tsx`,
  `AiProviderCard.tsx`) protegida por `RequireAdmin.tsx`, fuera de la navegación
  principal de usuario final (correcto, es gobernanza interna según
  `docs/00-progress.md`).
- **Nivel de auditoría necesario aquí**: bajo. Es una superficie interna, no de cara al
  usuario final — no amerita el mismo nivel de pulido visual que las 4 secciones de
  usuario. Se menciona solo para que quede explícito que **no es una omisión**, es una
  decisión de alcance ya tomada y correcta.

---

## 8. Pendiente: assets de marca reales

Esta sección se actualiza cuando lleguen el logo y los diseños de referencia. Mientras
tanto, queda documentado **dónde impactan** para que integrarlos sea un checklist, no
una exploración:

| Asset | Dónde va (web) | Dónde va (mobile) |
|---|---|---|
| Logo / isotipo | `web/public/` (favicon.svg/ico + variante para header, reemplaza `Zap` en `AppShell.tsx:36-42`) | `mobile/web/icons/Icon-*.png` (regenerar con herramienta de app icons), más ícono nativo Android/iOS si se genera APK/build firmado |
| Paleta de marca | `web/src/core/theme/tokens.css` (bloques `:root` y `.dark`) | `mobile/lib/core/theme/app_colors.dart` (`AppColors.light`/`AppColors.dark`) — **deben quedar sincronizados**, ya hay comentarios cruzados en ambos archivos que lo recuerdan |
| Tipografía de marca | `web/index.html` (font import) + `tokens.css`/Tailwind config — actualmente `@fontsource-variable/geist` | `mobile/lib/core/theme/app_text_styles.dart` |
| Nombre final del producto | Constante única de UI (crear, ver §1.2) + `web/index.html` `<title>` + `README.md` + guía de rename en `docs/01-architecture.md § 11` | `mobile/pubspec.yaml` (`name`), strings de UI, `android`/`ios` display name |
| Splash screen | — (SPA no tiene splash tradicional) | `mobile/lib/core/router/splash_screen.dart` ya existe como pantalla mientras se restaura sesión — es el lugar natural para el logo animado/estático |

**Instrucción para quien reciba los assets**: no aplicar la paleta nueva parche por
parche en cada componente. Actualizar únicamente los dos archivos de tokens
(`tokens.css` y `app_colors.dart`) — todo el resto de la UI ya consume esas variables
por diseño (es literalmente el propósito documentado de esos archivos, ver comentarios
en la cabecera de ambos). Si al aplicar la paleta nueva algún componente *no* se
actualiza solo, es señal de que ese componente tiene un color hardcodeado por fuera del
sistema de tokens — vale la pena flaguearlo como bug de arquitectura de diseño, no
parchearlo local.

---

## 9. Roadmap sugerido (por costo/impacto)

**Rápido y barato — no depende de assets de marca, se puede hacer ya:**
1. ✅ Constante `APP_NAME` centralizada (§1.2) — RESUELTO 2026-08-17.
2. ✅ `ErrorState` para fallos de red/timeout, con mensaje específico sobre cold start de
   Render (§3.2) — RESUELTO EN WEB 2026-08-17.
3. ✅ Mensaje de progreso con expectativa de tiempo en generación con IA (§3.1) —
   solo copy + UI, sin tocar backend — RESUELTO EN WEB 2026-08-17.
4. ✅ `EmptyState` condicional en rutinas cuando el perfil está incompleto (§4.1) —
   RESUELTO EN WEB 2026-08-17 (falta el equivalente en Nutrición).

**Medio — trabajo de implementación, patrón ya resuelto en web:**
5. Portar `HeroBanner`/`EmptyState` a widgets Flutter compartidos (§2.1).
6. Escala tipográfica nombrada y exhaustiva en ambas plataformas (§2.2).
7. Auditoría de cobertura de toasts de éxito en todas las mutations (§3.3).

**Bloqueado hasta tener assets de marca (§8):**
8. Logo, favicon, íconos de app, splash — cambio atómico en los 2 frontends.
9. Chequeo de contraste WCAG AA sobre la paleta final (§5.1) — hacerlo una vez con
   colores definitivos, no con el placeholder actual.

**Sin asignar / requiere decisión de producto, no solo de diseño:**
10. Progreso real (streaming) en generación con IA vía WebSocket/SSE — cambio de
    arquitectura backend, mencionado en §3.1 solo como nota, no como tarea de este
    audit.

---

## 10. Cómo usar este documento (nota para el chat de desarrollo/arquitectura)

- Cada hallazgo tiene archivo(s) afectado(s) citado(s) explícitamente — no hace falta
  re-explorar el repo para ubicar dónde tocar.
- Los ítems de §9 "rápido y barato" no requieren decisiones de diseño adicionales ni
  esperar assets — son implementables directo.
- Los ítems bloqueados por §8 no deberían empezarse en paralelo con placeholders "a
  ver qué onda" — generan el mismo problema de desincronización que ya existe entre
  mobile y web (§2), multiplicado.
- Si se cierra un ítem, actualizar su estado en este mismo documento (agregar
  `✅ RESUELTO (fecha)` al subtítulo correspondiente), siguiendo la misma convención que
  `docs/00-progress.md`, para que este archivo siga siendo la fuente de verdad de deuda
  de diseño y no quede desactualizado como un reporte de una sola vez.

---

# Parte 2 — Auditoría de formularios, inputs y detalles de interacción (2026-08-17)

> Alcance de esta parte: revisión pedida explícitamente sobre **inputs** ("¿están
> correctos?") y sobre **detalles que deberían existir por lógica/estándar de UX**
> (ej. mostrar/ocultar contraseña). Metodología: lectura de código de los formularios
> reales de ambas plataformas (web `web/src/features/*`, mobile
> `mobile/lib/features/*`) + prueba en vivo de la web levantada en local
> (`http://localhost:5173`, login/registro). Cuando un hallazgo aplica distinto a cada
> plataforma, se aclara explícitamente — varios de los gaps más importantes son de
> **paridad entre plataformas**, no inventos nuevos: una plataforma ya resolvió el
> problema y la otra no lo portó.

## 11. Contraseñas

### 11.1 Ningún formulario permite mostrar/ocultar la contraseña — el ejemplo que disparó esta auditoría — ✅ RESUELTO EN WEB (2026-08-17)
- **Verificado en vivo**: en `http://localhost:5173/login` y `/register`, el campo
  contraseña es un `<input type="password">` plano, sin ícono de ojo ni ningún control
  para revelar el texto. Código: `LoginPage.tsx:62-69`, `RegisterPage.tsx:60-69`.
- **Mobile tiene el mismo gap**: `login_screen.dart:69-72` y
  `register_screen.dart:64-71,73-81` usan `obscureText: true` sin `suffixIcon` de
  toggle — Flutter sí soporta esto de forma nativa (cambiar `obscureText` con
  `setState` + `IconButton` en `suffixIcon` del `InputDecoration`), simplemente no se
  implementó.
- **Por qué es un estándar esperable, no un "nice to have"**: contraseñas largas
  tipeadas a ciegas (sobre todo en mobile, con teclado táctil) son la causa n.º 1 de
  errores de tipeo silenciosos que después se leen como "no puedo iniciar sesión". Es
  una convención tan establecida que su ausencia se nota.
- **Recomendación concreta (web)**: crear `web/src/components/ui/password-input.tsx` —
  wrapper de `Input` que agrega un botón icon-only absolutamente posicionado a la
  derecha (`Eye`/`EyeOff` de `lucide-react`, ya es dependencia del proyecto — cero
  librerías nuevas), alterna `type` entre `"password"`/`"text"`,
  `aria-label="Mostrar contraseña"`/`"Ocultar contraseña"`, `tabIndex` correcto para no
  romper el orden de tabulación. Reemplazar el `<Input type="password">` en
  `LoginPage.tsx`, `RegisterPage.tsx`, y en cualquier otro lugar que pida contraseña
  (cambio de contraseña, si existe en `ProfileForm` o similar — no se encontró
  evidencia de esa pantalla en el código auditado, ver §11.4).
- **Recomendación concreta (mobile)**: mismo patrón con `StatefulWidget` local (un
  `bool _obscure` por campo) + `suffixIcon: IconButton(icon: Icon(_obscure ?
  Icons.visibility_off : Icons.visibility), onPressed: () => setState(() => _obscure =
  !_obscure))` en `login_screen.dart` y `register_screen.dart` (los 2 campos de
  contraseña, incluido "Confirmar contraseña").
- **Prioridad**: alta, barata, sin dependencias — es el quick win más directo de todo
  el documento.

### 11.2 Web no pide "confirmar contraseña" al registrarse — mobile sí (gap de paridad real) — ✅ RESUELTO EN WEB (2026-08-17)
- **Evidencia concreta**: `register_screen.dart:73-81` (mobile) tiene un tercer campo
  `_confirmController` con validación `value != _passwordController.text ? 'Las
  contraseñas no coinciden' : null`. `RegisterPage.tsx` (web) solo tiene email +
  contraseña (`registerSchema` en `authSchemas.ts:14-17` no define `confirmPassword`).
- **Por qué importa**: sin confirmación, un typo en la contraseña al registrarse crea
  una cuenta con una contraseña que el usuario no sabe cuál es — se entera recién en el
  primer intento de login fallido, sin pista de qué salió mal. Mobile ya evita este
  problema; web lo reintroduce.
- **Recomendación**: agregar `confirmPassword: z.string()` a `registerSchema` en
  `authSchemas.ts` con `.refine((data) => data.password === data.confirmPassword, {
  message: 'Las contraseñas no coinciden', path: ['confirmPassword'] })`, y el campo
  correspondiente en `RegisterPage.tsx` (mismo patrón visual que el campo contraseña
  existente). Es directamente portar lo que mobile ya tiene resuelto.

### 11.3 El requisito de contraseña (mínimo 8 caracteres) solo se ve **después** de fallar — ✅ RESUELTO EN WEB (2026-08-17)
- **Qué pasa**: tanto `authSchemas.ts:16` (web) como `register_screen.dart:70` (mobile)
  muestran "La contraseña debe tener al menos 8 caracteres" / "Mínimo 8 caracteres"
  como mensaje de error, recién tras un intento de submit fallido. No hay texto de
  ayuda visible desde el principio.
- **Recomendación**: agregar un texto de ayuda estático bajo el campo contraseña
  ("Mínimo 8 caracteres") visible siempre, no solo como error — evita el ciclo de
  "escribir 6 caracteres → submit → error → corregir → submit". Aplica a ambas
  plataformas, cambio de copy/UI puro.

### 11.4 No existe flujo de "¿Olvidaste tu contraseña?" (backlog, no quick win)
- **Verificado**: ni `LoginPage.tsx` ni `login_screen.dart` tienen un link de
  recuperación de contraseña; no se encontró endpoint de reset en
  `docs/03-api-contracts.md` ni módulo `auth` con esa capacidad
  (`docs/00-progress.md` § módulo `auth` solo lista register/login/refresh/me).
- **Por qué se separa del resto**: a diferencia de los ítems anteriores, esto no es un
  detalle de UI — requiere infraestructura nueva (envío de emails, tokens de reset con
  expiración, endpoint backend). No es un "detalle que falta por lógica", es una
  feature de producto sin construir todavía. Se deja documentado acá para que quede
  registrado como gap conocido, pero en el roadmap de §19 va en el bloque de
  "requiere decisión de producto/arquitectura", no en "rápido y barato".

## 12. Confirmación de acciones destructivas

### 12.1 Patrón inconsistente: las entidades principales confirman el borrado, los sub-recursos no — en ambas plataformas — ✅ RESUELTO EN WEB (2026-08-17)
- **Lo que sí está bien resuelto** (para no perder de vista lo que funciona): borrar
  una **rutina** completa sí pide confirmación en los dos frontends —
  `WorkoutDetailPage.tsx:75,119-129` (web, `Dialog` con `DialogTitle "Borrar rutina"` +
  botón `variant="destructive"`) y `workout_detail_screen.dart:55-71` (mobile,
  `AlertDialog` con el texto *"¿Seguro que querés borrar "{nombre}"? Esta acción no se
  puede deshacer."*). Es el patrón correcto y ya está probado en producción de código —
  el problema es que **no se aplicó al resto de los borrados**.
- **Lo que falta**: todo borrado de **sub-recursos** dispara la mutación
  inmediatamente al primer click, sin ningún diálogo intermedio, confirmado por código
  en ambas plataformas:
  - Web: `GoalSection.tsx:66` (`onClick={() => deleteGoal.mutate(goal.id, ...)}`),
    mismo patrón directo en `PathologySection.tsx`, `InjurySection.tsx`,
    `MedicationSection.tsx`, `MetricEntriesView.tsx:72`, `ProgressPhotosView.tsx`,
    `DailyIntakeView.tsx`, `ShoppingListDetailPage.tsx`, `MealPlanDayEditor.tsx`,
    `WorkoutDayEditor.tsx`. Se verificó con `grep -rn "confirm(" web/src` → **0
    resultados en todo el proyecto** fuera de los diálogos ya mencionados de
    rutina/plan.
  - Mobile: incluso el propio `goal_section.dart:38-41` (perfil) borra directo
    (`onPressed: () => ref.read(profileControllerProvider.notifier)
    .deleteGoal(goal.id)`), sin el `_confirmDelete` + `AlertDialog` que sí usa
    `workout_detail_screen.dart`. Mismo patrón esperable en pathologies/injuries/
    medications/progress entries/photos/shopping list items del lado mobile.
- **Por qué es el hallazgo más importante de esta segunda pasada**: son datos reales
  del usuario sin posibilidad de deshacer — un objetivo, una patología cargada, una
  entrada de peso con semanas de historial, una foto de progreso. Un misclick (muy
  fácil en listas con botones de borrar en cada fila, como `MetricEntriesView`) borra
  sin aviso ni forma de recuperar.
- **Recomendación**: no hay que diseñar nada nuevo — **extender el patrón que ya
  existe** para rutina/plan de alimentación a todos los sub-recursos. En web, lo más
  simple es un componente `ConfirmDeleteDialog` reutilizable (título + mensaje
  parametrizable + acción) que envuelva la lógica ya usada en `WorkoutDetailPage.tsx`,
  para no repetir el `Dialog` a mano en cada uno de los ~10 lugares. En mobile,
  extraer `_confirmDelete` de `workout_detail_screen.dart` a un helper compartido en
  `mobile/lib/core/` y reusarlo en las 4 secciones de perfil + progreso + compras.
  Prioridad **alta** — es el único hallazgo de esta parte con riesgo real de pérdida de
  datos, no solo de fricción.

## 13. Formularios que fallan en silencio

### 13.1 `AddEntryDialog` (registrar métrica de progreso) no muestra error si el valor es inválido — ✅ RESUELTO (2026-08-17)
- **Código**: `AddEntryDialog.tsx:26-28` —
  ```ts
  function handleSubmit() {
    const numericValue = Number(value.replace(',', '.'));
    if (Number.isNaN(numericValue) || !unit.trim()) return;
    ...
  ```
  Si el usuario deja "Valor" vacío o escribe algo no numérico y aprieta "Registrar", la
  función simplemente `return`s — no pasa nada visible. Ni error en el campo, ni toast,
  ni deshabilitar el botón según validez.
- **Por qué es peor que un error normal**: un error visible frustra pero informa; un
  botón que "no hace nada" al clickearlo hace dudar al usuario si el click llegó, si la
  app está trabada, o si algo se guardó. Es el tipo de bug de UX que genera doble-click
  y reportes de "se cuelga".
- **Recomendación**: mostrar `aria-invalid` + mensaje bajo el campo "Valor" cuando no
  es numérico (mismo patrón ya usado en `LoginPage.tsx:57,60` con `errors.email`), o
  como mínimo un `toast.error('Ingresá un valor numérico válido')` en el mismo punto
  donde hoy hay el `return`.

### 13.2 `ProfileForm` convierte texto inválido en `null` sin avisar — ✅ RESUELTO (2026-08-17)
- **Código**: `ProfileForm.tsx:19-24`, función `toNumberOrNull` — si el usuario escribe
  algo no numérico en "Altura (cm)" o "Peso (kg)" y guarda, el valor se transforma
  silenciosamente en `null` (se borra el dato) en vez de bloquear el guardado o avisar.
  Mismo patrón en `toIntOrNull` para días/minutos de entrenamiento.
- **Impacto concreto**: el usuario puede pensar que guardó su peso y en realidad el
  campo quedó vacío en el backend, sin ningún indicio en la UI de que eso pasó (el
  toast dice genéricamente "Perfil actualizado" — `ProfileForm.tsx:70` — que es
  técnicamente cierto pero engañoso sobre qué se guardó).
- **Recomendación**: validar antes de armar el payload y bloquear el guardado con un
  mensaje claro si un campo numérico tiene texto no numérico, en vez de descartarlo
  silenciosamente. Alternativa más simple: usar `type="number"` o restringir el
  `onChange` para que no acepte caracteres no numéricos en primer lugar (ver también
  §16 sobre la inconsistencia de tipos de input numérico).

### 13.3 Indicador de campo obligatorio inconsistente entre formularios
- **Evidencia**: `WorkoutForm.tsx:57` marca el nombre con asterisco (`Nombre *`) y lo
  valida en `handleSave` (`WorkoutForm.tsx:41-44`, con `toast.error`). Ningún otro
  formulario auditado (`ProfileForm`, `AddEntryDialog`, `MealPlanForm` — no leído en
  detalle pero mismo patrón esperable) marca visualmente qué campos son obligatorios.
- **Recomendación**: no es necesario marcar todo con asterisco (la mayoría de los
  campos de este producto son opcionales por diseño — perfil de salud incompleto es un
  estado válido). Alcanza con aplicar el mismo criterio de `WorkoutForm` (asterisco +
  validación con mensaje) a los pocos campos que sí son estrictamente obligatorios en
  cada formulario, en vez de que sea una decisión ad-hoc por desarrollador.

## 14. Inputs de búsqueda (pickers de ejercicios/recetas) — ✅ RESUELTO (2026-08-17)

- **Sin debounce**: `ExercisePickerDialog.tsx:20-21` dispara `useExerciseCatalog` en
  cada tecla presionada (`search` va directo al hook sin debounce). Se verificó que no
  existe ninguna utilidad de debounce en todo el proyecto (`grep -rn "debounce"
  web/src` → 0 resultados). El catálogo es chico hoy (seed de datos de prueba), pero es
  un patrón que escala mal y genera una request HTTP por tecla.
- **Sin botón de limpiar**: el input de búsqueda no tiene una "×" para vaciarlo rápido
  una vez escrito, hay que borrar a mano o cerrar el diálogo.
- **Recomendación**: un hook `useDebouncedValue` simple (300ms) aplicado antes de pasar
  `search` a `useExerciseCatalog`, reusable también en el picker de recetas
  (`RecipePickerDialog.tsx`, mismo patrón esperable sin haberlo confirmado línea por
  línea). Botón de limpiar: ícono `X` clickeable dentro del input cuando
  `search.length > 0`, mismo lugar donde hoy está el ícono `Search` a la izquierda.
  Prioridad media — no es un bug, es pulido de una interacción que se usa seguido
  (elegir ejercicios/recetas es parte central del flujo de armar rutinas/planes).

## 15. Cambios sin guardar se pierden en silencio — ✅ RESUELTO (2026-08-17)

- **Qué pasa**: `WorkoutForm.tsx` y `ProfileForm.tsx` mantienen el borrador en estado
  local (`useState`) sin ningún tracking de "dirty" (¿hay cambios sin guardar?) ni
  advertencia al navegar afuera (no hay `beforeunload` ni guard de router). Si un
  usuario edita varios campos de su perfil o arma una rutina con varios días/ejercicios
  y accidentalmente navega (back del navegador, click en un link del nav) antes de
  guardar, pierde todo sin aviso.
- **Por qué importa más en este producto**: `WorkoutForm` con varios días y ejercicios
  anidados (`WorkoutDayEditor`) puede representar varios minutos de trabajo de carga
  manual — es la pantalla con más para perder de todo el frontend.
- **Recomendación mínima**: un `window.confirm` (o `AlertDialog`) al intentar salir con
  cambios sin guardar es suficiente — no hace falta autosave ni nada más sofisticado
  para el alcance actual. Requiere trackear un flag `isDirty` (comparar contra el draft
  inicial, o simplemente marcar `true` en el primer `onChange`) y un listener de
  `beforeunload` + interceptar la navegación de React Router
  (`useBlocker`/`unstable_usePrompt`, disponible en la versión de `react-router-dom`
  instalada). Prioridad media-alta específicamente para `WorkoutForm` y `MealPlanForm`
  por ser los formularios más largos; menor urgencia en `ProfileForm` (guarda todo el
  formulario de una — el riesgo es el mismo pero la sesión de edición suele ser más
  corta).

## 16. Dos patrones distintos de input numérico conviviendo sin criterio — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `WorkoutForm.tsx:85` usa `type="number"` nativo (con las flechitas de
  incremento/decremento del navegador) para "Duración (semanas)", mientras
  `ProfileForm.tsx:87,91,114,124` y `AddEntryDialog.tsx:53` usan `inputMode="decimal"`/
  `"numeric"` sobre un `<input>` de tipo texto (sin flechitas, pero con teclado
  numérico correcto en mobile). Son dos soluciones válidas al mismo problema, pero
  mezcladas sin un criterio documentado de cuándo usar cada una.
- **Por qué importa**: `type="number"` nativo tiene problemas conocidos de UX (permite
  scrollear el valor por accidente con la rueda del mouse si el input tiene foco,
  formato de flechas inconsistente entre navegadores, no permite bien decimales con
  coma en locales que la usan) — es probable que el patrón `inputMode` usado en el
  resto del proyecto sea el que se eligió a propósito y `WorkoutForm` haya quedado
  como excepción sin querer.
- **Recomendación**: unificar a un solo patrón (recomendado: `inputMode` + validación
  Zod/manual, como ya hace la mayoría) y documentarlo como convención en un comentario
  corto en `web/src/components/ui/input.tsx` o en este mismo documento, para que no
  vuelva a mezclarse en formularios nuevos.

## 17. Foco automático (autofocus) aplicado solo en diálogos, no en páginas completas — ✅ RESUELTO EN WEB (2026-08-17)

- **Evidencia**: `ExercisePickerDialog.tsx:36` y `GenerateWorkoutDialog.tsx:62` usan
  `autoFocus` en su input/textarea principal (correcto — son diálogos modales, tiene
  sentido enfocar de inmediato). `LoginPage.tsx` y `RegisterPage.tsx` **no** enfocan el
  campo Email al cargar la página — el usuario tiene que clickear antes de poder
  escribir.
- **Recomendación**: agregar `autoFocus` al primer campo (`Email`) de `LoginPage.tsx` y
  `RegisterPage.tsx`. Es una mejora barata y estándar en pantallas de login (el primer
  campo casi siempre se enfoca solo). No aplicar el mismo criterio a formularios largos
  como `ProfileForm`/`WorkoutForm` — ahí autofocar el primer campo no aporta (el
  usuario entra a leer/navegar, no a escribir de inmediato).

## 18. Generación con IA: no se puede cancelar una vez iniciada — ✅ RESUELTO EN WEB (2026-08-17)

- **Complementa el hallazgo de §3.1** (loading state sin progreso) con un problema
  adicional encontrado al leer el código del diálogo: `GenerateWorkoutDialog.tsx:42,66`
  deshabilita tanto el cierre del diálogo (`!generateWorkout.isPending && setOpen`)
  como el botón "Cancelar" (`disabled={generateWorkout.isPending}`) mientras la
  generación está en curso. Con una duración real documentada de hasta 2m32s
  (`docs/00-progress.md:719`), el usuario queda **atrapado en el diálogo sin forma de
  salir** durante ese tiempo, incluso si se arrepiente o lo abrió por error.
- **Por qué se deshabilitó así (hipótesis razonable)**: probablemente para evitar que
  el usuario cierre el diálogo mientras la mutación sigue en curso y pierda el
  resultado sin darse cuenta. Es una razón válida, pero la solución actual (bloquear la
  salida completamente) es más agresiva de lo necesario.
- **Recomendación**: permitir cerrar/cancelar el diálogo en cualquier momento; si la
  request ya está en curso, dejarla completarse en background (React Query ya sostiene
  la mutation independientemente del diálogo) y mostrar el resultado vía toast +
  navegación cuando termine, en vez de forzar al usuario a esperar mirando el diálogo
  abierto. Esto es una mejora de flujo, no solo de copy — depende de que la mutation no
  esté atada al ciclo de vida del componente del diálogo (verificar con quien
  implemente si `useGenerateWorkout` ya sobrevive al unmount o si hay que ajustarlo).

## 19. Roadmap actualizado (Parte 1 + Parte 2)

**Quick wins nuevos de esta parte (baratos, sin dependencias, sin bloqueos):**
1. ✅ **Mostrar/ocultar contraseña** en los 4 campos de contraseña de ambas plataformas
   (§11.1) — el hallazgo que disparó esta auditoría. RESUELTO EN WEB 2026-08-17
   (`web/src/components/ui/password-input.tsx`). Falta Flutter.
2. ✅ Confirmar contraseña en el registro web, portando el patrón ya resuelto en mobile
   (§11.2) — RESUELTO 2026-08-17 (`authSchemas.ts` + `RegisterPage.tsx`).
3. ✅ Texto de ayuda visible ("Mínimo 8 caracteres") en vez de solo error post-submit
   (§11.3) — RESUELTO EN WEB 2026-08-17.
4. ✅ `autoFocus` en el campo Email de login/registro (§17) — RESUELTO EN WEB 2026-08-17.
5. ✅ Mostrar error visible en vez de fallar en silencio en `AddEntryDialog` (§13.1) —
   RESUELTO 2026-08-17.

**Prioridad alta — riesgo real de pérdida de datos:**
6. ✅ Extender el diálogo de confirmación de borrado (ya resuelto para
   rutina/plan de alimentación) a **todos** los sub-recursos con borrado directo:
   objetivos, patologías, lesiones, medicación, entradas de progreso, fotos, ítems de
   lista de compras, comidas del diario (§12.1). RESUELTO EN WEB 2026-08-17
   (`web/src/components/ConfirmDeleteDialog.tsx`, cableado en los 8 lugares
   identificados). Falta Flutter — mismo patrón que `workout_detail_screen.dart`
   extendido a `goal_section.dart` y equivalentes.

**Medio — mejoras de robustez/consistencia, requieren un poco más de trabajo:**
7. ✅ Validación en vez de conversión silenciosa a `null` en campos numéricos de
   `ProfileForm` (§13.2) — RESUELTO 2026-08-17.
8. ✅ Debounce + botón de limpiar en los inputs de búsqueda de los pickers (§14) —
   RESUELTO 2026-08-17 (`web/src/core/hooks/useDebouncedValue.ts`).
9. ✅ Advertencia de cambios sin guardar en `WorkoutForm`/`MealPlanForm` (§15) —
   RESUELTO EN WEB 2026-08-17 (`web/src/core/hooks/useUnsavedChangesGuard.ts`).
10. ✅ Unificar el patrón de input numérico (`type="number"` vs `inputMode`) (§16) —
    RESUELTO 2026-08-17.
11. ✅ Permitir cancelar/cerrar el diálogo de generación con IA sin esperar a que
    termine (§18) — RESUELTO EN WEB 2026-08-17 (la mutation sigue en background,
    resultado vía toast + navegación al terminar).

Todo lo de arriba está hecho **en web únicamente** — mobile no se tocó en esta ronda
(no hay entorno Flutter disponible para verificar compilación desde este chat). Queda
como paridad pendiente junto con el resto del §2.1.

**Backlog — requiere infraestructura o decisión de producto, no es solo UI:**
12. Flujo de recuperación de contraseña ("¿Olvidaste tu contraseña?") — necesita envío
    de emails y endpoint backend nuevo (§11.4).
