# KineticOs — App Flutter

## Estado

**Fase 3 completa.** Implementado: `core` (tema, router con guarda de auth, cliente
HTTP, storage seguro), `auth` (login/registro), `profile` (perfil de salud completo),
`workout` (catálogo, rutinas, sesiones de entrenamiento), `nutrition` (recetas, planes
de alimentación, diario alimentario, listas de compra) y `progress` (métricas de
seguimiento, fotos de progreso). `admin` y `ai` no tienen pantalla propia — son
gobernanza interna y base técnica sin UI de usuario final (deliberado, no pendiente).

Las carpetas de plataforma (`android/`, `web/`, `linux/`) ya están generadas.

## Requisitos

- **Flutter SDK 3.44.x** (stable). Si no lo tenés instalado:
  ```bash
  # Sin sudo, instalación manual (evita conflictos con snap/apt):
  curl -o flutter.tar.xz https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.44.9-stable.tar.xz
  mkdir -p ~/development && tar xf flutter.tar.xz -C ~/development
  export PATH="$HOME/development/flutter/bin:$PATH"   # agregalo a tu ~/.bashrc
  ```
- **Backend corriendo** (ver raíz del repo `README.md` / `docs/00-progress.md`):
  ```bash
  docker compose up -d postgres
  cd backend && ./gradlew flywayMigrate
  ./gradlew :app:bootRun --args='--server.port=8081'
  ```

## Arrancar la app (web — más simple para probar sin emulador)

```bash
cd mobile
flutter pub get
flutter run -d web-server --web-port=5050
```

Abrí `http://localhost:5050` en el navegador.

> **Importante — CORS:** el backend solo acepta requests desde los orígenes listados en
> `CORS_ALLOWED_ORIGINS` (`.env` de la raíz). Ya incluye `http://localhost:5050`. Si usás
> otro puerto (`flutter run -d chrome` sin `--web-port` elige uno al azar), agregalo a esa
> variable y reiniciá el backend, o corré siempre con `--web-port=5050`.

### Otras plataformas

```bash
flutter run -d chrome                 # Chrome nativo (requiere agregar su puerto a CORS)
flutter run -d linux                  # Desktop Linux (requiere: apt install clang cmake ninja-build pkg-config)
flutter run                           # Android, con un emulador/dispositivo conectado
                                       # (el backend debe verse en 10.0.2.2:8081 desde el emulador,
                                       #  no localhost — pasar API_BASE_URL, ver abajo)
```

Para apuntar a otra URL de API:
```bash
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8081/api/v1
```

## Probar el flujo completo (manual)

1. **Registrate**: pantalla de login → "¿No tenés cuenta? Registrate" → completá email +
   contraseña (mínimo 8 caracteres) + confirmación → "Crear cuenta". Deberías caer en la
   pestaña "Rutinas" (login automático tras registrarse).
2. **Perfil**: pestaña "Perfil" → completá sexo/altura/peso/nivel/etc. → "Guardar perfil".
   Probá agregar un objetivo/patología/lesión/medicación desde las secciones expandibles.
3. **Crear una rutina**: pestaña "Rutinas" → botón `+` → nombre, objetivo, nivel →
   "Agregar día" → dentro del día, "Agregar ejercicio" (buscá en el catálogo, ej.
   "Sentadilla") → completá series/reps → "Crear rutina".
4. **Entrenar**: en el detalle de la rutina, "Iniciar" sobre un día → se abre la sesión →
   "Registrar ejercicio" (elegí uno del catálogo, cargá series/peso/reps/RPE) →
   "Completar sesión" (con duración en minutos y notas).
5. **Historial**: pestaña "Rutinas" → "Historial" → la sesión completada debería listarse.
6. **Plan de alimentación**: pestaña "Nutrición" → "Planes" → botón `+` → nombre, fechas →
   "Agregar día" → dentro del día, "Agregar comida" (buscá una receta, ej. "Avena") →
   elegí tipo de comida y porciones → "Crear plan".
7. **Diario**: pestaña "Nutrición" → "Diario" → "Registrar comida" → elegí una receta
   (las macros se auto-completan según porciones, editables) → "Registrar". Debería
   sumarse al total del día.
8. **Lista de compras**: pestaña "Nutrición" → "Compras" → `+` para crear una lista →
   entrá y agregá ítems → tocá el checkbox para marcarlos comprados.
9. **Métricas de progreso**: pestaña "Progreso" → "Métricas" → `+` → elegí una métrica
   (ej. Peso) → cargá valor y unidad (se auto-completa) → "Registrar". Probá filtrar
   con los chips de arriba.
10. **Fotos de progreso**: pestaña "Progreso" → "Fotos" → botón de cámara → pegá una
    URL de imagen pública (ej. `https://picsum.photos/400`) → elegí ángulo → "Agregar".
11. **Logout**: ícono de salida en el AppBar de "Perfil" → debería volver a la pantalla
    de login.

Si algo falla, revisá la consola del navegador (F12) y que el backend esté corriendo en
el puerto que espera `API_BASE_URL` (por defecto `http://localhost:8081/api/v1`).

## Tests y análisis estático

```bash
flutter analyze   # debe dar "No issues found!"
flutter test      # smoke test: la app arranca y muestra el login sin sesión guardada
```

## Estructura

```
mobile/lib/
├── main.dart                 # punto de entrada (ProviderScope)
├── app/app.dart               # MaterialApp.router + GoRouter
├── core/
│   ├── theme/                # ★ PUNTO ÚNICO DEL DISEÑO ★ (colores/tipografía/spacing)
│   ├── router/                # GoRouter, guarda de auth, shell de navegación inferior
│   ├── network/                # Dio + interceptor JWT/refresh + ApiException
│   ├── storage/                # TokenStorage (flutter_secure_storage)
│   └── providers/               # DI transversal (Riverpod)
└── features/
    ├── auth/       # login, registro, sesión
    ├── profile/    # perfil de salud + objetivos/patologías/lesiones/medicación
    ├── workout/    # catálogo, rutinas, sesiones de entrenamiento
    ├── nutrition/  # recetas, planes de alimentación, diario, listas de compra
    └── progress/   # métricas de seguimiento (peso, medidas...) + fotos de progreso
    # cada feature: domain/ application/ infrastructure/ presentation/
```

## Decisiones de arquitectura (para quien continúe)

- **Manejo de estado: solo Riverpod** (providers + `Notifier`/`AsyncNotifier`). El
  `pubspec.yaml` declara también `flutter_bloc`/`bloc_test` pero no se usan en el código:
  mezclar dos soluciones de estado no aportaba valor en este alcance. Si preferís Bloc en
  features nuevas, quitá la dependencia no usada o adoptalo de forma consistente.
- **Sin Freezed/build_runner**: los modelos de dominio (`domain/*.dart`) son clases Dart
  simples con `fromJson`/`toJson` manuales. Evita depender del generador de código en
  cada cambio de modelo; `freezed` sigue declarado en `pubspec.yaml` por si se decide
  adoptarlo más adelante.
- **GoRouter con rutas hash** (`http://localhost:5050/#/workouts`, default en web). El
  guardado de sesión (`redirect`) reacciona a `authControllerProvider` vía
  `GoRouterRefreshNotifier` — Riverpod 3.x no tiene `ChangeNotifierProvider`, así que es
  un `Provider` normal que expone un `ChangeNotifier` construido a mano
  (`core/router/go_router_refresh_notifier.dart`).
- **Estrategia "replace" al editar rutinas**: igual que el backend, `WorkoutFormScreen`
  reemplaza todos los días/ejercicios al guardar (no hay edición incremental de un solo
  ejercicio dentro de una rutina existente).

## Cambiar el diseño

Edita `lib/core/theme/` (colores, tipografía, espaciado). La app se re-tematiza sola.
