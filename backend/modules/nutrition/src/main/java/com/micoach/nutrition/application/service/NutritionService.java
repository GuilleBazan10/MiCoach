package com.micoach.nutrition.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.nutrition.application.port.in.NutritionUseCase;
import com.micoach.nutrition.application.port.out.NutritionRepository;
import com.micoach.nutrition.domain.DailyIntakeEntry;
import com.micoach.nutrition.domain.Ingredient;
import com.micoach.nutrition.domain.MealPlan;
import com.micoach.nutrition.domain.MealPlanDay;
import com.micoach.nutrition.domain.MealPlanMeal;
import com.micoach.nutrition.domain.Recipe;
import com.micoach.nutrition.domain.ShoppingList;
import com.micoach.nutrition.domain.ShoppingListItem;
import com.micoach.nutrition.domain.Substitution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de casos de uso del módulo nutrition. Depende solo del puerto de salida.
 */
@Service
public class NutritionService implements NutritionUseCase {

    private final NutritionRepository repository;
    private final NutritionAiGenerator aiGenerator;
    private final NutritionSubstitutionAiGenerator substitutionAiGenerator;
    private final NutritionCalorieAdjuster calorieAdjuster;

    public NutritionService(NutritionRepository repository, NutritionAiGenerator aiGenerator,
                            NutritionSubstitutionAiGenerator substitutionAiGenerator,
                            NutritionCalorieAdjuster calorieAdjuster) {
        this.repository = repository;
        this.aiGenerator = aiGenerator;
        this.substitutionAiGenerator = substitutionAiGenerator;
        this.calorieAdjuster = calorieAdjuster;
    }

    // ------------------------- Catálogo -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Ingredient> listIngredients(IngredientFilter filter) {
        return repository.findIngredients(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Ingredient getIngredient(Long ingredientId) {
        return repository.findIngredientById(ingredientId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Ingrediente no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recipe> listRecipes(RecipeFilter filter) {
        return repository.findRecipes(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Recipe getRecipe(Long recipeId) {
        return repository.findRecipeById(recipeId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Receta no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Substitution> listSubstitutions(Long ingredientId) {
        return repository.findSubstitutions(ingredientId);
    }

    @Override
    @Transactional
    public Substitution generateSubstitution(Long userId, Long ingredientId, SubstitutionRequestData data) {
        Ingredient ingredient = getIngredient(ingredientId);
        List<Ingredient> catalog = repository.findIngredients(new IngredientFilter(null, null));
        Substitution substitution = substitutionAiGenerator.generate(userId, ingredient, data, catalog);
        return repository.saveSubstitution(substitution);
    }

    // ------------------------- Planes de alimentación -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<MealPlan> listMealPlans(Long userId) {
        return repository.findMealPlansByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlan getMealPlan(Long userId, Long mealPlanId) {
        return requireOwnedMealPlan(userId, mealPlanId);
    }

    @Override
    @Transactional
    public MealPlan createMealPlan(Long userId, MealPlanData data) {
        MealPlan mealPlan = MealPlan.create(userId, data.name(), data.description(), data.startDate(),
                data.endDate(), data.targetCalories(), data.targetProteinG(), data.targetCarbsG(),
                data.targetFatG(), toDays(data.days()));
        return repository.saveMealPlan(mealPlan);
    }

    @Override
    @Transactional
    public MealPlan updateMealPlan(Long userId, Long mealPlanId, MealPlanData data) {
        MealPlan mealPlan = requireOwnedMealPlan(userId, mealPlanId);
        mealPlan.update(data.name(), data.description(), data.startDate(), data.endDate(),
                data.targetCalories(), data.targetProteinG(), data.targetCarbsG(), data.targetFatG(),
                toDays(data.days()));
        return repository.saveMealPlan(mealPlan);
    }

    @Override
    @Transactional
    public MealPlan adjustMealPlanCalories(Long userId, Long mealPlanId) {
        MealPlan existing = requireOwnedMealPlan(userId, mealPlanId);
        List<Recipe> catalog = repository.findRecipes(new RecipeFilter(null, null, null));
        MealPlanData data = calorieAdjuster.adjust(userId, existing, catalog);
        existing.update(existing.getName(), existing.getDescription(), data.startDate(), data.endDate(),
                data.targetCalories(), data.targetProteinG(), data.targetCarbsG(), data.targetFatG(),
                toDays(data.days()));
        return repository.saveMealPlan(existing);
    }

    @Override
    @Transactional
    public MealPlan generateMealPlan(Long userId, String goal) {
        List<Recipe> catalog = repository.findRecipes(new RecipeFilter(null, null, null));
        MealPlanData data = aiGenerator.generate(userId, goal, catalog);
        MealPlan mealPlan = MealPlan.createAiGenerated(userId, data.name(), data.description(),
                data.startDate(), data.endDate(), data.targetCalories(), data.targetProteinG(),
                data.targetCarbsG(), data.targetFatG(), toDays(data.days()));
        return repository.saveMealPlan(mealPlan);
    }

    @Override
    @Transactional
    public void deleteMealPlan(Long userId, Long mealPlanId) {
        requireOwnedMealPlan(userId, mealPlanId);
        repository.deleteMealPlan(mealPlanId);
    }

    private List<MealPlanDay> toDays(List<MealPlanDayData> days) {
        if (days == null) {
            return List.of();
        }
        return days.stream().map(d -> MealPlanDay.create(d.planDate(), toMeals(d.meals()))).toList();
    }

    private List<MealPlanMeal> toMeals(List<MealPlanMealData> meals) {
        if (meals == null) {
            return List.of();
        }
        return meals.stream()
                .map(m -> MealPlanMeal.create(m.recipeId(), m.mealType(), m.orderIndex(), m.servings(),
                        m.notes()))
                .toList();
    }

    private MealPlan requireOwnedMealPlan(Long userId, Long mealPlanId) {
        MealPlan mealPlan = repository.findMealPlanById(mealPlanId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Plan no encontrado"));
        if (!mealPlan.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Plan no encontrado");
        }
        return mealPlan;
    }

    // ------------------------- Diario alimentario -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<DailyIntakeEntry> listIntake(Long userId, LocalDate foodDate) {
        return repository.findIntakeByUser(userId, foodDate);
    }

    @Override
    @Transactional
    public DailyIntakeEntry logIntake(Long userId, IntakeData data) {
        DailyIntakeEntry entry = DailyIntakeEntry.create(userId, data.mealPlanMealId(), data.recipeId(),
                data.foodDate(), data.mealType(), data.amount(), data.calories(), data.proteinG(),
                data.carbsG(), data.fatG());
        return repository.saveIntake(entry);
    }

    @Override
    @Transactional
    public void deleteIntake(Long userId, Long intakeId) {
        DailyIntakeEntry entry = repository.findIntakeById(intakeId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado"));
        if (!entry.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado");
        }
        repository.deleteIntake(intakeId);
    }

    // ------------------------- Listas de compra -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ShoppingList> listShoppingLists(Long userId) {
        return repository.findShoppingListsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingList getShoppingList(Long userId, Long shoppingListId) {
        return requireOwnedShoppingList(userId, shoppingListId);
    }

    @Override
    @Transactional
    public ShoppingList createShoppingList(Long userId, ShoppingListData data) {
        return repository.saveShoppingList(ShoppingList.create(userId, data.name(), data.weekStart()));
    }

    @Override
    @Transactional
    public void deleteShoppingList(Long userId, Long shoppingListId) {
        requireOwnedShoppingList(userId, shoppingListId);
        repository.deleteShoppingList(shoppingListId);
    }

    @Override
    @Transactional
    public ShoppingListItem addShoppingListItem(Long userId, Long shoppingListId, ShoppingListItemData data) {
        requireOwnedShoppingList(userId, shoppingListId);
        ShoppingListItem item = ShoppingListItem.create(data.ingredientId(), data.itemName(), data.amount(),
                data.unit(), data.category());
        return repository.saveShoppingListItem(shoppingListId, item);
    }

    @Override
    @Transactional
    public ShoppingListItem setShoppingListItemChecked(Long userId, Long shoppingListId, Long itemId,
                                                        boolean checked) {
        requireOwnedShoppingList(userId, shoppingListId);
        ShoppingListItem item = repository.findShoppingListItemById(itemId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Ítem no encontrado"));
        item.setChecked(checked);
        return repository.saveShoppingListItem(shoppingListId, item);
    }

    @Override
    @Transactional
    public void deleteShoppingListItem(Long userId, Long shoppingListId, Long itemId) {
        requireOwnedShoppingList(userId, shoppingListId);
        repository.deleteShoppingListItem(itemId);
    }

    private ShoppingList requireOwnedShoppingList(Long userId, Long shoppingListId) {
        ShoppingList shoppingList = repository.findShoppingListById(shoppingListId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Lista no encontrada"));
        if (!shoppingList.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Lista no encontrada");
        }
        return shoppingList;
    }
}
