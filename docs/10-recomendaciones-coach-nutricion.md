# Recomendaciones desde la perspectiva de entrenador + nutricionista — MiCoach

> **Para el chat de desarrollo.** Este documento es el resultado de probar la app
> corriendo localmente (backend real + Postgres + Ollama), poniéndose en el rol de un
> **entrenador deportivo con formación en diseño/planificación de entrenamiento y
> asesoramiento nutricional**, no en el rol de diseñador UI. Es complementario a
> `docs/06-ux-ui-audit.md` (que audita UX/UI genérico: formularios, estados de carga,
> accesibilidad) — acá el foco es **si el contenido y los datos que la app genera y
> muestra son correctos y útiles desde el punto de vista de entrenamiento/nutrición
> real**, no solo si la interfaz es prolija.
>
> **Metodología**: se registró un usuario de prueba (`coach.review@micoach.dev`), se
> completó un perfil realista (masculino, 31 años, 178cm, 82kg, actividad moderada,
> intermedio, objetivo "ganar músculo", 4 días/semana), se generaron una rutina y un
> plan de alimentación con IA (Ollama local, `llama3.2:1b`), se inspeccionó el modelo de
> datos en Postgres para verificar qué información existe pero no se muestra, y se leyó
> el código fuente (`web/src/features/*`, `backend/modules/*`) para confirmar cada
> hallazgo contra archivo y línea real, no contra suposiciones.
>
> **Nota de contexto de la prueba**: el proveedor de IA activo en la base local
> (`ai_provider_configs`) era Groq con el modelo `llama-3.3-70b-versatile`, que ya no
> existe (`model_not_found` — ver hallazgo A.2). Se cambió manualmente a Ollama local
> para poder completar la prueba end-to-end; queda así en el entorno local. **En
> producción (Render) esto probablemente esté roto de la misma manera** — revisar
> `ai_provider_configs` en Supabase antes de asumir que la generación con IA funciona.

---

## A. Bugs críticos — funcionalidad rota, no solo mejorable

### A.1 — Generación de plan de alimentación con IA falla y no queda registro del error real — ✅ PARCIALMENTE RESUELTO (2026-08-17)

- **Reproducido**: pedido "Plan para ganar masa muscular, 3 días, alto en proteína, con
  4 comidas por día" contra Ollama local → `502 {"code":"INTERNAL_ERROR","message":"La
  IA no devolvió un JSON válido"}` tras ~60s de espera. Confirmado con curl directo
  contra el backend, sin pasar por el navegador, para descartar timeout de cliente.
- **Causa**: `NutritionAiGenerator.parseJson()` / `extractFirstJsonObject()`
  (`backend/modules/nutrition/src/main/java/com/micoach/nutrition/application/service/NutritionAiGenerator.java:81-90`)
  no logra extraer un JSON válido de la respuesta del modelo. Es la misma clase de
  problema que ya se encontró y se corrigió para `workout` (ver
  `docs/00-progress.md` § Fase 4, "Extracción de JSON frágil ante texto extra del
  modelo") — pero el fix de `workout` (`WorkoutAiGenerator.extractFirstJsonObject`, el
  mismo algoritmo de llaves balanceadas) **también existe acá y también falla**, lo que
  sugiere que con modelos chicos (`llama3.2:1b`) el prompt de `meal_plan_generator` es
  más propenso a que el modelo devuelva texto extra o JSON incompleto que el de
  `workout_generator`. No alcanza con "ya lo arreglamos en workout" — el prompt de
  nutrición necesita el mismo nivel de robustez o un ajuste propio.
- **Problema adicional, más grave que el fallo en sí**: `ai_generation_logs` registra
  la llamada a la IA como `status = success` (el modelo sí respondió, solo que el texto
  no se pudo parsear) — es decir, **la auditoría dice "éxito" en un flujo que terminó en
  error para el usuario**. Y el error en sí **no se loguea en el backend**:
  `GlobalExceptionHandler.handleDomain()`
  (`backend/app/src/main/java/com/micoach/app/web/GlobalExceptionHandler.java:25-29`)
  traduce la `DomainException` a un `ApiError` pero no llama a `log.error`/`log.warn` —
  a diferencia de `handleGeneric()`, que sí loguea. Resultado: si esto pasa en
  producción, no queda ningún rastro en los logs del servidor para diagnosticarlo —
  solo lo que vio el usuario en pantalla.
- **Qué hacer**:
  1. ✅ **HECHO** — `GlobalExceptionHandler.handleDomain()` ahora hace `log.warn` con
     código/mensaje/path antes de responder.
  2. ⬜ **Pendiente** — revisar/reforzar el prompt `meal_plan_generator` con corridas
     reales contra `llama3.2:1b`. No se pudo hacer desde este chat (no hay Ollama
     corriendo en este entorno) — queda para una sesión con el stack local levantado.
  3. ✅ **HECHO** — `ai_generation_logs.status` ahora distingue "la IA respondió pero
     no se pudo usar" (`partial`) de un éxito real. Implementado en
     `AiUseCase.markGenerationPartial(logId)` (nuevo método) + `AiService` +
     `AiRepository`/`GenerationLogJpaRepository.updateStatus()`, y cableado en los 3
     generadores que parsean JSON de la IA (`WorkoutAiGenerator`,
     `NutritionAiGenerator`, `NutritionSubstitutionAiGenerator` — este último no
     estaba en el hallazgo original pero tenía el mismo bug, mismo fix).

### A.2 — Modelo de Groq desactualizado: la IA está completamente rota si Groq es el proveedor activo — ✅ RESUELTO (2026-08-17)

- **Reproducido**: con Groq activo (`ai_provider_configs.id = 2`,
  `model = llama-3.3-70b-versatile`), **cualquier generación con IA falla** (rutinas y
  planes de alimentación) con `model_not_found` — Groq deprecó/renombró ese modelo.
  Esto rompe la funcionalidad más diferenciada del producto (según el propio README)
  para cualquier usuario que use el proveedor que está activo por defecto según
  `docs/09-deployment.md` § 5 (recomienda activar Groq o Gemini en producción porque
  Ollama no corre en el free tier de Render).
- **Qué hacer**:
  1. ✅ **HECHO** — verificado en vivo contra `console.groq.com/docs/models` (2026-08):
     Groq deprecó por completo los modelos Llama de su tier de producción (no es un
     rename, es que ya no están). Los modelos de propósito general vigentes hoy son
     `openai/gpt-oss-120b` y `openai/gpt-oss-20b`. Migración
     `V19__fix_groq_model.sql` (`UPDATE ai_provider_configs SET model =
     'openai/gpt-oss-120b' WHERE provider = 'groq'`) — se eligió el de 120b sobre el
     de 20b por mejor calidad de JSON estructurado (relevante para A.1), ya que Groq
     corre en hardware propio (LPU) y la latencia extra del modelo más grande es
     mínima.
  2. **Falso positivo, ya existía** — el botón "probar conexión" que pedía este ítem
     ya está implementado: `AiProviderCard.tsx` tiene un botón (ícono `Zap`) cableado
     a `useTestAiProvider()` → `AiService.testProvider()`, que hace una llamada
     mínima real al proveedor. No hizo falta construir nada.

### A.3 — Timeout del cliente HTTP (60s) más corto que la duración real de una generación con IA — ❌ FALSO POSITIVO, verificado (2026-08-17)

- **Evidencia**: `web/src/core/api/client.ts:36` fija `timeout: 60000` (60s) en el
  cliente Axios global. Pero una generación real con Ollama tomó **60906ms** (plan de
  alimentación, medido en `ai_generation_logs.duration_ms`) y hasta **2m32s** en
  pruebas anteriores documentadas (`docs/00-progress.md:719`, rutina de fuerza). Los
  diálogos de generación (`GenerateWorkoutDialog.tsx`, `GenerateMealPlanDialog.tsx`)
  avisan explícitamente "puede tardar hasta 3 minutos" — pero el cliente HTTP corta a
  los 60s de todos modos.
- **Por qué importa**: en el mejor de los casos el usuario ve un error de timeout
  aunque el backend termine generando el contenido igual (trabajo perdido de vista, el
  usuario no sabe que en realidad se creó). En el peor caso (como en A.1), el timeout
  del cliente se solapa con un fallo real del backend y hace más difícil distinguir
  "tardó demasiado" de "falló de verdad".
- **Verificado contra el código real**: `workoutApi.ts:44` y `nutritionApi.ts:50` ya
  pasan `{ timeout: 180000 }` puntual en esas dos llamadas — este override ya existía
  desde antes de que el timeout global subiera a 60s (ver comentario en
  `nutritionApi.ts`: *"Igual que workoutApi.generateWorkout: la IA local... tarda
  bastante más que una llamada normal"*). No hacía falta ningún cambio — el hallazgo
  parece haberse basado solo en `client.ts` sin revisar los call sites específicos.

---

## B. Perfil y línea base nutricional — falta el cálculo que sostiene todo lo demás

### B.1 — El TDEE (gasto calórico diario) nunca se calcula, aunque todos los datos para hacerlo ya están cargados — ✅ RESUELTO (2026-08-17)

- **Evidencia**: el perfil (`UserProfile`) ya captura `sex`, `birthDate`, `heightCm`,
  `weightKg` y `activityLevel` — exactamente lo que pide la fórmula estándar de
  Mifflin-St Jeor (o Harris-Benedict) para estimar BMR/TDEE. El campo
  `tdeeCalories` existe en el modelo y en la BD, y **la IA de nutrición ya lo usa
  cuando está presente** (`NutritionAiGenerator.buildProfileText()`,
  `backend/modules/nutrition/src/main/java/com/micoach/nutrition/application/service/NutritionAiGenerator.java:63-65`:
  *"Gasto calórico diario estimado (TDEE): ... kcal"*). Pero **no hay ningún lugar en
  el código, backend o frontend, donde `tdeeCalories` se calcule** — solo se puede
  setear a mano vía `PUT /users/me/profile`, y no hay ningún campo en `ProfileForm.tsx`
  para cargarlo manualmente tampoco. En la práctica, **siempre queda `null`** y la IA
  arma los planes de alimentación sin ninguna base calórica real del usuario, solo con
  el objetivo dietario como texto libre.
- **Por qué es el hallazgo más importante de todo este documento**: sin un TDEE
  estimado, "plan para ganar masa muscular" o "plan para bajar de peso" son frases sin
  ningún ancla numérica — un nutricionista real nunca arma un plan sin antes calcular
  (aunque sea una estimación) cuántas calorías necesita esa persona. Hoy la IA
  literalmente adivina.
- **Qué hacer**:
  1. Calcular BMR con Mifflin-St Jeor en el backend cuando el perfil tiene sexo, fecha
     de nacimiento, altura y peso completos (server-side, en `UserProfileService` o al
     vuelo al pedir el perfil): 
     `BMR = 10×peso(kg) + 6.25×altura(cm) − 5×edad + (5 si masculino, −161 si
     femenino)`. Multiplicar por el factor de `activityLevel` (sedentario ×1.2, ligera
     ×1.375, moderada ×1.55, activo ×1.725, muy activo ×1.9 — factores estándar de
     Harris-Benedict/Mifflin) para obtener el TDEE.
  2. Ajustar según `dietaryGoal`: TDEE − ~500 kcal para "perder grasa", TDEE + ~300-500
     kcal para "ganar músculo", TDEE para "mantener"/"salud general".
  3. Mostrar el resultado en el perfil (aunque sea de solo lectura, "Tu gasto calórico
     estimado: ~2400 kcal/día") y usarlo como default de `tdeeCalories` en vez de
     dejarlo en `null` — sin bloquear que el usuario lo pise a mano si ya conoce su
     propio número (de un estudio de composición corporal, por ejemplo).
  4. Una vez calculado, se vuelve la base para B.2 y C.1 más abajo — varios hallazgos
     de este documento dependen de que este número exista.

### B.2 — El IMC no se calcula ni se muestra en el perfil — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `ProfileForm.tsx` captura altura y peso pero no calcula ni muestra el
  IMC en ningún lado de esa pantalla. La única forma de tener un IMC en la app es
  cargarlo **a mano** como una métrica más en Progreso (`metricType: "bmi"`,
  `progressLabels.ts:4`) — un cálculo que la propia app podría hacer automáticamente a
  partir de datos que el usuario ya cargó, y en cambio le pide cargarlo él mismo por
  separado.
- **Qué hacer**: mostrar el IMC calculado (`peso / altura²`) directamente en la tarjeta
  de datos físicos del perfil, actualizado en vivo mientras se edita altura/peso.
  Opcionalmente, seguir permitiendo el registro manual en Progreso para quien lo mide
  con otro método (bioimpedancia, etc.), pero que el perfil no dependa de esa carga
  manual para mostrar el número más básico de todos.

---

## C. Calidad del contenido generado por IA (entrenamiento)

### C.1 — La rutina generada por IA es incoherente: los nombres de los días no corresponden a los ejercicios asignados — 🟡 PARCIAL (2026-08-17, mínimo viable)

- **Reproducido y verificado contra el catálogo real** (pedido: "Rutina de hipertrofia,
  4 días a la semana, nivel intermedio, con mancuernas, barra y banco, enfocada en tren
  superior e inferior balanceado"). Resultado real generado:

  | Día | Ejercicio asignado | Músculo primario real (según `workout_exercise_muscles`) | ¿Coherente con el nombre del día? |
  |---|---|---|---|
  | **Push** | Dominadas | Dorsales (+ romboides, bíceps) | ❌ Dominadas es un ejercicio de **tracción (pull)**, no de empuje |
  | **Pull** | Burpees | Cuádriceps (+ pectoral, abdominales) | ❌ Burpees es full-body/cardio, no de tracción |
  | **Piernas** | Crunches abdominales | Abdominales | ❌ Es un ejercicio de core, no trabaja piernas en absoluto |

  Además, **cada día tiene un solo ejercicio** — ninguna rutina de hipertrofia real (ni
  de ningún objetivo) se arma con 1 ejercicio por día; un día de "Push" necesita como
  mínimo un compuesto (press banca/militar) y 2-3 accesorios (aperturas, elevaciones,
  extensión de tríceps) para tener sentido como sesión de entrenamiento.
- **Por qué pasa esto, con evidencia concreta**: el catálogo **sí tiene** la
  información necesaria para evitar este error —
  `workout_exercise_muscles(exercise_id, muscle_id, role)` con roles
  `primary`/`secondary`/`stabilizer` está poblada para los 106 ejercicios del seed. El
  generador (`WorkoutAiGenerator`, ver referencia en `docs/00-progress.md` § Fase 4)
  resuelve el nombre que devuelve el modelo contra el catálogo real (evita ejercicios
  inventados), pero **no valida que el ejercicio elegido tenga sentido para el tipo de
  día** — confía ciegamente en que el modelo de 1-3B parámetros entienda qué es "Push"
  vs "Pull" vs "Piernas", cosa que en la práctica no hace de forma confiable.
- **Qué hacer** (de más simple/barato a más robusto):
  1. ✅ **HECHO 2026-08-17** — `WorkoutAiGenerator` rechaza (502, pide reintentar) un
     día no-descanso con menos de 3 ejercicios resueltos, y marca el log de generación
     como `partial`. Ya no se guarda una rutina de 1 ejercicio por día.
  2. **Validación de coherencia**: usar el catálogo de `workout_exercise_muscles` para
     mapear cada nombre de día común (Push/Pull/Piernas/Full body/etc.) a un set
     esperado de grupos musculares, y filtrar o re-pedir cuando el ejercicio elegido no
     matchea ninguno de esos grupos para ese tipo de día. No hace falta que sea
     perfecto — incluso una validación laxa (¿al menos 1 de los ejercicios del día toca
     el grupo muscular principal esperado?) mejoraría mucho el resultado actual.
  3. **A mediano plazo**: pasarle al prompt la lista de músculos por ejercicio (no solo
     el nombre) para que el modelo tenga esa info al elegir, en vez de que la
     validación sea puramente posterior — hoy el catálogo que se le manda a la IA
     probablemente solo incluye nombres (a confirmar en el prompt real de
     `workout_generator` en la tabla `ai_prompts`).
  4. Vale la pena probar con un modelo más grande (`llama3.2` sin el sufijo
     `:1b`, o el 3B que ya está descargado según `docs/00-progress.md`) antes de asumir
     que el problema es solo de validación — un modelo más grande puede resolver parte
     de esto solo, pero la validación server-side sigue siendo necesaria como red de
     seguridad (nunca hay que confiar 100% en que el LLM entienda anatomía).

### C.2 — El detalle de un ejercicio no muestra qué músculo trabaja — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `ExerciseDetailDialog.tsx:29-38` muestra categoría, dificultad y
  equipamiento, pero no consume `workout_exercise_muscles` para mostrar "Trabaja:
  Dorsales (primario), Bíceps (secundario)". El dato está en la base (confirmado en
  C.1) pero no llega a la UI.
- **Por qué importa para el usuario final, no solo para depurar C.1**: cualquier
  persona entrenando con un mínimo de criterio quiere saber qué está trabajando cada
  ejercicio — es información básica esperable en cualquier app de entrenamiento seria,
  independientemente del bug de coherencia de C.1.
- **Qué hacer**: exponer `muscles` en el DTO de ejercicio si no está ya (el tipo
  `Exercise` en `workoutTypes.ts:32` ya tiene `muscles: ExerciseMuscle[]`, así que
  probablemente **ya viaja en la respuesta de la API** y solo falta renderizarlo en
  `ExerciseDetailDialog.tsx`) — agregar una sección "Músculos trabajados" con badges
  (primario destacado, secundarios en un tono más apagado).

### C.3 — Catálogo de ejercicios sin ningún video de demostración

- **Evidencia**: de 106 ejercicios en el catálogo, **0 tienen `video_url` cargado**
  (104 sí tienen imagen, 106 tienen instrucciones de texto). `ExerciseDetailDialog.tsx`
  ya tiene la UI lista para mostrar un link "Ver video de demostración" — el gap es
  puramente de contenido, no de código.
- **Por qué importa**: para ejercicios técnicos (sentadilla, peso muerto, press
  militar) una foto estática e instrucciones de texto no alcanzan para que alguien sin
  experiencia previa aprenda la técnica correctamente — es donde más lesiones se
  producen por mala ejecución. Un video corto (o al menos un GIF) es el estándar mínimo
  esperable en cualquier app de entrenamiento.
- **Qué hacer**: no es una tarea de código — es carga de contenido. Priorizar los
  ejercicios compuestos de mayor riesgo técnico primero (sentadilla, peso muerto, press
  banca, press militar, remo con barra, dominadas) en vez de intentar cubrir los 106 de
  una. Videos de YouTube de dominio público/licencia abierta o grabaciones propias
  cortas (10-15s) alcanzan — no hace falta producción profesional para el alcance
  actual del proyecto.

---

## D. Visualización de datos de nutrición

### D.1 — El diario alimentario no muestra ningún objetivo de referencia, solo lo consumido — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `DailyIntakeView.tsx:36-43` calcula y muestra
  `{calorías} kcal · P {}g · C {}g · G {}g` del día, pero no hay ningún target contra
  el cual comparar ese número — ni una barra de progreso, ni un "de 2400 kcal", ni color
  que indique si está por debajo/dentro/por encima del objetivo.
- **Por qué importa**: "hoy comiste 1800 kcal" sin saber si el objetivo era 1800, 2200 o
  2800 no le sirve a nadie para tomar una decisión — es el equivalente a mostrarle a
  alguien su ritmo cardíaco sin decirle cuál es su zona objetivo. Este hallazgo está
  directamente encadenado a B.1 (sin TDEE calculado, no hay contra qué comparar).
- **Qué hacer**: una vez resuelto B.1, mostrar en `DailyIntakeView` una barra o anillo
  de progreso "1800 / 2400 kcal" y lo mismo para macros si el plan activo define
  targets de proteína/carbos/grasa (ver D.3). No es necesario que el usuario configure
  nada manualmente si B.1 ya está resuelto — el target puede salir del TDEE ajustado
  por objetivo.

### D.2 — El detalle de una receta no muestra macros, solo calorías — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `RecipeDetailDialog.tsx:38` solo renderiza
  `{recipe.caloriesPerServing} kcal/porción` como badge. El tipo `Recipe`
  (`nutritionTypes.ts:35-53`) **ya tiene** `proteinPerServing`, `carbsPerServing`,
  `fatPerServing` y `fiberPerServing` disponibles — el dato llega de la API, solo no se
  renderiza.
- **Por qué importa específicamente en este producto**: la IA arma planes con pedidos
  como "alto en proteína" (probado en este mismo testeo), pero el usuario no tiene
  forma de **verificar** cuánta proteína tiene realmente cada receta del plan que le
  armaron — tiene que confiar ciegamente en la IA sin poder auditar el resultado.
- **Qué hacer**: agregar 3-4 badges más junto al de calorías (Proteína/Carbos/Grasa, y
  fibra si hay espacio) en `RecipeDetailDialog.tsx` — cambio chico, el dato ya está
  disponible en el objeto `recipe` que el componente ya recibe.

### D.3 — El plan de alimentación no desglosa macros por día, solo calorías totales del plan — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `MealPlanDetailPage.tsx:90` muestra un único badge
  `{plan.targetCalories} kcal/día` a nivel de plan completo, pero no hay ningún total de
  proteína/carbos/grasa por día (agregando las comidas de cada `day.meals`), aunque el
  dato para calcularlo está disponible vía D.2 una vez resuelto.
- **Qué hacer**: sumar los macros de las comidas de cada día (mismo patrón que ya usa
  `DailyIntakeView.tsx:23-31` para el diario, se puede reutilizar la lógica) y
  mostrarlos junto al nombre de cada día en `MealPlanDetailPage.tsx` — útil para
  comparar de un vistazo si todos los días del plan están parejos o si alguno se va
  muy lejos del objetivo.

---

## E. Ejecución de la sesión de entrenamiento — el momento real de entrenar

### E.1 — Durante la sesión no se muestra lo que la rutina planificó (series, reps, descanso, tempo) — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `WorkoutSession` (`workoutTypes.ts:172-180`) tiene `workoutDayId`, que
  referencia al `WorkoutDay` planificado — y cada `PlannedExercise`
  (`workoutTypes.ts:42-52`) tiene `sets`, `repsMin`/`repsMax`, `restSeconds`,
  `intensityPercent` y `tempo` ya cargados desde que se armó la rutina. Pero
  `SessionPage.tsx` (la pantalla que se usa **mientras se entrena**) nunca busca ni
  muestra esos datos: el botón "Registrar ejercicio" abre `ExercisePickerDialog` desde
  cero (mismo picker que se usa para armar una rutina nueva, sin ningún filtro ni
  pre-selección basada en el día planificado) y `LogExerciseDialog`
  (`SessionPage.tsx:93-148`) arranca con todos los campos vacíos, sin mostrar en ningún
  lado "planificado: 3 series de 8-12 reps, descanso 90s".
- **Por qué es el hallazgo más importante de esta sección**: esto rompe el propósito
  central de tener una rutina planificada. El usuario arma (o genera con IA) una rutina
  con series/reps/descanso pensados para su objetivo, pero al momento de entrenar de
  verdad tiene que **acordarse de memoria** qué le tocaba hacer y cuánto, porque la
  pantalla de sesión no se lo muestra — literalmente tiene que tener la rutina abierta
  en otra pestaña/pantalla para seguirla. Es la diferencia entre una app de
  entrenamiento real y un simple registro de actividad después del hecho.
- **Qué hacer**:
  1. Al iniciar sesión desde un día específico (`handleStart(workoutDayId)` en
     `WorkoutDetailPage.tsx:33-41`), traer los `PlannedExercise` de ese día (ya
     disponibles vía `useWorkoutDetail`) y mostrarlos en `SessionPage.tsx` como lista
     de "por hacer", con su objetivo (series/reps/descanso) visible.
  2. Que "Registrar ejercicio" desde esa lista pre-seleccione el ejercicio planificado
     (en vez de abrir el picker completo desde cero) y pre-cargue `sets`/`reps` como
     placeholder/default en `LogExerciseDialog`, editable si el usuario hizo distinto
     a lo planeado (más series, menos peso, etc. — eso es información valiosa en sí
     misma para ajustar el programa después).
  3. El picker completo (`ExercisePickerDialog`) sigue teniendo sentido para "Iniciar
     sesión libre" o para agregar un ejercicio extra no planificado — no hay que
     sacarlo, solo dejar de forzarlo como único camino cuando sí hay un plan de fondo.

### E.2 — No hay temporizador de descanso entre series — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `restSeconds` está modelado en `PlannedExercise` pero no se usa en
  ningún lado de la UI de sesión activa — no hay cuenta regresiva, ni notificación, ni
  siquiera el número mostrado como referencia.
- **Por qué importa**: el descanso entre series es una variable de programación tan
  importante como el peso o las repeticiones — descansar de más o de menos cambia el
  estímulo de entrenamiento. Es una funcionalidad esperable en cualquier app de
  entrenamiento (Hevy, Strong, etc. la tienen como feature central).
- **Qué hacer**: no requiere infraestructura nueva — un timer simple en el cliente
  (`setInterval`/countdown) que arranque al marcar una serie como completada, usando el
  `restSeconds` del `PlannedExercise` correspondiente como valor por defecto (editable).
  Depende de que E.1 esté resuelto primero (necesita saber cuál es el ejercicio
  planificado actual para saber qué `restSeconds` usar).

---

## F. Progreso — falta la visualización que le da sentido a los datos

### F.1 — No existe ningún gráfico de evolución de métricas — ✅ RESUELTO EN WEB (2026-08-17)

- **Evidencia**: `MetricEntriesView.tsx` es una lista cronológica plana de tarjetas
  (`entry.value entry.unit` + fecha) con filtro por tipo de métrica, sin ningún
  gráfico. `fl_chart` está declarado como dependencia en el lado mobile
  (`docs/00-progress.md:429-431`) pero nunca se usó — el mismo gap existe en ambas
  plataformas. El modelo de datos ya soporta 15 tipos de métrica distintos
  (`progressLabels.ts`: peso, IMC, %grasa, circunferencias, masa muscular, grasa
  visceral, FC en reposo, etc.), variedad más que suficiente para un gráfico útil.
- **Por qué es, junto con B.1, el hallazgo de mayor impacto de todo este documento**:
  el valor real de registrar progreso a lo largo del tiempo **es** la tendencia, no el
  dato puntual. Un entrenador (o el propio usuario) no puede evaluar de un vistazo si
  un plan de 8 semanas está funcionando mirando una lista de 20 números sueltos —
  necesita ver la curva. Sin esto, la sección "Progreso" registra datos pero no ayuda a
  decidir nada con ellos, que es justamente para lo que existe.
- **Qué hacer**:
  1. Agregar un gráfico de línea simple por métrica seleccionada (eje X: fecha, eje Y:
     valor) arriba de la lista existente en `MetricEntriesView.tsx` — no hace falta
     reemplazar la lista, solo anteponerle el gráfico. En web, cualquier librería
     liviana (Recharts, visx, o incluso SVG a mano para el caso simple de una sola
     serie) sirve; en mobile, activar por fin `fl_chart` que ya está declarado.
  2. Prioridad de implementación sugerida: peso primero (es la métrica que casi todos
     los usuarios cargan), después %grasa corporal y las circunferencias — no hace
     falta graficar las 15 métricas el mismo día, con peso resuelto ya se cubre el caso
     de uso más común.
  3. Si en algún momento se calcula el TDEE ajustado por objetivo (B.1), cruzar el
     gráfico de peso con la línea de tiempo del objetivo (ej. una línea de referencia
     de "ritmo esperado de pérdida/ganancia") es una mejora natural a futuro, no
     necesaria para una primera versión.

---

## G. Detalles menores de copy (rápidos, sin dependencias)

### G.1 — Concordancia de género/número en los contadores de "generado con IA" — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `WorkoutHomePage.tsx:25` → `"generadas con IA ✨"` (con 1 rutina generada
  dice "1 generadas", debería ser "1 generada"). `NutritionHomePage.tsx:26` →
  `"generados con IA ✨"` (mismo problema con "plan"/"planes", debería ser "generado"
  en singular). Cambio de una palabra, pluralizar condicionalmente según el count
  (`aiCount === 1 ? 'generada' : 'generadas'`, ídem para nutrición).

---

## H. Formato visual de rutinas y planes — cómo se ven, no solo qué contienen

> Esta sección responde específicamente a "¿el formato en el que se generan y
> **muestran** las rutinas/planes está bien?" — a diferencia de las secciones C y D
> (que evalúan si el *contenido* generado tiene sentido), acá el foco es la
> **estructura visual** de la ficha de rutina y del plan de alimentación en modo
> lectura, independientemente de si el contenido de adentro es correcto.
>
> **Actualización — cada hallazgo de esta sección incluye ahora un "Formato
> propuesto"**: un mockup en texto de cómo debería verse la fila/tarjeta después del
> cambio, no solo la descripción del problema. La idea es que el chat de desarrollo
> pueda tomar cada mockup como especificación directa (estructura, orden de la
> información, qué va en negrita vs. en gris) sin tener que inventar el layout desde
> cero ni volver a consultar con nadie qué se espera visualmente.

### H.1 — La ficha de rutina no muestra orden, descanso, tempo ni intensidad, aunque están cargados — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `PlannedExerciseRow.tsx:13-22` arma cada fila con miniatura + nombre +
  el resultado de `formatSetsReps()`
  (`web/src/features/workout/domain/workoutLabels.ts:67-79`), que **solo** combina
  `sets` y `repsMin`-`repsMax`. `PlannedExercise` (`workoutTypes.ts:42-52`) tiene además
  `restSeconds`, `intensityPercent` y `tempo` — ninguno de los tres se renderiza en
  ningún lado en modo lectura (ni en `WorkoutDetailPage`, ni en `WorkoutForm`). Tampoco
  se muestra `orderIndex` como número visible — el orden queda implícito en la
  posición vertical de la fila, sin un "1", "2", "3" explícito.
- **Por qué importa como formato, no solo como dato faltante**: una ficha de rutina
  real (en papel, PDF o cualquier app de entrenamiento seria) siempre tiene una
  estructura tipo tabla: **# · Ejercicio · Series · Reps · Descanso · Peso/Intensidad**.
  Reducir eso a "3 series · 8-12 reps" pierde exactamente la información que distingue
  un programa bien diseñado de una lista de nombres de ejercicios — el descanso y la
  intensidad son tan parte del estímulo de entrenamiento como el ejercicio elegido.
- **Formato actual** (`PlannedExerciseRow`):
  ```
  [🖼]  Press de banca                    4 series · 8-10 reps
  ```
- **Formato propuesto**:
  ```
  ①  [🖼]  Press de banca                                    ⓘ
           4 series × 8-10 reps · descanso 90s · RPE 8 (80%)
  ```
  - `①` — círculo numerado con `orderIndex + 1`, mismo tratamiento visual en toda la
    fila (color de fondo suave, no compite con la miniatura).
  - Línea 1: nombre del ejercicio (clickeable → `ExerciseDetailDialog`, ver H.6), en
    negrita/medium, igual que hoy.
  - Línea 2 (subtítulo, `text-muted-foreground`, igual criterio que hoy): series ×
    reps (rango si `repsMin != repsMax`) · descanso si `restSeconds != null` · RPE o
    `%1RM` si `intensityPercent != null` — usar `·` como separador, igual que
    `formatSetsReps` ya hace, solo que agregando los dos campos nuevos condicionalmente
    (mismo patrón defensivo que ya usa la función: si el campo es `null`, no se
    imprime ese segmento, no se rompe el formato).
  - `ⓘ` a la derecha (ícono `Info` o `ChevronRight`, ya hay uno similar en
    `WorkoutListView`) — refuerza que la fila es clickeable para ver el detalle
    completo, hoy solo el nombre lo es y no es obvio a simple vista.
- **Implementación**: extender `formatSetsReps()` (o crear una variante,
  `formatExerciseSummary`) para incluir `restSeconds` e `intensityPercent`/`tempo`
  cuando estén presentes, agregar el badge numerado en `PlannedExerciseRow.tsx`. No
  hace falta ampliar el modelo de datos — todo esto ya se guarda (`PlannedExercise`),
  es puramente de renderizado.

### H.2 — No hay forma visual de agrupar superseries/circuitos

- **Qué pasa**: los ejercicios de un día se renderizan como una lista vertical
  homogénea (`WorkoutDetailPage.tsx:104-106`, `.map` directo sobre `day.exercises`
  sin ningún agrupamiento). No hay soporte, ni de datos ni visual, para indicar que dos
  o tres ejercicios se hacen en superserie/circuito (ej. "3A: Press banca" /
  "3B: Remo con mancuerna", alternados sin descanso entre ellos) — un patrón de
  programación muy común en hipertrofia y en entrenamiento de tiempo reducido.
- **Qué hacer**: es una mejora de mediano plazo (toca modelo de datos, no solo
  presentación) — agregar un campo opcional de agrupación (ej. `supersetGroup` en
  `PlannedExercise`) y renderizar los ejercicios del mismo grupo dentro de un mismo
  bloque visual (borde compartido, numeración "3A"/"3B"). No es urgente si hoy nadie
  arma rutinas con superseries, pero vale la pena dejarlo marcado para cuando el
  producto quiera soportar programación más avanzada.

### H.3 — Sin resumen del día a simple vista — ✅ RESUELTO (2026-08-17)

- **Qué pasa**: cada tarjeta de día muestra el nombre y la lista de ejercicios, pero
  ningún total agregado (cantidad de ejercicios, series totales, duración estimada, o
  grupo muscular principal trabajado ese día).
- **Por qué importa**: al mirar una rutina de 4-5 días, un usuario (o un coach
  revisándola) quiere poder escanear rápido "qué es cada día" sin leer ejercicio por
  ejercicio — hoy eso solo se puede inferir leyendo cada fila.
- **Formato actual** (`WorkoutDetailPage.tsx:94-103`, encabezado de cada `Card` de día):
  ```
  Push                                                  [Iniciar]
  ```
- **Formato propuesto**:
  ```
  Push                                                  [Iniciar]
  Pecho, hombros, tríceps · 4 ejercicios · ~14 series
  ```
  - Línea 2 nueva, `text-muted-foreground`, mismo tratamiento que el subtítulo de
    `WorkoutListView`. "Pecho, hombros, tríceps" sale de los músculos primarios más
    frecuentes entre los ejercicios del día (depende de C.2 — si esa sección no está
    resuelta, arrancar solo con "4 ejercicios · ~14 series", que no depende de nada
    nuevo, y sumar el grupo muscular cuando C.2 esté implementado).
  - Series totales = suma de `sets` de los `PlannedExercise` del día — cálculo trivial,
    sin datos nuevos.
- **Implementación**: agregar el cálculo en `WorkoutDetailPage.tsx` (o extraerlo a un
  helper `summarizeDay(day: WorkoutDay)` en `workoutLabels.ts` para poder reusarlo
  también en la vista de edición `WorkoutForm` si se quiere el mismo resumen ahí).

### H.4 — El plan de alimentación no muestra imagen ni calorías por comida (asimetría con el módulo de entrenamiento) — ✅ RESUELTO (2026-08-17)

- **Evidencia**: `MealPlanDetailPage.tsx:102-108` renderiza cada comida como
  `{tipo de comida} {nombre de receta, clickeable} x{porciones}` — texto plano, sin
  imagen. Comparado con `PlannedExerciseRow.tsx`, que sí trae `ExerciseThumb` (miniatura
  de la foto del ejercicio) en cada fila: el módulo de nutrición es visualmente **más
  pobre** que el de entrenamiento pese a que, en una app de nutrición, la imagen del
  plato suele importar más para la adherencia del usuario que la foto de un ejercicio.
- **Además**, no se ve ninguna cifra de calorías/macros en la fila — hay que abrir el
  diálogo de cada receta (uno por uno) para saber cuánto aporta cada comida al día. Esto
  se encadena con D.2/D.3 (el dato de macros no se muestra ni siquiera en el detalle
  completo todavía), pero acá el problema es específico de la fila resumen: incluso
  resolviendo D.2, si no se refleja también acá, seguís sin poder evaluar un día de
  un vistazo.
- **Formato actual** (`MealPlanDetailPage.tsx:102-108`):
  ```
  Desayuno    Avena con banana y proteína              x1.5
  ```
- **Formato propuesto**:
  ```
  [🍳 Desayuno]  [🖼]  Avena con banana y proteína         x1.5
                        ~420 kcal · P 32g · C 45g · G 10g
  ```
  - Un ícono chico por tipo de comida (🍳 desayuno / 🍽 almuerzo-cena / 🍎 snack, o los
    equivalentes de `lucide-react` ya en uso en el resto de la app) junto al label,
    para que el tipo de comida se reconozca de un vistazo por color/forma, no solo por
    texto — mismo principio que ya aplica `colorFor(OBJECTIVE_COLORS, ...)` en
    `WorkoutListView`.
  - Miniatura cuadrada (`recipe.imageUrl`, mismo componente/tamaño que `ExerciseThumb`
    — vale la pena extraer un `Thumb` genérico compartido entre `workout` y
    `nutrition` en vez de duplicar el componente, ya que la lógica es idéntica: imagen
    si existe, ícono de color por categoría si no).
  - Línea 2 nueva bajo el nombre: calorías totales de esa comida
    (`recipe.caloriesPerServing × servings`, redondeado) y opcionalmente P/C/G si D.2
    ya está resuelto — mismo formato compacto que ya usa `DailyIntakeView.tsx:39-41`
    para el resumen del día (`{kcal} kcal · P {}g · C {}g · G {}g`), así el mismo
    patrón visual se repite en diario, receta y ahora fila del plan.
- **Implementación**: extender la fila de `MealPlanDetailPage.tsx:102-108` con el
  thumbnail + línea de macros, reusando el cálculo que `DailyIntakeView.tsx:23-31` ya
  hace para totales (acá es por comida individual, no por día, pero es la misma
  fórmula aplicada a un solo ítem).

### H.5 — Las comidas de un día no tienen horario ni orden garantizado — parte 1 (orden) ❌ FALSO POSITIVO, verificado (2026-08-17)

- **Qué pasa**: `MEAL_TYPE_LABELS` (`nutritionLabels.ts`) son categorías
  (Desayuno/Almuerzo/Cena/Snack), no horarios reales — no hay forma de indicar "8:00" o
  "post-entreno" para timing nutricional. Además, ni `MealPlanMealJpa`
  (`backend/modules/nutrition/.../MealPlanMealJpa.java:42`) tiene `@OrderBy` sobre la
  colección de comidas, ni `MealPlanDetailPage.tsx` ordena explícitamente por
  `orderIndex` en el frontend — en la práctica, el orden en que aparecen las comidas de
  un día depende del orden que devuelva la consulta JPA, no está garantizado que
  Desayuno aparezca siempre antes que Cena. **El mismo problema existe en rutinas**:
  `PlannedExerciseJpa.java:37` tampoco tiene `@OrderBy`, así que el orden de ejercicios
  dentro de un día tampoco está garantizado pese a que `orderIndex` se guarda.
- **Verificado contra el código real — no hace falta ningún cambio**: este proyecto no
  mapea `meals`/`exercises` como colecciones `@OneToMany` de JPA (por eso no hay
  `@OrderBy` para agregar — no es que falte la anotación, es que el patrón de
  persistencia acá es distinto). El orden ya está garantizado en las tres capas
  relevantes vía métodos derivados de Spring Data, que generan `ORDER BY` real en SQL:
  `WorkoutDayJpaRepository.findByWorkoutIdOrderByDayIndexAsc`,
  `MealPlanDayJpaRepository.findByMealPlanIdOrderByPlanDateAsc`,
  `PlannedExerciseJpaRepository.findByWorkoutDayIdInOrderByOrderIndexAsc`,
  `MealPlanMealJpaRepository.findByMealPlanDayIdInOrderByOrderIndexAsc`. El hallazgo
  parece haberse basado en buscar literalmente `@OrderBy` en el código sin considerar
  que el ordering derivado de Spring Data es el mecanismo real usado acá.
- **Qué hacer** (solo queda la parte 2, horario real):
  1. Horario real por comida: es una mejora de producto más grande (agregar un campo
     `time` opcional a `MealPlanMeal`), no urgente si hoy nadie lo pidió explícitamente
     — se deja anotado acá para cuando se priorice timing nutricional (relevante sobre
     todo para planes de rendimiento deportivo, pre/post entreno).

### H.6 — El detalle de ejercicio muestra una sola imagen, pero la fuente de datos ya tiene dos (posición inicial y final del movimiento) — ✅ RESUELTO (2026-08-17)

- **Hallazgo confirmado**: el catálogo usa el dataset público `free-exercise-db`
  (`github.com/yuhonas/free-exercise-db`, ver
  `backend/app/src/main/resources/db/migration/V13__exercise_reference_images.sql:1-3`).
  Cada ejercicio de ese dataset trae **dos imágenes** por carpeta —
  `.../exercises/{Ejercicio}/0.jpg` (posición inicial) y `.../1.jpg` (posición
  final/contraída del movimiento) — pero la migración `V13` solo guarda `0.jpg` en la
  columna `image_url` (una sola columna VARCHAR en `workout_exercises`, sin lugar para
  una segunda URL). **Se verificó en vivo** que la segunda imagen existe y es
  accesible para los ejercicios revisados:

  | Ejercicio | `0.jpg` | `1.jpg` |
  |---|---|---|
  | Sentadilla (`Barbell_Squat`) | 200 OK | 200 OK |
  | Dominadas (`Pullups`) | 200 OK | 200 OK |
  | Press de banca (`Barbell_Bench_Press_-_Medium_Grip`) | 200 OK | 200 OK |
  | Crunches abdominales (`Crunches`) | 200 OK | 200 OK |

  (Burpees no tiene ninguna de las dos — coincide con la nota de `V13` que ya lo
  marca como "sin imagen", así que no es un caso nuevo, es consistente con lo
  documentado).
- **Por qué importa, con la propia palabra del usuario que disparó este hallazgo**:
  *"con una no se puede entender nada"* — es exactamente el problema real de mostrar
  solo la posición inicial (o solo la final) de un movimiento: una sentadilla con
  barra parada (imagen 0) no comunica la profundidad ni la mecánica del ejercicio sin
  ver también la posición abajo (imagen 1). El dataset elegido ya resuelve esto
  correctamente — el catálogo actual solo está usando la mitad de lo que tiene
  disponible.
- **Formato propuesto** (`ExerciseDetailDialog.tsx`, reemplaza el bloque de una sola
  `<img>` en las líneas 40-47):
  ```
  Sentadilla
  [Fuerza] [Intermedio] [Barra]

  ┌──────────────┐  ┌──────────────┐
  │              │  │              │
  │   Posición   │  │   Posición   │
  │   inicial    │  │   final      │
  │              │  │              │
  └──────────────┘  └──────────────┘

  Músculos: Cuádriceps (primario) · Glúteos, isquiotibiales (secundarios)
  ▶ Ver video de demostración
  Cómo hacerlo: ...
  ```
  - Dos imágenes lado a lado en pantallas anchas (≥ `sm`), con una etiqueta chica
    debajo de cada una ("Posición inicial" / "Posición final") — no hace falta que
    sean perfectas para todos los ejercicios (para algunos "inicial/final" no aplica
    tan literalmente, ej. plancha), pero es la interpretación correcta para la
    mayoría de los ejercicios de fuerza del catálogo.
  - En mobile (`< sm`), apilar verticalmente o usar un carrusel de 2 imágenes
    swipeable (lo que ya exista como patrón en el proyecto para evitar traer una
    librería nueva solo para esto) — dos imágenes chicas lado a lado en una pantalla
    angosta pueden quedar demasiado pequeñas para ser útiles.
  - Si en algún momento el catálogo suma ejercicios con más de 2 imágenes (el dataset
    en algunos casos tiene 3-4), el mismo layout con scroll horizontal o un
    indicador de puntos (dots) escala sin rediseñar de nuevo — no hace falta
    resolverlo ahora, pero vale la pena no hardcodear "exactamente 2" en el
    componente si el modelo de datos permite más.
- **Implementación (toca las 4 capas, de atrás para adelante)**:
  1. **Migración nueva** (ej. `V19__exercise_end_position_images.sql`): agregar
     columna `image_url_end VARCHAR(500)` (nullable) a `workout_exercises`. Backfill
     determinístico para las filas ya sembradas desde `free-exercise-db`: son las que
     tienen `image_url` con el patrón `.../exercises/{X}/0.jpg` — para esas,
     `image_url_end = REPLACE(image_url, '/0.jpg', '/1.jpg')` cubre la gran mayoría
     sin tener que rehacer el mapeo a mano ejercicio por ejercicio (a diferencia de
     `V13`, que sí necesitó match manual de nombres). Verificar con `HTTP 200` antes
     de commitear el backfill masivo, igual criterio que ya siguió `V13` — algunos
     ejercicios pueden no tener `1.jpg` aunque tengan `0.jpg` (poco frecuente, pero
     hay que verificar, no asumir 1:1).
  2. **Backend**: agregar `imageUrlEnd` en `ExerciseJpa.java` (junto a `imageUrl`,
     línea 57-58), `Exercise.java` (dominio), y el DTO de salida
     `WorkoutDtos.java:49` (agregar el campo al record que ya expone `imageUrl`).
  3. **Frontend**: agregar `imageUrlEnd?: string | null` a la interfaz `Exercise` en
     `workoutTypes.ts`, y actualizar `ExerciseDetailDialog.tsx:40-47` para renderizar
     ambas imágenes con el layout de arriba (mantener el fallback actual de
     "Todavía no hay imagen de referencia" para cuando falte cualquiera de las dos,
     no solo cuando falten ambas).
  4. **No hace falta tocar** `ExerciseThumb.tsx` (la miniatura de las filas de lista
     sigue usando una sola imagen, `image_url` — tiene sentido, ahí el espacio es
     chico y el propósito es solo identificar el ejercicio, no explicar la técnica).

---

## Roadmap sugerido (por impacto real en la utilidad de la app, no solo por costo)

**Los dos cambios de mayor impacto — habilitan varios otros hallazgos de este documento:**
1. ✅ **B.1** — Calcular TDEE/BMR automáticamente a partir del perfil — RESUELTO
   2026-08-17 (`TdeeCalculator.java`, Mifflin-St Jeor + factor de actividad + ajuste
   por objetivo; editable a mano en `ProfileForm`).
2. ✅ **F.1** — Gráfico de evolución en Progreso — RESUELTO EN WEB 2026-08-17
   (`MetricChart.tsx`, SVG a mano, se muestra al filtrar por un tipo de métrica
   específico con 2+ registros).

**Bugs a corregir antes de seguir sumando features de IA:**
3. ✅ **A.2** — Actualizar el modelo de Groq — RESUELTO 2026-08-17 (`V19__fix_groq_model.sql`).
4. 🟡 **A.1** — Loguear errores + distinguir `partial` de `success` — RESUELTO
   2026-08-17. Falta la parte de probar/reforzar el prompt `meal_plan_generator` con
   corridas reales (necesita Ollama corriendo, no se pudo hacer desde este chat).
5. ❌ **A.3** — Falso positivo, ya estaba resuelto (verificado 2026-08-17).
6. ❌ **H.5** (parte 1, orden) — Falso positivo, ya estaba resuelto (verificado
   2026-08-17). Parte 2 (horario real por comida) sigue pendiente, ver backlog.

**Contenido verificado, requiere una migración chica (alto impacto, esfuerzo contenido):**
7. ✅ **H.6** — Segunda imagen (posición final) en el detalle de ejercicio — RESUELTO
   2026-08-17 (`V20__exercise_end_position_images.sql`, backfill determinístico +
   `imageUrlEnd` en las 3 capas + `ExerciseDetailDialog` muestra ambas lado a lado).

**Calidad del contenido generado (afecta la confianza del usuario en la IA):**
8. 🟡 **C.1** — Validar coherencia día/ejercicio — PARCIAL 2026-08-17 (mínimo viable:
   rechaza días con menos de 3 ejercicios y pide reintentar). Falta el chequeo de
   coherencia contra grupo muscular esperado (items 2-4 del hallazgo original).

**Mejoras de visualización — dato ya disponible en la API, solo falta mostrarlo (bajo costo, alto valor):**
9. ✅ **D.2** — Macros en el detalle de receta — RESUELTO 2026-08-17.
10. ✅ **H.4** — Miniatura + calorías por fila del plan de alimentación — RESUELTO
    2026-08-17.
11. ✅ **C.2** — Músculos trabajados en el detalle de ejercicio — RESUELTO 2026-08-17.
12. ✅ **H.1** — Descanso, intensidad, tempo y número de orden en la ficha de rutina —
    RESUELTO 2026-08-17.
13. ✅ **B.2** — IMC calculado y mostrado en el perfil — RESUELTO 2026-08-17.
14. ✅ **D.1** — Barra de progreso de calorías en el diario — RESUELTO 2026-08-17.
15. ✅ **D.3** — Macros por día en el detalle del plan de alimentación — RESUELTO
    2026-08-17.
16. ✅ **H.3** — Resumen por día (ejercicios/series totales) — RESUELTO 2026-08-17.

**Mejora de flujo central de la app (más trabajo, pero es el corazón del producto):**
17. ✅ **E.1** — Mostrar el objetivo planificado durante la sesión activa — RESUELTO
    2026-08-17 (`SessionPage.tsx` § "Plan de hoy", pre-carga sets/reps al registrar).
18. ✅ **E.2** — Temporizador de descanso entre series — RESUELTO 2026-08-17 (arranca
    automáticamente al registrar un ejercicio planificado con `restSeconds`).

**Backlog — requiere modelo de datos nuevo, no urgente:**
19. **H.2** — Agrupación visual de superseries/circuitos (campo nuevo tipo
    `supersetGroup`) — solo si el producto quiere soportar programación más avanzada.
20. **H.5 (parte 2)** — Horario real por comida (más allá de la categoría
    Desayuno/Almuerzo/Cena/Snack), relevante para timing nutricional pre/post entreno.

**Contenido, no código:**
21. **C.3** — Videos de demostración para los ejercicios compuestos de mayor riesgo
    técnico (sentadilla, peso muerto, press banca, press militar, remo, dominadas).

**Rápido y sin dependencias:**
22. ✅ **G.1** — Concordancia de género/número en los contadores "generado/a(s) con IA" — RESUELTO 2026-08-17.

---

## Cómo usar este documento (nota para el chat de desarrollo)

- Cada hallazgo cita archivo(s) y, cuando aplica, línea — no hace falta re-explorar el
  repo para ubicar dónde tocar.
- Los hallazgos de la sección A son bugs reproducidos en vivo contra el backend real,
  no hipótesis — se puede reproducir A.1 con el mismo pedido documentado contra Ollama
  local, y A.2 se confirma con una sola consulta a `ai_provider_configs`.
- Si se cierra un ítem, marcarlo acá (`✅ RESUELTO (fecha)` en el subtítulo
  correspondiente, misma convención que `docs/00-progress.md` y
  `docs/06-ux-ui-audit.md`) para que este documento siga siendo confiable como fuente
  de qué falta y no quede desactualizado.
- Este documento no reemplaza `docs/06-ux-ui-audit.md` — son complementarios. Ese
  audita la interfaz en general; este audita si el **contenido de entrenamiento y
  nutrición que la app genera y muestra tiene sentido real** desde la disciplina.

## Estado (actualizado 2026-08-17, segunda ronda)

**Primera ronda**: sección A completa (bugs críticos) + G.1.

**Segunda ronda** (la misma sesión, a continuación): todo lo que quedaba salvo
contenido puro y backlog explícito:

- ✅ Resuelto: B.1, B.2, C.2, D.1, D.2, D.3, E.1, E.2, F.1 (web), H.1, H.3, H.4, H.6.
- 🟡 Parcial: C.1 (mínimo viable — rechaza rutinas con muy pocos ejercicios por día;
  falta el chequeo de coherencia contra grupo muscular esperado).
- **Deliberadamente sin tocar** (clasificación del propio documento, no un olvido):
  - **C.3** — carga de videos de ejercicios: es contenido, no código.
  - **H.2** — agrupación de superseries: backlog, requiere campo nuevo en el modelo
    de datos, "no urgente" según el propio documento.
  - **H.5 (parte 2)** — horario real por comida: backlog, mismo criterio.
- Todo lo de arriba es **solo backend/web** — no se tocó Flutter en esta ronda (no hay
  entorno Flutter disponible desde este chat para compilar/verificar). El seguimiento
  consolidado de qué le falta a mobile para tener paridad con esto vive en
  `docs/00-progress.md` § Pendientes globales, no acá.
- Con esto, de los 22 hallazgos originales del roadmap solo quedan sin cerrar: C.1
  (parcial), C.3, H.2 y H.5-parte-2 — los cuatro por decisión explícita de alcance, no
  por falta de tiempo.
