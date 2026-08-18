package com.micoach.workout.application.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.domain.UserInjury;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;
import com.micoach.workout.application.port.in.WorkoutUseCase.PlannedExerciseData;
import com.micoach.workout.application.port.in.WorkoutUseCase.WorkoutData;
import com.micoach.workout.application.port.in.WorkoutUseCase.WorkoutDayData;
import com.micoach.workout.domain.Exercise;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Arma el pedido al módulo {@code ai} (prompt {@code workout_generator}) y traduce su
 * respuesta JSON a un {@link WorkoutData} válido: los ejercicios que nombra la IA se
 * resuelven contra el catálogo real (por nombre, no hay forma de que la IA conozca los
 * ids), y los que no matchean ningún ejercicito se descartan en vez de romper la
 * generación completa.
 */
@Component
class WorkoutAiGenerator {

    private static final String PROMPT_SLUG = "workout_generator";

    private final AiUseCase aiUseCase;
    private final UserProfileUseCase userProfileUseCase;
    private final ObjectMapper objectMapper;

    WorkoutAiGenerator(AiUseCase aiUseCase, UserProfileUseCase userProfileUseCase, ObjectMapper objectMapper) {
        this.aiUseCase = aiUseCase;
        this.userProfileUseCase = userProfileUseCase;
        this.objectMapper = objectMapper;
    }

    WorkoutData generate(Long userId, String goal, List<Exercise> catalog) {
        UserProfile profile = userProfileUseCase.getOrCreateProfile(userId);
        List<Exercise> filteredCatalog = filterByEquipment(catalog, profile.getEquipment());
        String catalogText = filteredCatalog.stream().map(Exercise::getName).map(name -> "- " + name)
                .reduce((a, b) -> a + "\n" + b).orElse("");

        AiUseCase.GenerationResult result = aiUseCase.generate(userId, PROMPT_SLUG,
                Map.of("goal", goal, "catalog", catalogText, "profile", buildProfileText(userId, profile)));

        AiWorkoutJson parsed = parseJson(result);
        return toWorkoutData(result, parsed, filteredCatalog);
    }

    /**
     * Con el catálogo ampliado (106 ejercicios) mandar la lista completa en cada prompt
     * infla el tamaño del request (más lento, más caro en proveedores cloud) y le da a la
     * IA más variantes casi idénticas entre las que confundirse. Se filtra por el
     * equipamiento que el usuario declaró en su perfil — bodyweight/sin equipamiento
     * siempre queda incluido. Si el perfil no tiene equipamiento cargado, o el filtro deja
     * muy pocas opciones (dato de perfil raro/incompleto), se manda el catálogo completo
     * en vez de arriesgarse a una rutina pobre por falta de variedad.
     */
    private List<Exercise> filterByEquipment(List<Exercise> catalog, List<String> userEquipment) {
        if (userEquipment == null || userEquipment.isEmpty()) {
            return catalog;
        }
        Set<String> available = new HashSet<>(userEquipment);
        List<Exercise> filtered = catalog.stream()
                .filter(e -> e.getEquipment() == null || e.getEquipment().isEmpty()
                        || e.getEquipment().contains("bodyweight")
                        || e.getEquipment().stream().anyMatch(available::contains))
                .toList();
        return filtered.size() >= 15 ? filtered : catalog;
    }

    /**
     * Perfil real del usuario (nivel, equipamiento, objetivo, lesiones, patologías) para
     * que la IA no genere una rutina "genérica" ignorando datos que ya tenemos — en
     * particular las lesiones, que en una app de salud no son un detalle cosmético.
     */
    private String buildProfileText(Long userId, UserProfile profile) {
        List<UserPathology> pathologies = userProfileUseCase.getPathologies(userId);
        List<UserInjury> injuries = userProfileUseCase.getInjuries(userId);

        StringBuilder text = new StringBuilder();
        if (profile.getExperienceLevel() != null) {
            text.append("- Nivel de experiencia: ").append(profile.getExperienceLevel()).append('\n');
        }
        if (profile.getActivityLevel() != null) {
            text.append("- Nivel de actividad: ").append(profile.getActivityLevel()).append('\n');
        }
        if (profile.getEquipment() != null && !profile.getEquipment().isEmpty()) {
            text.append("- Equipamiento disponible: ").append(String.join(", ", profile.getEquipment()))
                    .append('\n');
        }
        if (profile.getDietaryGoal() != null) {
            text.append("- Objetivo: ").append(profile.getDietaryGoal()).append('\n');
        }
        if (!pathologies.isEmpty()) {
            String list = pathologies.stream().map(UserPathology::getPathology)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            text.append("- Patologías reportadas (IMPORTANTE, tenerlas en cuenta): ").append(list).append('\n');
        }
        if (!injuries.isEmpty()) {
            String list = injuries.stream()
                    .map(i -> i.getBodyPart() + " (" + i.getInjuryType()
                            + (i.getStatus() != null ? ", " + i.getStatus() : "") + ")")
                    .reduce((a, b) -> a + ", " + b).orElse("");
            text.append("- Lesiones reportadas (IMPORTANTE, evitar ejercicios que las agraven): ")
                    .append(list).append('\n');
        }
        return text.isEmpty() ? "Sin datos de perfil cargados." : text.toString();
    }

    private AiWorkoutJson parseJson(AiUseCase.GenerationResult result) {
        String jsonSlice = extractFirstJsonObject(result.rawOutput());
        if (jsonSlice == null) {
            aiUseCase.markGenerationPartial(result.logId());
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR, "La IA no devolvió un JSON válido");
        }
        try {
            return objectMapper.readValue(jsonSlice, AiWorkoutJson.class);
        } catch (Exception e) {
            aiUseCase.markGenerationPartial(result.logId());
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo interpretar la rutina generada por la IA: " + e.getMessage());
        }
    }

    /**
     * Modelos chicos como llama3.2 a veces agregan explicación antes/después del JSON
     * (o code fences ```json) a pesar de que el prompt pide "solo JSON". En vez de
     * cortar del primer {@code {} al último {@code }} del texto completo (rompe si esa
     * prosa extra contiene alguna llave suelta), se cuentan llaves balanceadas desde el
     * primer {@code {} para quedarse solo con ESE objeto.
     */
    private String extractFirstJsonObject(String raw) {
        if (raw == null) {
            return null;
        }
        int start = raw.indexOf('{');
        if (start == -1) {
            return null;
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
            } else if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return raw.substring(start, i + 1);
                }
            }
        }
        return null;
    }

    // Valores permitidos por el CHECK constraint de workout_workouts (V2__workout_schema.sql)
    // — un modelo chico como llama3.2:1b no los respeta de forma confiable (llegó a mandar
    // una oración libre de más de 30 caracteres en "objective", que además de no matchear
    // el CHECK ni siquiera entra en la columna VARCHAR(30) y tira un 500 sin capturar).
    private static final Set<String> VALID_OBJECTIVES =
            Set.of("lose_fat", "gain_muscle", "maintain", "endurance", "strength", "general_health");
    private static final Set<String> VALID_LEVELS = Set.of("beginner", "intermediate", "advanced");

    // docs/10-recomendaciones-coach-nutricion.md § C.1: se vio en la práctica que el
    // modelo puede devolver días con un solo ejercicio (ej. "Piernas" con una sola
    // serie de crunches) — inutilizable como sesión real. Validación mínima viable:
    // rechazar y pedir reintentar en vez de guardar una rutina que nadie puede usar.
    private static final int MIN_EXERCISES_PER_DAY = 3;

    private WorkoutData toWorkoutData(AiUseCase.GenerationResult result, AiWorkoutJson json, List<Exercise> catalog) {
        List<WorkoutDayData> days = new ArrayList<>();
        int dayIndex = 1;
        List<AiWorkoutDay> sourceDays = json.days() == null ? List.of() : json.days();
        for (AiWorkoutDay day : sourceDays) {
            List<PlannedExerciseData> exercises = new ArrayList<>();
            int orderIndex = 1;
            List<AiExerciseEntry> sourceExercises = day.exercises() == null ? List.of() : day.exercises();
            for (AiExerciseEntry entry : sourceExercises) {
                Exercise resolved = resolveExercise(entry.exerciseName(), catalog);
                if (resolved == null) {
                    continue;
                }
                exercises.add(new PlannedExerciseData(resolved.getId(), orderIndex++, entry.sets(), entry.repsMin(),
                        entry.repsMax(), defaultRestSeconds(resolved), null, null, null));
            }
            // Un día con ejercicios reales no puede ser "restDay": true pase lo que pase el
            // modelo haya marcado (visto en la práctica: los marcó todos true igual).
            boolean restDay = exercises.isEmpty() && Boolean.TRUE.equals(day.restDay());
            if (!restDay && exercises.size() < MIN_EXERCISES_PER_DAY) {
                aiUseCase.markGenerationPartial(result.logId());
                throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                        "La IA generó un día de entrenamiento con muy pocos ejercicios ("
                                + exercises.size() + "). Probá generar de nuevo.");
            }
            days.add(new WorkoutDayData(dayIndex++, truncate(day.name(), 100), restDay, exercises));
        }
        if (days.isEmpty()) {
            aiUseCase.markGenerationPartial(result.logId());
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR, "La IA generó una rutina sin días");
        }
        String name = truncate(json.name(), 200);
        return new WorkoutData(name == null || name.isBlank() ? "Rutina generada con IA" : name,
                truncate(json.description(), 1000), normalizeEnum(json.objective(), VALID_OBJECTIVES),
                normalizeEnum(json.level(), VALID_LEVELS), json.durationWeeks(), days);
    }

    private String normalizeEnum(String value, Set<String> allowed) {
        return value != null && allowed.contains(value.trim().toLowerCase()) ? value.trim().toLowerCase() : null;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private Exercise resolveExercise(String name, List<Exercise> catalog) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String normalized = name.trim().toLowerCase();
        return catalog.stream()
                .filter(e -> e.getName().trim().equalsIgnoreCase(normalized))
                .findFirst()
                .or(() -> catalog.stream()
                        .filter(e -> e.getName().toLowerCase().contains(normalized)
                                || normalized.contains(e.getName().toLowerCase()))
                        .findFirst())
                .orElse(null);
    }

    // docs/10-recomendaciones-coach-nutricion.md § I.2: la IA nunca completa restSeconds
    // (no se le pide), así que sin un default nadie ve el temporizador de descanso (E.2) ni
    // la ficha de rutina (H.1). Heurística simple en vez de default fijo: compuestos con
    // barra sostienen más carga y necesitan más recuperación entre series que aislados o
    // trabajo en máquina.
    private static final Set<String> HEAVY_COMPOUND_EQUIPMENT = Set.of("barbell", "rack", "sled");

    private Integer defaultRestSeconds(Exercise exercise) {
        if (!"strength".equals(exercise.getCategory())) {
            return null;
        }
        boolean heavyCompound = exercise.getEquipment().stream().anyMatch(HEAVY_COMPOUND_EQUIPMENT::contains);
        return heavyCompound ? 90 : 60;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiWorkoutJson(String name, String description, String objective, String level,
                                 Integer durationWeeks, List<AiWorkoutDay> days) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiWorkoutDay(String name, Boolean restDay, List<AiExerciseEntry> exercises) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiExerciseEntry(String exerciseName, Integer sets, Integer repsMin, Integer repsMax) {
    }
}
