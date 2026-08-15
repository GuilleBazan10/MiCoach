package com.micoach.nutrition.application.service;

import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.nutrition.application.port.in.NutritionUseCase.MealPlanData;
import com.micoach.nutrition.domain.MealPlan;
import com.micoach.nutrition.domain.Recipe;
import com.micoach.progress.application.port.in.ProgressUseCase;
import com.micoach.progress.domain.ProgressEntry;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * Recalcula el objetivo calórico de un plan existente según el progreso real del
 * usuario (tendencia de peso reciente vs. su objetivo dietario) y le pide al
 * {@link NutritionAiGenerator} un plan nuevo dentro de ese presupuesto, mismo cantidad
 * de días que el original. La cuenta de la tendencia es aritmética simple, a propósito
 * NO se le pide a la IA que la calcule — un modelo chico no es confiable para ese tipo
 * de cálculo, mejor hacerlo en código y usar la IA solo para lo que sabe hacer bien:
 * elegir recetas del catálogo.
 */
@Component
class NutritionCalorieAdjuster {

    private static final int ADJUSTMENT_STEP_KCAL = 250;
    private static final double FLAT_TREND_THRESHOLD_KG_PER_WEEK = 0.1;
    private static final double DRIFT_THRESHOLD_KG_PER_WEEK = 0.3;
    private static final int MIN_HISTORY_DAYS = 3;
    private static final int DEFAULT_CALORIES = 2000;

    private final NutritionAiGenerator aiGenerator;
    private final UserProfileUseCase userProfileUseCase;
    private final ProgressUseCase progressUseCase;

    NutritionCalorieAdjuster(NutritionAiGenerator aiGenerator, UserProfileUseCase userProfileUseCase,
                             ProgressUseCase progressUseCase) {
        this.aiGenerator = aiGenerator;
        this.userProfileUseCase = userProfileUseCase;
        this.progressUseCase = progressUseCase;
    }

    MealPlanData adjust(Long userId, MealPlan existing, List<Recipe> catalog) {
        UserProfile profile = userProfileUseCase.getOrCreateProfile(userId);
        int currentTarget = resolveCurrentTarget(existing, profile);
        int adjustedTarget = computeAdjustedTarget(userId, profile.getDietaryGoal(), currentTarget);
        int dayCount = Math.max(1, existing.getDays().size());

        String goal = String.format(
                "Ajustá este plan de alimentación a aproximadamente %d calorías diarias en total, en "
                        + "EXACTAMENTE %d días, manteniendo variedad de comidas.",
                adjustedTarget, dayCount);

        return aiGenerator.generate(userId, goal, catalog);
    }

    private int resolveCurrentTarget(MealPlan existing, UserProfile profile) {
        if (existing.getTargetCalories() != null) {
            return existing.getTargetCalories();
        }
        if (profile.getTdeeCalories() != null) {
            return profile.getTdeeCalories();
        }
        return DEFAULT_CALORIES;
    }

    /**
     * Sube o baja el objetivo calórico según si la tendencia de peso reciente va en la
     * dirección del objetivo dietario declarado. Sin historial suficiente (menos de 2
     * registros o una ventana muy corta) no se toca el número — mejor no ajustar que
     * ajustar con datos insuficientes.
     */
    private int computeAdjustedTarget(Long userId, String dietaryGoal, int currentTarget) {
        List<ProgressEntry> entries = progressUseCase.listEntries(userId, "weight");
        if (entries.size() < 2) {
            return currentTarget;
        }
        List<ProgressEntry> sorted = entries.stream()
                .sorted(Comparator.comparing(ProgressEntry::getMeasuredAt))
                .toList();
        ProgressEntry first = sorted.get(0);
        ProgressEntry last = sorted.get(sorted.size() - 1);
        long days = Duration.between(first.getMeasuredAt(), last.getMeasuredAt()).toDays();
        if (days < MIN_HISTORY_DAYS) {
            return currentTarget;
        }
        double deltaKg = last.getValue().doubleValue() - first.getValue().doubleValue();
        double kgPerWeek = deltaKg / days * 7;

        String goal = dietaryGoal == null ? "maintain" : dietaryGoal;
        return switch (goal) {
            case "lose_fat" -> kgPerWeek > -FLAT_TREND_THRESHOLD_KG_PER_WEEK
                    ? currentTarget - ADJUSTMENT_STEP_KCAL : currentTarget;
            case "gain_muscle" -> kgPerWeek < FLAT_TREND_THRESHOLD_KG_PER_WEEK
                    ? currentTarget + ADJUSTMENT_STEP_KCAL : currentTarget;
            default -> Math.abs(kgPerWeek) > DRIFT_THRESHOLD_KG_PER_WEEK
                    ? currentTarget - (int) Math.signum(kgPerWeek) * ADJUSTMENT_STEP_KCAL : currentTarget;
        };
    }
}
