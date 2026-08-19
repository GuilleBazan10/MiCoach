package com.micoach.nutrition.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.AuditLogEvent;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de casos de uso del módulo nutrition. Depende solo del puerto de salida.
 */
@Slf4j
@Service
public class NutritionService implements NutritionUseCase {

    private final NutritionRepository repository;
    private final NutritionAiGenerator aiGenerator;
    private final NutritionSubstitutionAiGenerator substitutionAiGenerator;
    private final NutritionCalorieAdjuster calorieAdjuster;
    private final ApplicationEventPublisher eventPublisher;

    public NutritionService(NutritionRepository repository, NutritionAiGenerator aiGenerator,
                            NutritionSubstitutionAiGenerator substitutionAiGenerator,
                            NutritionCalorieAdjuster calorieAdjuster,
                            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.aiGenerator = aiGenerator;
        this.substitutionAiGenerator = substitutionAiGenerator;
        this.calorieAdjuster = calorieAdjuster;
        this.eventPublisher = eventPublisher;
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
        log.info("Iniciando generación de sustitución con IA para ingrediente ID: {} por el usuario ID: {}", ingredientId, userId);
        Ingredient ingredient = getIngredient(ingredientId);
        List<Ingredient> catalog = repository.findIngredients(new IngredientFilter(null, null));
        Substitution substitution = substitutionAiGenerator.generate(userId, ingredient, data, catalog);
        Substitution saved = repository.saveSubstitution(substitution);
        
        log.info("Sustitución con IA generada y guardada exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "SUBSTITUTION_GENERATE", "SUBSTITUTION", saved.getId()));
        return saved;
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
        log.info("Creando plan de alimentación para el usuario ID: {} (Nombre: {})", userId, data.name());
        MealPlan mealPlan = MealPlan.create(userId, data.name(), data.description(), data.startDate(),
                data.endDate(), data.targetCalories(), data.targetProteinG(), data.targetCarbsG(),
                data.targetFatG(), toDays(data.days()));
        MealPlan saved = repository.saveMealPlan(mealPlan);
        
        log.info("Plan de alimentación creado exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEAL_PLAN_CREATE", "MEAL_PLAN", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public MealPlan updateMealPlan(Long userId, Long mealPlanId, MealPlanData data) {
        log.info("Actualizando plan de alimentación ID: {} para el usuario ID: {}", mealPlanId, userId);
        MealPlan mealPlan = requireOwnedMealPlan(userId, mealPlanId);
        mealPlan.update(data.name(), data.description(), data.startDate(), data.endDate(),
                data.targetCalories(), data.targetProteinG(), data.targetCarbsG(), data.targetFatG(),
                toDays(data.days()));
        MealPlan saved = repository.saveMealPlan(mealPlan);
        
        log.info("Plan de alimentación ID: {} actualizado exitosamente para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEAL_PLAN_UPDATE", "MEAL_PLAN", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public MealPlan adjustMealPlanCalories(Long userId, Long mealPlanId) {
        log.info("Ajustando calorías del plan de alimentación ID: {} para el usuario ID: {}", mealPlanId, userId);
        MealPlan existing = requireOwnedMealPlan(userId, mealPlanId);
        List<Recipe> catalog = repository.findRecipes(new RecipeFilter(null, null, null));
        MealPlanData data = calorieAdjuster.adjust(userId, existing, catalog);
        existing.update(existing.getName(), existing.getDescription(), data.startDate(), data.endDate(),
                data.targetCalories(), data.targetProteinG(), data.targetCarbsG(), data.targetFatG(),
                toDays(data.days()));
        MealPlan saved = repository.saveMealPlan(existing);
        
        log.info("Calorías ajustadas exitosamente para el plan de alimentación ID: {} del usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEAL_PLAN_ADJUST", "MEAL_PLAN", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public MealPlan generateMealPlan(Long userId, String goal) {
        log.info("Iniciando generación de plan alimenticio con IA para el usuario ID: {} con objetivo: {}", userId, goal);
        List<Recipe> catalog = repository.findRecipes(new RecipeFilter(null, null, null));
        MealPlanData data = aiGenerator.generate(userId, goal, catalog);
        MealPlan mealPlan = MealPlan.createAiGenerated(userId, data.name(), data.description(),
                data.startDate(), data.endDate(), data.targetCalories(), data.targetProteinG(),
                data.targetCarbsG(), data.targetFatG(), toDays(data.days()));
        MealPlan saved = repository.saveMealPlan(mealPlan);
        
        log.info("Plan alimenticio con IA generado y guardado exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEAL_PLAN_GENERATE", "MEAL_PLAN", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteMealPlan(Long userId, Long mealPlanId) {
        log.info("Eliminando plan de alimentación ID: {} para el usuario ID: {}", mealPlanId, userId);
        requireOwnedMealPlan(userId, mealPlanId);
        repository.deleteMealPlan(mealPlanId);
        
        log.info("Plan de alimentación ID: {} eliminado exitosamente para el usuario ID: {}", mealPlanId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEAL_PLAN_DELETE", "MEAL_PLAN", mealPlanId));
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
        log.info("Registrando consumo alimentario diario para el usuario ID: {} (Fecha: {}, Tipo comida: {}, Calorías: {})", 
                userId, data.foodDate(), data.mealType(), data.calories());
        DailyIntakeEntry entry = DailyIntakeEntry.create(userId, data.mealPlanMealId(), data.recipeId(),
                data.foodDate(), data.mealType(), data.amount(), data.calories(), data.proteinG(),
                data.carbsG(), data.fatG());
        DailyIntakeEntry saved = repository.saveIntake(entry);
        
        log.info("Consumo diario registrado exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "INTAKE_LOG", "DAILY_INTAKE", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteIntake(Long userId, Long intakeId) {
        log.info("Eliminando consumo diario ID: {} para el usuario ID: {}", intakeId, userId);
        DailyIntakeEntry entry = repository.findIntakeById(intakeId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado"));
        if (!entry.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado");
        }
        repository.deleteIntake(intakeId);
        
        log.info("Consumo diario ID: {} eliminado exitosamente para el usuario ID: {}", intakeId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "INTAKE_DELETE", "DAILY_INTAKE", intakeId));
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
