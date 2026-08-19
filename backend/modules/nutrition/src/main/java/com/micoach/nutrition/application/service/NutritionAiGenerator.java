package com.micoach.nutrition.application.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.ai.application.port.in.AiUseCase.GenerationLogFilter;
import com.micoach.ai.domain.GenerationLog;
import com.micoach.nutrition.application.port.in.NutritionUseCase.MealPlanData;
import com.micoach.nutrition.application.port.in.NutritionUseCase.MealPlanDayData;
import com.micoach.nutrition.application.port.in.NutritionUseCase.MealPlanMealData;
import com.micoach.nutrition.domain.Recipe;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Arma el pedido al módulo {@code ai} (prompt {@code meal_plan_generator}) y traduce su
 * respuesta JSON a un {@link MealPlanData} válido. Mismo patrón que
 * {@code workout.WorkoutAiGenerator}: la IA nombra recetas por nombre (no conoce ids),
 * se resuelven contra el catálogo real, y las fechas se calculan acá (la IA no sabe qué
 * día es "hoy" de forma confiable) a partir de un {@code dayOffset} relativo.
 */
@Component
class NutritionAiGenerator {

    private static final String PROMPT_SLUG = "meal_plan_generator";

    private final AiUseCase aiUseCase;
    private final UserProfileUseCase userProfileUseCase;
    private final ObjectMapper objectMapper;

    NutritionAiGenerator(AiUseCase aiUseCase, UserProfileUseCase userProfileUseCase, ObjectMapper objectMapper) {
        this.aiUseCase = aiUseCase;
        this.userProfileUseCase = userProfileUseCase;
        this.objectMapper = objectMapper;
    }

    MealPlanData generate(Long userId, String goal, List<Recipe> catalog) {
        String catalogText = catalog.stream()
                .map(r -> "- " + r.getName() + " (" + r.getMealCategory() + ")")
                .reduce((a, b) -> a + "\n" + b).orElse("");

        AiUseCase.GenerationResult result = aiUseCase.generate(userId, PROMPT_SLUG,
                Map.of("goal", goal, "catalog", catalogText, "profile", buildProfileText(userId),
                        "feedbackHistory", buildFeedbackHistory(userId)));

        AiMealPlanJson parsed = parseJson(result);
        return toMealPlanData(parsed, catalog);
    }

    /**
     * Mismo mecanismo de memoria persistente que {@code WorkoutAiGenerator} — acá solo
     * la mitad "auto-corrección por validación" (no hay todavía un link explícito
     * plan→generación como el de rutinas para capturar el descarte humano; ver
     * docs/12-tp-fin-de-ciclo.md § Limitaciones y trabajo futuro).
     */
    private String buildFeedbackHistory(Long userId) {
        List<GenerationLog> logs = aiUseCase.listGenerationLogs(new GenerationLogFilter(userId, PROMPT_SLUG));
        if (logs.isEmpty() || !"partial".equals(logs.get(0).getStatus())) {
            return "Sin antecedentes relevantes de generaciones anteriores.";
        }
        return "La generación anterior para este usuario fue rechazada automáticamente por no "
                + "devolver un JSON válido o no cumplir el formato pedido — asegurate de devolver "
                + "SOLO el JSON, sin texto adicional.";
    }

    private String buildProfileText(Long userId) {
        UserProfile profile = userProfileUseCase.getOrCreateProfile(userId);
        List<UserPathology> pathologies = userProfileUseCase.getPathologies(userId);

        StringBuilder text = new StringBuilder();
        if (profile.getDietaryGoal() != null) {
            text.append("- Objetivo dietario: ").append(profile.getDietaryGoal()).append('\n');
        }
        if (profile.getWeightKg() != null) {
            text.append("- Peso: ").append(profile.getWeightKg()).append(" kg\n");
        }
        if (profile.getTdeeCalories() != null) {
            text.append("- Gasto calórico diario estimado (TDEE): ").append(profile.getTdeeCalories())
                    .append(" kcal\n");
        }
        if (!pathologies.isEmpty()) {
            String list = pathologies.stream().map(UserPathology::getPathology)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            text.append("- Patologías reportadas (IMPORTANTE, tenerlas en cuenta): ").append(list).append('\n');
        }
        return text.isEmpty() ? "Sin datos de perfil cargados." : text.toString();
    }

    private AiMealPlanJson parseJson(AiUseCase.GenerationResult result) {
        String jsonSlice = extractFirstJsonObject(result.rawOutput());
        if (jsonSlice == null) {
            aiUseCase.markGenerationPartial(result.logId());
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR, "La IA no devolvió un JSON válido");
        }
        try {
            return objectMapper.readValue(jsonSlice, AiMealPlanJson.class);
        } catch (Exception e) {
            aiUseCase.markGenerationPartial(result.logId());
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo interpretar el plan generado por la IA: " + e.getMessage());
        }
    }

    // Ver WorkoutAiGenerator.extractFirstJsonObject: mismo enfoque de llaves
    // balanceadas, modelos chicos suelen agregar texto extra pese a que el prompt pide
    // "solo JSON".
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

    private MealPlanData toMealPlanData(AiMealPlanJson json, List<Recipe> catalog) {
        LocalDate today = LocalDate.now();
        List<MealPlanDayData> days = new ArrayList<>();
        List<AiMealPlanDay> sourceDays = json.days() == null ? List.of() : json.days();
        int maxOffset = 0;
        for (AiMealPlanDay day : sourceDays) {
            int offset = day.dayOffset() == null ? 0 : Math.max(0, day.dayOffset());
            maxOffset = Math.max(maxOffset, offset);
            List<MealPlanMealData> meals = new ArrayList<>();
            int orderIndex = 1;
            List<AiMealEntry> sourceMeals = day.meals() == null ? List.of() : day.meals();
            for (AiMealEntry entry : sourceMeals) {
                Long recipeId = resolveRecipeId(entry.recipeName(), catalog);
                if (recipeId == null) {
                    continue;
                }
                BigDecimal servings = entry.servings() == null ? BigDecimal.ONE : BigDecimal.valueOf(entry.servings());
                // meal_type es NOT NULL con CHECK en la BD (V3__nutrition_schema.sql) — un
                // valor inválido del modelo rompería el INSERT sin capturar (mismo tipo de
                // bug que "objective" en workout, ver WorkoutAiGenerator.normalizeEnum).
                String mealType = normalizeMealType(entry.mealType());
                meals.add(new MealPlanMealData(recipeId, mealType, orderIndex++, servings, null));
            }
            days.add(new MealPlanDayData(today.plusDays(offset), meals));
        }
        if (days.isEmpty()) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR, "La IA generó un plan sin días");
        }
        String name = truncate(json.name(), 200);
        return new MealPlanData(name == null || name.isBlank() ? "Plan generado con IA" : name,
                truncate(json.description(), 1000), today, today.plusDays(maxOffset), json.targetCalories(),
                null, null, null, days);
    }

    private static final List<String> VALID_MEAL_TYPES = List.of("breakfast", "lunch", "dinner", "snack");

    private String normalizeMealType(String value) {
        if (value != null && VALID_MEAL_TYPES.contains(value.trim().toLowerCase())) {
            return value.trim().toLowerCase();
        }
        return "snack";
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private Long resolveRecipeId(String name, List<Recipe> catalog) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String normalized = name.trim().toLowerCase();
        return catalog.stream()
                .filter(r -> r.getName().trim().equalsIgnoreCase(normalized))
                .findFirst()
                .or(() -> catalog.stream()
                        .filter(r -> r.getName().toLowerCase().contains(normalized)
                                || normalized.contains(r.getName().toLowerCase()))
                        .findFirst())
                .map(Recipe::getId)
                .orElse(null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiMealPlanJson(String name, String description, Integer targetCalories,
                                  List<AiMealPlanDay> days) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiMealPlanDay(Integer dayOffset, List<AiMealEntry> meals) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiMealEntry(String recipeName, String mealType, Double servings) {
    }
}
