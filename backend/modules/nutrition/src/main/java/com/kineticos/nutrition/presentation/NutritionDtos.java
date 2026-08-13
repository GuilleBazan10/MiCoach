package com.kineticos.nutrition.presentation;

import com.kineticos.nutrition.domain.DailyIntakeEntry;
import com.kineticos.nutrition.domain.Ingredient;
import com.kineticos.nutrition.domain.MealPlan;
import com.kineticos.nutrition.domain.MealPlanDay;
import com.kineticos.nutrition.domain.MealPlanMeal;
import com.kineticos.nutrition.domain.Recipe;
import com.kineticos.nutrition.domain.RecipeIngredient;
import com.kineticos.nutrition.domain.ShoppingList;
import com.kineticos.nutrition.domain.ShoppingListItem;
import com.kineticos.nutrition.domain.Substitution;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * DTOs del módulo nutrition. Cada verbose class es un contrato de entrada/salida.
 */
public final class NutritionDtos {

    private NutritionDtos() {
    }

    // ------------------------- Catálogo -------------------------

    public record IngredientResponse(Long id, String name, String category, String baseUnit,
                                     BigDecimal caloriesPer100g, BigDecimal proteinPer100g,
                                     BigDecimal carbsPer100g, BigDecimal fatPer100g, BigDecimal fiberPer100g,
                                     boolean aiGenerated) {

        static IngredientResponse from(Ingredient i) {
            return new IngredientResponse(i.getId(), i.getName(), i.getCategory(), i.getBaseUnit(),
                    i.getCaloriesPer100g(), i.getProteinPer100g(), i.getCarbsPer100g(), i.getFatPer100g(),
                    i.getFiberPer100g(), i.isAiGenerated());
        }
    }

    public record RecipeIngredientResponse(Long ingredientId, String ingredientName, BigDecimal amount,
                                           String unit, Integer orderIndex) {

        static RecipeIngredientResponse from(RecipeIngredient ri) {
            return new RecipeIngredientResponse(ri.getIngredientId(), ri.getIngredientName(), ri.getAmount(),
                    ri.getUnit(), ri.getOrderIndex());
        }
    }

    public record RecipeResponse(Long id, String name, String description, String mealCategory,
                                 String difficulty, Integer servings, Integer prepTimeMin, Integer cookTimeMin,
                                 BigDecimal caloriesPerServing, BigDecimal proteinPerServing,
                                 BigDecimal carbsPerServing, BigDecimal fatPerServing, BigDecimal fiberPerServing,
                                 String instructions, String imageUrl, boolean aiGenerated,
                                 List<RecipeIngredientResponse> ingredients) {

        static RecipeResponse from(Recipe r) {
            return new RecipeResponse(r.getId(), r.getName(), r.getDescription(), r.getMealCategory(),
                    r.getDifficulty(), r.getServings(), r.getPrepTimeMin(), r.getCookTimeMin(),
                    r.getCaloriesPerServing(), r.getProteinPerServing(), r.getCarbsPerServing(),
                    r.getFatPerServing(), r.getFiberPerServing(), r.getInstructions(), r.getImageUrl(),
                    r.isAiGenerated(), r.getIngredients().stream().map(RecipeIngredientResponse::from).toList());
        }
    }

    public record SubstitutionResponse(Long id, Long ingredientId, Long substituteIngredientId,
                                       String substituteIngredientName, String reason, String notes) {

        static SubstitutionResponse from(Substitution s) {
            return new SubstitutionResponse(s.getId(), s.getIngredientId(), s.getSubstituteIngredientId(),
                    s.getSubstituteIngredientName(), s.getReason(), s.getNotes());
        }
    }

    // ------------------------- Planes de alimentación -------------------------

    public record MealPlanMealResponse(Long id, Long recipeId, String mealType, Integer orderIndex,
                                       BigDecimal servings, String notes) {

        static MealPlanMealResponse from(MealPlanMeal m) {
            return new MealPlanMealResponse(m.getId(), m.getRecipeId(), m.getMealType(), m.getOrderIndex(),
                    m.getServings(), m.getNotes());
        }
    }

    public record MealPlanMealRequest(Long recipeId, @NotBlank String mealType, @NotNull @Min(1) Integer orderIndex,
                                      BigDecimal servings, @Size(max = 500) String notes) {
    }

    public record MealPlanDayResponse(Long id, LocalDate planDate, List<MealPlanMealResponse> meals) {

        static MealPlanDayResponse from(MealPlanDay d) {
            return new MealPlanDayResponse(d.getId(), d.getPlanDate(),
                    d.getMeals().stream().map(MealPlanMealResponse::from).toList());
        }
    }

    public record MealPlanDayRequest(@NotNull LocalDate planDate, @Valid List<MealPlanMealRequest> meals) {
    }

    public record MealPlanResponse(Long id, Long userId, String name, String description, LocalDate startDate,
                                   LocalDate endDate, Integer targetCalories, BigDecimal targetProteinG,
                                   BigDecimal targetCarbsG, BigDecimal targetFatG, boolean aiGenerated,
                                   String status, List<MealPlanDayResponse> days) {

        static MealPlanResponse from(MealPlan p) {
            return new MealPlanResponse(p.getId(), p.getUserId(), p.getName(), p.getDescription(),
                    p.getStartDate(), p.getEndDate(), p.getTargetCalories(), p.getTargetProteinG(),
                    p.getTargetCarbsG(), p.getTargetFatG(), p.isAiGenerated(), p.getStatus(),
                    p.getDays().stream().map(MealPlanDayResponse::from).toList());
        }
    }

    public record MealPlanRequest(@NotBlank @Size(max = 200) String name, @Size(max = 1000) String description,
                                  @NotNull LocalDate startDate, @NotNull LocalDate endDate,
                                  Integer targetCalories, BigDecimal targetProteinG, BigDecimal targetCarbsG,
                                  BigDecimal targetFatG, @NotEmpty @Valid List<MealPlanDayRequest> days) {
    }

    // ------------------------- Diario alimentario -------------------------

    public record IntakeResponse(Long id, Long mealPlanMealId, Long recipeId, LocalDate foodDate,
                                 String mealType, BigDecimal amount, BigDecimal calories, BigDecimal proteinG,
                                 BigDecimal carbsG, BigDecimal fatG, Instant consumedAt) {

        static IntakeResponse from(DailyIntakeEntry e) {
            return new IntakeResponse(e.getId(), e.getMealPlanMealId(), e.getRecipeId(), e.getFoodDate(),
                    e.getMealType(), e.getAmount(), e.getCalories(), e.getProteinG(), e.getCarbsG(),
                    e.getFatG(), e.getConsumedAt());
        }
    }

    public record IntakeRequest(Long mealPlanMealId, Long recipeId, @NotNull LocalDate foodDate,
                                @NotBlank String mealType, BigDecimal amount, BigDecimal calories,
                                BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG) {
    }

    // ------------------------- Listas de compra -------------------------

    public record ShoppingListItemResponse(Long id, Long ingredientId, String itemName, BigDecimal amount,
                                           String unit, String category, boolean checked) {

        static ShoppingListItemResponse from(ShoppingListItem i) {
            return new ShoppingListItemResponse(i.getId(), i.getIngredientId(), i.getItemName(), i.getAmount(),
                    i.getUnit(), i.getCategory(), i.isChecked());
        }
    }

    public record ShoppingListItemRequest(Long ingredientId, @Size(max = 200) String itemName,
                                          BigDecimal amount, @Size(max = 20) String unit,
                                          @Size(max = 50) String category) {
    }

    public record ShoppingListItemCheckRequest(@NotNull Boolean checked) {
    }

    public record ShoppingListResponse(Long id, String name, LocalDate weekStart,
                                       List<ShoppingListItemResponse> items) {

        static ShoppingListResponse from(ShoppingList l) {
            return new ShoppingListResponse(l.getId(), l.getName(), l.getWeekStart(),
                    l.getItems().stream().map(ShoppingListItemResponse::from).toList());
        }
    }

    public record ShoppingListRequest(@Size(max = 200) String name, LocalDate weekStart) {
    }
}
