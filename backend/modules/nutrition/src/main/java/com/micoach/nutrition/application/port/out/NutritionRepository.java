package com.micoach.nutrition.application.port.out;

import com.micoach.nutrition.application.port.in.NutritionUseCase.IngredientFilter;
import com.micoach.nutrition.application.port.in.NutritionUseCase.RecipeFilter;
import com.micoach.nutrition.domain.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NutritionRepository {
    List<Ingredient> findIngredients(IngredientFilter filter);
    Optional<Ingredient> findIngredientById(Long ingredientId);
    List<Recipe> findRecipes(RecipeFilter filter);
    Optional<Recipe> findRecipeById(Long recipeId);
    List<Substitution> findSubstitutions(Long ingredientId);
    Substitution saveSubstitution(Substitution substitution);
    List<MealPlan> findMealPlansByUser(Long userId);
    Optional<MealPlan> findMealPlanById(Long mealPlanId);
    MealPlan saveMealPlan(MealPlan mealPlan);
    void deleteMealPlan(Long mealPlanId);
    List<DailyIntakeEntry> findIntakeByUser(Long userId, LocalDate foodDate);
    Optional<DailyIntakeEntry> findIntakeById(Long intakeId);
    DailyIntakeEntry saveIntake(DailyIntakeEntry entry);
    void deleteIntake(Long intakeId);
    List<ShoppingList> findShoppingListsByUser(Long userId);
    Optional<ShoppingList> findShoppingListById(Long shoppingListId);
    ShoppingList saveShoppingList(ShoppingList shoppingList);
    void deleteShoppingList(Long shoppingListId);
    ShoppingListItem saveShoppingListItem(Long shoppingListId, ShoppingListItem item);
    Optional<ShoppingListItem> findShoppingListItemById(Long itemId);
    void deleteShoppingListItem(Long itemId);
}
