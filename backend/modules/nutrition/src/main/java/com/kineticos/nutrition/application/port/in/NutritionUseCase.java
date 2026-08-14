package com.kineticos.nutrition.application.port.in;

import com.kineticos.nutrition.domain.DailyIntakeEntry;
import com.kineticos.nutrition.domain.Ingredient;
import com.kineticos.nutrition.domain.MealPlan;
import com.kineticos.nutrition.domain.Recipe;
import com.kineticos.nutrition.domain.ShoppingList;
import com.kineticos.nutrition.domain.ShoppingListItem;
import com.kineticos.nutrition.domain.Substitution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de entrada del módulo nutrition (catálogo, planes, diario y listas de compra).
 */
public interface NutritionUseCase {

    // ------------------------- Catálogo -------------------------

    List<Ingredient> listIngredients(IngredientFilter filter);

    Ingredient getIngredient(Long ingredientId);

    List<Recipe> listRecipes(RecipeFilter filter);

    Recipe getRecipe(Long recipeId);

    List<Substitution> listSubstitutions(Long ingredientId);

    /**
     * Genera con IA una sustitución para un ingrediente (alergia/intolerancia/no
     * disponible/preferencia), la resuelve contra el catálogo real y la persiste en
     * {@code nutrition_substitutions} para reutilizarla después vía
     * {@link #listSubstitutions}.
     */
    Substitution generateSubstitution(Long userId, Long ingredientId, SubstitutionRequestData data);

    // ------------------------- Planes de alimentación -------------------------

    List<MealPlan> listMealPlans(Long userId);

    MealPlan getMealPlan(Long userId, Long mealPlanId);

    MealPlan createMealPlan(Long userId, MealPlanData data);

    /**
     * Genera un plan de alimentación con IA (LangChain4j + Ollama, ver módulo
     * {@code ai}) a partir de un pedido en lenguaje natural y del perfil real del
     * usuario, lo crea como propio ({@code aiGenerated = true}) y lo devuelve ya
     * persistido.
     */
    MealPlan generateMealPlan(Long userId, String goal);

    MealPlan updateMealPlan(Long userId, Long mealPlanId, MealPlanData data);

    /**
     * Recalcula el objetivo calórico de un plan existente según la tendencia de peso
     * reciente del usuario vs. su objetivo dietario, y regenera las comidas con IA
     * dentro de ese nuevo presupuesto (mismo rango de días que el plan original).
     */
    MealPlan adjustMealPlanCalories(Long userId, Long mealPlanId);

    void deleteMealPlan(Long userId, Long mealPlanId);

    // ------------------------- Diario alimentario -------------------------

    List<DailyIntakeEntry> listIntake(Long userId, LocalDate foodDate);

    DailyIntakeEntry logIntake(Long userId, IntakeData data);

    void deleteIntake(Long userId, Long intakeId);

    // ------------------------- Listas de compra -------------------------

    List<ShoppingList> listShoppingLists(Long userId);

    ShoppingList getShoppingList(Long userId, Long shoppingListId);

    ShoppingList createShoppingList(Long userId, ShoppingListData data);

    void deleteShoppingList(Long userId, Long shoppingListId);

    ShoppingListItem addShoppingListItem(Long userId, Long shoppingListId, ShoppingListItemData data);

    ShoppingListItem setShoppingListItemChecked(Long userId, Long shoppingListId, Long itemId, boolean checked);

    void deleteShoppingListItem(Long userId, Long shoppingListId, Long itemId);

    record IngredientFilter(String category, String search) {
    }

    /** {@code reason}: allergy|intolerance|unavailable|preference (CHECK de nutrition_substitutions). */
    record SubstitutionRequestData(String reason, String notes) {
    }

    record RecipeFilter(String mealCategory, String difficulty, String search) {
    }

    record MealPlanData(String name, String description, LocalDate startDate, LocalDate endDate,
                        Integer targetCalories, BigDecimal targetProteinG, BigDecimal targetCarbsG,
                        BigDecimal targetFatG, List<MealPlanDayData> days) {
    }

    record MealPlanDayData(LocalDate planDate, List<MealPlanMealData> meals) {
    }

    record MealPlanMealData(Long recipeId, String mealType, Integer orderIndex, BigDecimal servings,
                            String notes) {
    }

    record IntakeData(Long mealPlanMealId, Long recipeId, LocalDate foodDate, String mealType,
                      BigDecimal amount, BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG,
                      BigDecimal fatG) {
    }

    record ShoppingListData(String name, LocalDate weekStart) {
    }

    record ShoppingListItemData(Long ingredientId, String itemName, BigDecimal amount, String unit,
                                String category) {
    }
}
