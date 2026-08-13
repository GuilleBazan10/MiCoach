package com.kineticos.nutrition.infrastructure.persistence;

import com.kineticos.nutrition.application.port.in.NutritionUseCase.IngredientFilter;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.RecipeFilter;
import com.kineticos.nutrition.application.port.out.NutritionRepository;
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
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador JPA del puerto {@link NutritionRepository}. Los agregados (plan de
 * alimentación, lista de compra) se orquestan acá; {@link NutritionMappers} solo
 * convierte fila a fila.
 */
@Component
public class NutritionRepositoryAdapter implements NutritionRepository {

    private final IngredientJpaRepository ingredientRepository;
    private final RecipeJpaRepository recipeRepository;
    private final RecipeIngredientJpaRepository recipeIngredientRepository;
    private final MealPlanJpaRepository mealPlanRepository;
    private final MealPlanDayJpaRepository mealPlanDayRepository;
    private final MealPlanMealJpaRepository mealPlanMealRepository;
    private final SubstitutionJpaRepository substitutionRepository;
    private final DailyIntakeJpaRepository dailyIntakeRepository;
    private final ShoppingListJpaRepository shoppingListRepository;
    private final ShoppingListItemJpaRepository shoppingListItemRepository;

    public NutritionRepositoryAdapter(IngredientJpaRepository ingredientRepository,
                                      RecipeJpaRepository recipeRepository,
                                      RecipeIngredientJpaRepository recipeIngredientRepository,
                                      MealPlanJpaRepository mealPlanRepository,
                                      MealPlanDayJpaRepository mealPlanDayRepository,
                                      MealPlanMealJpaRepository mealPlanMealRepository,
                                      SubstitutionJpaRepository substitutionRepository,
                                      DailyIntakeJpaRepository dailyIntakeRepository,
                                      ShoppingListJpaRepository shoppingListRepository,
                                      ShoppingListItemJpaRepository shoppingListItemRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.mealPlanDayRepository = mealPlanDayRepository;
        this.mealPlanMealRepository = mealPlanMealRepository;
        this.substitutionRepository = substitutionRepository;
        this.dailyIntakeRepository = dailyIntakeRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListItemRepository = shoppingListItemRepository;
    }

    // ------------------------- Catálogo -------------------------

    @Override
    public List<Ingredient> findIngredients(IngredientFilter filter) {
        return ingredientRepository.findByActiveTrue().stream()
                .filter(i -> filter.category() == null || filter.category().equalsIgnoreCase(i.getCategory()))
                .filter(i -> filter.search() == null
                        || i.getName().toLowerCase().contains(filter.search().toLowerCase()))
                .map(IngredientMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Ingredient> findIngredientById(Long ingredientId) {
        return ingredientRepository.findById(ingredientId).map(IngredientMapper::toDomain);
    }

    @Override
    public List<Recipe> findRecipes(RecipeFilter filter) {
        List<RecipeJpa> all = recipeRepository.findByActiveTrue();
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe =
                loadRecipeIngredients(all.stream().map(RecipeJpa::getId).toList());

        return all.stream()
                .filter(r -> filter.mealCategory() == null || filter.mealCategory().equalsIgnoreCase(r.getMealCategory()))
                .filter(r -> filter.difficulty() == null || filter.difficulty().equalsIgnoreCase(r.getDifficulty()))
                .filter(r -> filter.search() == null
                        || r.getName().toLowerCase().contains(filter.search().toLowerCase()))
                .map(r -> RecipeMapper.toDomain(r, ingredientsByRecipe.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Override
    public Optional<Recipe> findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .map(r -> RecipeMapper.toDomain(r,
                        loadRecipeIngredients(List.of(recipeId)).getOrDefault(recipeId, List.of())));
    }

    private Map<Long, List<RecipeIngredient>> loadRecipeIngredients(List<Long> recipeIds) {
        if (recipeIds.isEmpty()) {
            return Map.of();
        }
        List<RecipeIngredientJpa> links = recipeIngredientRepository.findByRecipeIdInOrderByOrderIndexAsc(recipeIds);
        if (links.isEmpty()) {
            return Map.of();
        }
        Set<Long> ingredientIds = links.stream().map(RecipeIngredientJpa::getIngredientId)
                .collect(Collectors.toSet());
        Map<Long, IngredientJpa> ingredientsById = ingredientRepository.findAllById(ingredientIds).stream()
                .collect(Collectors.toMap(IngredientJpa::getId, i -> i));

        return links.stream().collect(Collectors.groupingBy(RecipeIngredientJpa::getRecipeId,
                LinkedHashMap::new,
                Collectors.mapping(link -> RecipeIngredientMapper.toDomain(link,
                        ingredientsById.get(link.getIngredientId()).getName()), Collectors.toList())));
    }

    @Override
    public List<Substitution> findSubstitutions(Long ingredientId) {
        List<SubstitutionJpa> subs = substitutionRepository.findByIngredientId(ingredientId);
        if (subs.isEmpty()) {
            return List.of();
        }
        Set<Long> substituteIds = subs.stream().map(SubstitutionJpa::getSubstituteIngredientId)
                .collect(Collectors.toSet());
        Map<Long, IngredientJpa> ingredientsById = ingredientRepository.findAllById(substituteIds).stream()
                .collect(Collectors.toMap(IngredientJpa::getId, i -> i));

        return subs.stream()
                .map(s -> SubstitutionMapper.toDomain(s, ingredientsById.get(s.getSubstituteIngredientId()).getName()))
                .toList();
    }

    // ------------------------- Planes de alimentación -------------------------

    @Override
    public List<MealPlan> findMealPlansByUser(Long userId) {
        return mealPlanRepository.findByUserId(userId).stream()
                .map(jpa -> MealPlanMapper.toDomain(jpa, loadDays(jpa.getId()))).toList();
    }

    @Override
    public Optional<MealPlan> findMealPlanById(Long mealPlanId) {
        return mealPlanRepository.findById(mealPlanId)
                .map(jpa -> MealPlanMapper.toDomain(jpa, loadDays(jpa.getId())));
    }

    @Override
    public MealPlan saveMealPlan(MealPlan mealPlan) {
        MealPlanJpa savedMealPlan = mealPlanRepository.save(MealPlanMapper.toJpa(mealPlan));

        // Estrategia "replace": se descartan los días/comidas previos y se recrean.
        // flush() fuerza el DELETE antes de los INSERT siguientes (si no, Hibernate los
        // reordena y viola uq_nutrition_meal_plan_day al reinsertar la misma fecha).
        mealPlanDayRepository.deleteByMealPlanId(savedMealPlan.getId());
        mealPlanDayRepository.flush();
        for (MealPlanDay day : mealPlan.getDays()) {
            MealPlanDayJpa savedDay = mealPlanDayRepository.save(MealPlanDayMapper.toJpa(day, savedMealPlan.getId()));
            for (MealPlanMeal meal : day.getMeals()) {
                mealPlanMealRepository.save(MealPlanMealMapper.toJpa(meal, savedDay.getId()));
            }
        }
        return MealPlanMapper.toDomain(savedMealPlan, loadDays(savedMealPlan.getId()));
    }

    @Override
    public void deleteMealPlan(Long mealPlanId) {
        mealPlanDayRepository.deleteByMealPlanId(mealPlanId);
        mealPlanRepository.deleteById(mealPlanId);
    }

    private List<MealPlanDay> loadDays(Long mealPlanId) {
        List<MealPlanDayJpa> dayJpas = mealPlanDayRepository.findByMealPlanIdOrderByPlanDateAsc(mealPlanId);
        if (dayJpas.isEmpty()) {
            return List.of();
        }
        List<Long> dayIds = dayJpas.stream().map(MealPlanDayJpa::getId).toList();
        Map<Long, List<MealPlanMeal>> mealsByDay = mealPlanMealRepository
                .findByMealPlanDayIdInOrderByOrderIndexAsc(dayIds).stream()
                .collect(Collectors.groupingBy(MealPlanMealJpa::getMealPlanDayId, LinkedHashMap::new,
                        Collectors.mapping(MealPlanMealMapper::toDomain, Collectors.toList())));

        return dayJpas.stream()
                .map(d -> MealPlanDayMapper.toDomain(d, mealsByDay.getOrDefault(d.getId(), List.of())))
                .toList();
    }

    // ------------------------- Diario alimentario -------------------------

    @Override
    public List<DailyIntakeEntry> findIntakeByUser(Long userId, LocalDate foodDate) {
        List<DailyIntakeJpa> entries = foodDate != null
                ? dailyIntakeRepository.findByUserIdAndFoodDateOrderByConsumedAtDesc(userId, foodDate)
                : dailyIntakeRepository.findByUserIdOrderByFoodDateDescConsumedAtDesc(userId);
        return entries.stream().map(DailyIntakeMapper::toDomain).toList();
    }

    @Override
    public Optional<DailyIntakeEntry> findIntakeById(Long intakeId) {
        return dailyIntakeRepository.findById(intakeId).map(DailyIntakeMapper::toDomain);
    }

    @Override
    public DailyIntakeEntry saveIntake(DailyIntakeEntry entry) {
        return DailyIntakeMapper.toDomain(dailyIntakeRepository.save(DailyIntakeMapper.toJpa(entry)));
    }

    @Override
    public void deleteIntake(Long intakeId) {
        dailyIntakeRepository.deleteById(intakeId);
    }

    // ------------------------- Listas de compra -------------------------

    @Override
    public List<ShoppingList> findShoppingListsByUser(Long userId) {
        return shoppingListRepository.findByUserId(userId).stream()
                .map(jpa -> ShoppingListMapper.toDomain(jpa, loadItems(jpa.getId()))).toList();
    }

    @Override
    public Optional<ShoppingList> findShoppingListById(Long shoppingListId) {
        return shoppingListRepository.findById(shoppingListId)
                .map(jpa -> ShoppingListMapper.toDomain(jpa, loadItems(jpa.getId())));
    }

    @Override
    public ShoppingList saveShoppingList(ShoppingList shoppingList) {
        ShoppingListJpa saved = shoppingListRepository.save(ShoppingListMapper.toJpa(shoppingList));
        return ShoppingListMapper.toDomain(saved, loadItems(saved.getId()));
    }

    @Override
    public void deleteShoppingList(Long shoppingListId) {
        shoppingListRepository.deleteById(shoppingListId);
    }

    @Override
    public ShoppingListItem saveShoppingListItem(Long shoppingListId, ShoppingListItem item) {
        return ShoppingListItemMapper.toDomain(
                shoppingListItemRepository.save(ShoppingListItemMapper.toJpa(item, shoppingListId)));
    }

    @Override
    public Optional<ShoppingListItem> findShoppingListItemById(Long itemId) {
        return shoppingListItemRepository.findById(itemId).map(ShoppingListItemMapper::toDomain);
    }

    @Override
    public void deleteShoppingListItem(Long itemId) {
        shoppingListItemRepository.deleteById(itemId);
    }

    private List<ShoppingListItem> loadItems(Long shoppingListId) {
        return shoppingListItemRepository.findByShoppingListId(shoppingListId).stream()
                .map(ShoppingListItemMapper::toDomain).toList();
    }
}
