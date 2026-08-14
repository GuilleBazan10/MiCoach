package com.kineticos.nutrition.presentation;

import com.kineticos.shared.security.AuthenticatedUser;
import com.kineticos.nutrition.application.port.in.NutritionUseCase;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.IngredientFilter;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.IntakeData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.MealPlanData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.MealPlanDayData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.MealPlanMealData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.RecipeFilter;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.ShoppingListData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.ShoppingListItemData;
import com.kineticos.nutrition.application.port.in.NutritionUseCase.SubstitutionRequestData;
import com.kineticos.nutrition.presentation.NutritionDtos.IngredientResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.IntakeRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.GenerateMealPlanRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.IntakeResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.MealPlanDayRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.MealPlanMealRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.MealPlanRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.MealPlanResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.RecipeResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.ShoppingListItemCheckRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.ShoppingListItemRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.ShoppingListItemResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.ShoppingListRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.ShoppingListResponse;
import com.kineticos.nutrition.presentation.NutritionDtos.SubstitutionGenerateRequest;
import com.kineticos.nutrition.presentation.NutritionDtos.SubstitutionResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Contratos REST del módulo nutrition (base path /api/v1/nutrition). Todos requieren
 * JWT (configurado en app/security).
 */
@RestController
@RequestMapping("/api/v1/nutrition")
public class NutritionController {

    private final NutritionUseCase useCase;

    public NutritionController(NutritionUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Catálogo -------------------------

    @GetMapping("/ingredients")
    public List<IngredientResponse> listIngredients(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) String search) {
        return useCase.listIngredients(new IngredientFilter(category, search)).stream()
                .map(IngredientResponse::from).toList();
    }

    @GetMapping("/ingredients/{ingredientId}")
    public IngredientResponse getIngredient(@PathVariable Long ingredientId) {
        return IngredientResponse.from(useCase.getIngredient(ingredientId));
    }

    @GetMapping("/ingredients/{ingredientId}/substitutions")
    public List<SubstitutionResponse> listSubstitutions(@PathVariable Long ingredientId) {
        return useCase.listSubstitutions(ingredientId).stream().map(SubstitutionResponse::from).toList();
    }

    @PostMapping("/ingredients/{ingredientId}/substitutions/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public SubstitutionResponse generateSubstitution(@AuthenticationPrincipal AuthenticatedUser user,
                                                      @PathVariable Long ingredientId,
                                                      @Valid @RequestBody SubstitutionGenerateRequest request) {
        SubstitutionRequestData data = new SubstitutionRequestData(request.reason(), request.notes());
        return SubstitutionResponse.from(useCase.generateSubstitution(user.id(), ingredientId, data));
    }

    @GetMapping("/recipes")
    public List<RecipeResponse> listRecipes(@RequestParam(required = false) String mealCategory,
                                            @RequestParam(required = false) String difficulty,
                                            @RequestParam(required = false) String search) {
        return useCase.listRecipes(new RecipeFilter(mealCategory, difficulty, search)).stream()
                .map(RecipeResponse::from).toList();
    }

    @GetMapping("/recipes/{recipeId}")
    public RecipeResponse getRecipe(@PathVariable Long recipeId) {
        return RecipeResponse.from(useCase.getRecipe(recipeId));
    }

    // ------------------------- Planes de alimentación -------------------------

    @GetMapping("/meal-plans")
    public List<MealPlanResponse> listMealPlans(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listMealPlans(user.id()).stream().map(MealPlanResponse::from).toList();
    }

    @GetMapping("/meal-plans/{mealPlanId}")
    public MealPlanResponse getMealPlan(@AuthenticationPrincipal AuthenticatedUser user,
                                        @PathVariable Long mealPlanId) {
        return MealPlanResponse.from(useCase.getMealPlan(user.id(), mealPlanId));
    }

    @PostMapping("/meal-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse createMealPlan(@AuthenticationPrincipal AuthenticatedUser user,
                                           @Valid @RequestBody MealPlanRequest request) {
        return MealPlanResponse.from(useCase.createMealPlan(user.id(), toMealPlanData(request)));
    }

    @PostMapping("/meal-plans/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse generateMealPlan(@AuthenticationPrincipal AuthenticatedUser user,
                                             @Valid @RequestBody GenerateMealPlanRequest request) {
        return MealPlanResponse.from(useCase.generateMealPlan(user.id(), request.goal()));
    }

    @PutMapping("/meal-plans/{mealPlanId}")
    public MealPlanResponse updateMealPlan(@AuthenticationPrincipal AuthenticatedUser user,
                                           @PathVariable Long mealPlanId,
                                           @Valid @RequestBody MealPlanRequest request) {
        return MealPlanResponse.from(useCase.updateMealPlan(user.id(), mealPlanId, toMealPlanData(request)));
    }

    @PostMapping("/meal-plans/{mealPlanId}/adjust-calories")
    public MealPlanResponse adjustMealPlanCalories(@AuthenticationPrincipal AuthenticatedUser user,
                                                    @PathVariable Long mealPlanId) {
        return MealPlanResponse.from(useCase.adjustMealPlanCalories(user.id(), mealPlanId));
    }

    @DeleteMapping("/meal-plans/{mealPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealPlan(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long mealPlanId) {
        useCase.deleteMealPlan(user.id(), mealPlanId);
    }

    private MealPlanData toMealPlanData(MealPlanRequest request) {
        List<MealPlanDayData> days = request.days().stream().map(this::toDayData).toList();
        return new MealPlanData(request.name(), request.description(), request.startDate(), request.endDate(),
                request.targetCalories(), request.targetProteinG(), request.targetCarbsG(),
                request.targetFatG(), days);
    }

    private MealPlanDayData toDayData(MealPlanDayRequest request) {
        List<MealPlanMealData> meals = request.meals() == null ? List.of()
                : request.meals().stream().map(this::toMealData).toList();
        return new MealPlanDayData(request.planDate(), meals);
    }

    private MealPlanMealData toMealData(MealPlanMealRequest request) {
        return new MealPlanMealData(request.recipeId(), request.mealType(), request.orderIndex(),
                request.servings(), request.notes());
    }

    // ------------------------- Diario alimentario -------------------------

    @GetMapping("/intake")
    public List<IntakeResponse> listIntake(@AuthenticationPrincipal AuthenticatedUser user,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return useCase.listIntake(user.id(), date).stream().map(IntakeResponse::from).toList();
    }

    @PostMapping("/intake")
    @ResponseStatus(HttpStatus.CREATED)
    public IntakeResponse logIntake(@AuthenticationPrincipal AuthenticatedUser user,
                                    @Valid @RequestBody IntakeRequest request) {
        IntakeData data = new IntakeData(request.mealPlanMealId(), request.recipeId(), request.foodDate(),
                request.mealType(), request.amount(), request.calories(), request.proteinG(),
                request.carbsG(), request.fatG());
        return IntakeResponse.from(useCase.logIntake(user.id(), data));
    }

    @DeleteMapping("/intake/{intakeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntake(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long intakeId) {
        useCase.deleteIntake(user.id(), intakeId);
    }

    // ------------------------- Listas de compra -------------------------

    @GetMapping("/shopping-lists")
    public List<ShoppingListResponse> listShoppingLists(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listShoppingLists(user.id()).stream().map(ShoppingListResponse::from).toList();
    }

    @GetMapping("/shopping-lists/{shoppingListId}")
    public ShoppingListResponse getShoppingList(@AuthenticationPrincipal AuthenticatedUser user,
                                                @PathVariable Long shoppingListId) {
        return ShoppingListResponse.from(useCase.getShoppingList(user.id(), shoppingListId));
    }

    @PostMapping("/shopping-lists")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResponse createShoppingList(@AuthenticationPrincipal AuthenticatedUser user,
                                                   @Valid @RequestBody ShoppingListRequest request) {
        ShoppingListData data = new ShoppingListData(request.name(), request.weekStart());
        return ShoppingListResponse.from(useCase.createShoppingList(user.id(), data));
    }

    @DeleteMapping("/shopping-lists/{shoppingListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingList(@AuthenticationPrincipal AuthenticatedUser user,
                                   @PathVariable Long shoppingListId) {
        useCase.deleteShoppingList(user.id(), shoppingListId);
    }

    @PostMapping("/shopping-lists/{shoppingListId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItemResponse addShoppingListItem(@AuthenticationPrincipal AuthenticatedUser user,
                                                         @PathVariable Long shoppingListId,
                                                         @Valid @RequestBody ShoppingListItemRequest request) {
        ShoppingListItemData data = new ShoppingListItemData(request.ingredientId(), request.itemName(),
                request.amount(), request.unit(), request.category());
        return ShoppingListItemResponse.from(useCase.addShoppingListItem(user.id(), shoppingListId, data));
    }

    @PutMapping("/shopping-lists/{shoppingListId}/items/{itemId}")
    public ShoppingListItemResponse setItemChecked(@AuthenticationPrincipal AuthenticatedUser user,
                                                   @PathVariable Long shoppingListId, @PathVariable Long itemId,
                                                   @Valid @RequestBody ShoppingListItemCheckRequest request) {
        return ShoppingListItemResponse.from(
                useCase.setShoppingListItemChecked(user.id(), shoppingListId, itemId, request.checked()));
    }

    @DeleteMapping("/shopping-lists/{shoppingListId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingListItem(@AuthenticationPrincipal AuthenticatedUser user,
                                       @PathVariable Long shoppingListId, @PathVariable Long itemId) {
        useCase.deleteShoppingListItem(user.id(), shoppingListId, itemId);
    }
}
