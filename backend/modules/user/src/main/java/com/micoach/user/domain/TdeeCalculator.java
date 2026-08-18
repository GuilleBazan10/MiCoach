package com.micoach.user.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

/**
 * BMR (Mifflin-St Jeor) → TDEE (factor de actividad) → ajuste por objetivo dietario.
 * docs/10-recomendaciones-coach-nutricion.md § B.1: sin esto, "plan para bajar de peso"
 * o "ganar músculo" son frases sin ningún ancla numérica real — la IA de nutrición ya
 * usa {@code tdeeCalories} cuando está presente, pero nunca se calculaba, siempre
 * quedaba en null.
 */
public final class TdeeCalculator {

    private TdeeCalculator() {
    }

    private static final Map<String, Double> ACTIVITY_FACTORS = Map.of(
            "sedentary", 1.2,
            "light", 1.375,
            "moderate", 1.55,
            "active", 1.725,
            "very_active", 1.9
    );

    // Ajuste sobre el TDEE de mantenimiento según objetivo (déficit/superávit moderado).
    private static final Map<String, Integer> GOAL_ADJUSTMENT_KCAL = Map.of(
            "lose_fat", -500,
            "gain_muscle", 400,
            "maintain", 0,
            "endurance", 0,
            "health", 0
    );

    /**
     * Null si falta algún dato requerido (sexo, fecha de nacimiento, altura, peso o un
     * nivel de actividad reconocido) — no se inventa un número sin base real.
     */
    public static Integer calculate(String sex, LocalDate birthDate, BigDecimal heightCm, BigDecimal weightKg,
                                    String activityLevel, String dietaryGoal) {
        if (sex == null || birthDate == null || heightCm == null || weightKg == null || activityLevel == null) {
            return null;
        }
        Double activityFactor = ACTIVITY_FACTORS.get(activityLevel);
        if (activityFactor == null) {
            return null;
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        // Mifflin-St Jeor no distingue más de dos sexos biológicos; para "other" se
        // promedia el offset como estimación razonable en vez de forzar uno de los dos.
        double sexOffset = switch (sex) {
            case "male" -> 5;
            case "female" -> -161;
            default -> -78; // (5 + -161) / 2
        };
        double bmr = 10 * weightKg.doubleValue() + 6.25 * heightCm.doubleValue() - 5 * age + sexOffset;
        double tdee = bmr * activityFactor;
        int adjustment = GOAL_ADJUSTMENT_KCAL.getOrDefault(dietaryGoal, 0);
        return (int) Math.round(tdee + adjustment);
    }
}
