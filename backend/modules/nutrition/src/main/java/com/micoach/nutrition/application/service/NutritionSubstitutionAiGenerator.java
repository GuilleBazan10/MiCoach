package com.micoach.nutrition.application.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.nutrition.application.port.in.NutritionUseCase.SubstitutionRequestData;
import com.micoach.nutrition.domain.Ingredient;
import com.micoach.nutrition.domain.Substitution;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Arma el pedido al módulo {@code ai} (prompt {@code ingredient_substitution}) y traduce
 * su respuesta a un {@link Substitution} sin persistir todavía (lo persiste
 * {@code NutritionService}, mismo reparto de responsabilidades que
 * {@code WorkoutAiGenerator}/{@code NutritionAiGenerator}).
 */
@Component
class NutritionSubstitutionAiGenerator {

    private static final String PROMPT_SLUG = "ingredient_substitution";
    private static final Set<String> VALID_REASONS = Set.of("allergy", "intolerance", "unavailable", "preference");
    private static final Map<String, String> REASON_LABELS = Map.of(
            "allergy", "alergia",
            "intolerance", "intolerancia",
            "unavailable", "no tiene el ingrediente disponible",
            "preference", "preferencia personal");

    private final AiUseCase aiUseCase;
    private final UserProfileUseCase userProfileUseCase;
    private final ObjectMapper objectMapper;

    NutritionSubstitutionAiGenerator(AiUseCase aiUseCase, UserProfileUseCase userProfileUseCase,
                                     ObjectMapper objectMapper) {
        this.aiUseCase = aiUseCase;
        this.userProfileUseCase = userProfileUseCase;
        this.objectMapper = objectMapper;
    }

    Substitution generate(Long userId, Ingredient original, SubstitutionRequestData data, List<Ingredient> catalog) {
        String reason = normalizeReason(data.reason());

        List<Ingredient> sameCategory = catalog.stream()
                .filter(i -> !i.getId().equals(original.getId()))
                .filter(i -> original.getCategory() == null || original.getCategory().equals(i.getCategory()))
                .toList();
        // Si la categoría del original deja muy pocas opciones (o no tiene categoría),
        // se abre al catálogo completo en vez de forzar un mal sustituto.
        List<Ingredient> candidates = sameCategory.size() >= 5 ? sameCategory
                : catalog.stream().filter(i -> !i.getId().equals(original.getId())).toList();

        String catalogText = candidates.stream().map(Ingredient::getName).map(n -> "- " + n)
                .reduce((a, b) -> a + "\n" + b).orElse("");

        AiUseCase.GenerationResult result = aiUseCase.generate(userId, PROMPT_SLUG, Map.of(
                "ingredient", original.getName(),
                "macros", describeMacros(original),
                "reason", REASON_LABELS.get(reason),
                "notes", data.notes() == null || data.notes().isBlank() ? "sin detalle adicional" : data.notes(),
                "profile", buildProfileText(userId),
                "catalog", catalogText));

        AiSubstitutionJson parsed = parseJson(result.rawOutput());
        Long substituteId = resolveIngredientId(parsed.substituteName(), candidates);
        if (substituteId == null) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "La IA no encontró un sustituto válido en el catálogo");
        }
        String explanation = truncate(parsed.explanation(), 500);
        return Substitution.create(original.getId(), substituteId, null, reason, explanation);
    }

    private String normalizeReason(String reason) {
        if (reason != null && VALID_REASONS.contains(reason.trim().toLowerCase())) {
            return reason.trim().toLowerCase();
        }
        throw new DomainException(400, ErrorCode.VALIDATION_ERROR,
                "reason debe ser uno de: " + VALID_REASONS);
    }

    private String describeMacros(Ingredient i) {
        return String.format("%s kcal, %sg proteína, %sg carbohidratos, %sg grasa (por 100%s)",
                i.getCaloriesPer100g(), i.getProteinPer100g(), i.getCarbsPer100g(), i.getFatPer100g(), i.getBaseUnit());
    }

    /**
     * Perfil del usuario (patologías) — un sustituto elegido sin tener en cuenta, por
     * ejemplo, diabetes reportada podría ser técnicamente "sin lácteos" pero igual
     * inadecuado.
     */
    private String buildProfileText(Long userId) {
        UserProfile profile = userProfileUseCase.getOrCreateProfile(userId);
        List<UserPathology> pathologies = userProfileUseCase.getPathologies(userId);

        StringBuilder text = new StringBuilder();
        if (profile.getDietaryGoal() != null) {
            text.append("- Objetivo dietario: ").append(profile.getDietaryGoal()).append('\n');
        }
        if (!pathologies.isEmpty()) {
            String list = pathologies.stream().map(UserPathology::getPathology)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            text.append("- Patologías reportadas (IMPORTANTE, tenerlas en cuenta): ").append(list).append('\n');
        }
        return text.isEmpty() ? "Sin datos de perfil cargados." : text.toString();
    }

    private AiSubstitutionJson parseJson(String raw) {
        String jsonSlice = extractFirstJsonObject(raw);
        if (jsonSlice == null) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR, "La IA no devolvió un JSON válido");
        }
        try {
            return objectMapper.readValue(jsonSlice, AiSubstitutionJson.class);
        } catch (Exception e) {
            throw new DomainException(502, ErrorCode.INTERNAL_ERROR,
                    "No se pudo interpretar la sustitución generada por la IA: " + e.getMessage());
        }
    }

    // Ver WorkoutAiGenerator.extractFirstJsonObject: mismo enfoque de llaves balanceadas.
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

    private Long resolveIngredientId(String name, List<Ingredient> candidates) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String normalized = name.trim().toLowerCase();
        return candidates.stream()
                .filter(i -> i.getName().trim().equalsIgnoreCase(normalized))
                .findFirst()
                .or(() -> candidates.stream()
                        .filter(i -> i.getName().toLowerCase().contains(normalized)
                                || normalized.contains(i.getName().toLowerCase()))
                        .findFirst())
                .map(Ingredient::getId)
                .orElse(null);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiSubstitutionJson(String substituteName, String explanation) {
    }
}
