package com.kineticos.nutrition.infrastructure.persistence;

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

import java.util.List;

final class NumberConversions {

    private NumberConversions() {
    }

    static Integer intValue(Short v) {
        return v == null ? null : v.intValue();
    }

    static Short shortValue(Integer v) {
        return v == null ? null : v.shortValue();
    }
}

final class IngredientMapper {

    private IngredientMapper() {
    }

    static Ingredient toDomain(IngredientJpa jpa) {
        return Ingredient.restore(jpa.getId(), jpa.getName(), jpa.getCategory(), jpa.getBaseUnit(),
                jpa.getCaloriesPer100g(), jpa.getProteinPer100g(), jpa.getCarbsPer100g(), jpa.getFatPer100g(),
                jpa.getFiberPer100g(), jpa.isAiGenerated(), jpa.isActive(), jpa.getCreatedAt(),
                jpa.getUpdatedAt());
    }
}

final class RecipeIngredientMapper {

    private RecipeIngredientMapper() {
    }

    static RecipeIngredient toDomain(RecipeIngredientJpa jpa, String ingredientName) {
        return RecipeIngredient.restore(jpa.getIngredientId(), ingredientName, jpa.getAmount(), jpa.getUnit(),
                NumberConversions.intValue(jpa.getOrderIndex()));
    }
}

final class RecipeMapper {

    private RecipeMapper() {
    }

    static Recipe toDomain(RecipeJpa jpa, List<RecipeIngredient> ingredients) {
        return Recipe.restore(jpa.getId(), jpa.getName(), jpa.getDescription(), jpa.getMealCategory(),
                jpa.getDifficulty(), NumberConversions.intValue(jpa.getServings()),
                NumberConversions.intValue(jpa.getPrepTimeMin()), NumberConversions.intValue(jpa.getCookTimeMin()),
                jpa.getCaloriesPerServing(), jpa.getProteinPerServing(), jpa.getCarbsPerServing(),
                jpa.getFatPerServing(), jpa.getFiberPerServing(), jpa.getInstructions(), jpa.getImageUrl(),
                jpa.isAiGenerated(), jpa.isActive(), ingredients, jpa.getCreatedAt(), jpa.getUpdatedAt());
    }
}

final class MealPlanMapper {

    private MealPlanMapper() {
    }

    static MealPlan toDomain(MealPlanJpa jpa, List<MealPlanDay> days) {
        return MealPlan.restore(jpa.getId(), jpa.getUserId(), jpa.getName(), jpa.getDescription(),
                jpa.getStartDate(), jpa.getEndDate(), jpa.getTargetCalories(), jpa.getTargetProteinG(),
                jpa.getTargetCarbsG(), jpa.getTargetFatG(), jpa.isAiGenerated(), jpa.getStatus(), days,
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static MealPlanJpa toJpa(MealPlan domain) {
        return MealPlanJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .targetCalories(domain.getTargetCalories())
                .targetProteinG(domain.getTargetProteinG())
                .targetCarbsG(domain.getTargetCarbsG())
                .targetFatG(domain.getTargetFatG())
                .aiGenerated(domain.isAiGenerated())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class MealPlanDayMapper {

    private MealPlanDayMapper() {
    }

    static MealPlanDay toDomain(MealPlanDayJpa jpa, List<MealPlanMeal> meals) {
        return MealPlanDay.restore(jpa.getId(), jpa.getPlanDate(), meals);
    }

    static MealPlanDayJpa toJpa(MealPlanDay domain, Long mealPlanId) {
        return MealPlanDayJpa.builder()
                .mealPlanId(mealPlanId)
                .planDate(domain.getPlanDate())
                .build();
    }
}

final class MealPlanMealMapper {

    private MealPlanMealMapper() {
    }

    static MealPlanMeal toDomain(MealPlanMealJpa jpa) {
        return MealPlanMeal.restore(jpa.getId(), jpa.getRecipeId(), jpa.getMealType(),
                NumberConversions.intValue(jpa.getOrderIndex()), jpa.getServings(), jpa.getNotes());
    }

    static MealPlanMealJpa toJpa(MealPlanMeal domain, Long mealPlanDayId) {
        return MealPlanMealJpa.builder()
                .mealPlanDayId(mealPlanDayId)
                .recipeId(domain.getRecipeId())
                .mealType(domain.getMealType())
                .orderIndex(NumberConversions.shortValue(domain.getOrderIndex()))
                .servings(domain.getServings())
                .notes(domain.getNotes())
                .build();
    }
}

final class SubstitutionMapper {

    private SubstitutionMapper() {
    }

    static Substitution toDomain(SubstitutionJpa jpa, String substituteIngredientName) {
        return Substitution.restore(jpa.getId(), jpa.getRecipeId(), jpa.getIngredientId(),
                jpa.getSubstituteIngredientId(), substituteIngredientName, jpa.getReason(), jpa.getNotes(),
                jpa.getCreatedAt());
    }
}

final class DailyIntakeMapper {

    private DailyIntakeMapper() {
    }

    static DailyIntakeEntry toDomain(DailyIntakeJpa jpa) {
        return DailyIntakeEntry.restore(jpa.getId(), jpa.getUserId(), jpa.getMealPlanMealId(), jpa.getRecipeId(),
                jpa.getFoodDate(), jpa.getMealType(), jpa.getAmount(), jpa.getCalories(), jpa.getProteinG(),
                jpa.getCarbsG(), jpa.getFatG(), jpa.getConsumedAt());
    }

    static DailyIntakeJpa toJpa(DailyIntakeEntry domain) {
        return DailyIntakeJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .mealPlanMealId(domain.getMealPlanMealId())
                .recipeId(domain.getRecipeId())
                .foodDate(domain.getFoodDate())
                .mealType(domain.getMealType())
                .amount(domain.getAmount())
                .calories(domain.getCalories())
                .proteinG(domain.getProteinG())
                .carbsG(domain.getCarbsG())
                .fatG(domain.getFatG())
                .consumedAt(domain.getConsumedAt())
                .build();
    }
}

final class ShoppingListMapper {

    private ShoppingListMapper() {
    }

    static ShoppingList toDomain(ShoppingListJpa jpa, List<ShoppingListItem> items) {
        return ShoppingList.restore(jpa.getId(), jpa.getUserId(), jpa.getName(), jpa.getWeekStart(), items,
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static ShoppingListJpa toJpa(ShoppingList domain) {
        return ShoppingListJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .weekStart(domain.getWeekStart())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class ShoppingListItemMapper {

    private ShoppingListItemMapper() {
    }

    static ShoppingListItem toDomain(ShoppingListItemJpa jpa) {
        return ShoppingListItem.restore(jpa.getId(), jpa.getIngredientId(), jpa.getItemName(), jpa.getAmount(),
                jpa.getUnit(), jpa.getCategory(), jpa.isChecked());
    }

    static ShoppingListItemJpa toJpa(ShoppingListItem domain, Long shoppingListId) {
        return ShoppingListItemJpa.builder()
                .id(domain.getId())
                .shoppingListId(shoppingListId)
                .ingredientId(domain.getIngredientId())
                .itemName(domain.getItemName())
                .amount(domain.getAmount())
                .unit(domain.getUnit())
                .category(domain.getCategory())
                .checked(domain.isChecked())
                .build();
    }
}
