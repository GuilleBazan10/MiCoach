// =====================================================================
// KineticOs — Detalle de un plan de alimentación: días y comidas.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_spacing.dart';
import '../application/nutrition_providers.dart';
import '../domain/meal_plan.dart';
import 'nutrition_labels.dart';
import 'widgets/recipe_name_text.dart';

class MealPlanDetailScreen extends ConsumerWidget {
  final int mealPlanId;

  const MealPlanDetailScreen({super.key, required this.mealPlanId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final planAsync = ref.watch(mealPlanDetailProvider(mealPlanId));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Plan de alimentación'),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_outlined),
            onPressed: () => context.push('/nutrition/plans/$mealPlanId/edit'),
          ),
          planAsync.maybeWhen(
            data: (plan) => IconButton(
              icon: const Icon(Icons.delete_outline),
              onPressed: () => _confirmDelete(context, ref, plan),
            ),
            orElse: () => const SizedBox.shrink(),
          ),
        ],
      ),
      body: planAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (plan) => _MealPlanDetailBody(plan: plan),
      ),
    );
  }

  Future<void> _confirmDelete(BuildContext context, WidgetRef ref, MealPlan plan) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Borrar plan'),
        content: Text('¿Seguro que querés borrar "${plan.name}"? Esta acción no se puede deshacer.'),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Borrar')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(nutritionActionsProvider).deleteMealPlan(plan.id);
      if (context.mounted) context.go('/nutrition');
    }
  }
}

class _MealPlanDetailBody extends StatelessWidget {
  final MealPlan plan;

  const _MealPlanDetailBody({required this.plan});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AppSpacing.md),
      children: [
        Text(plan.name, style: Theme.of(context).textTheme.headlineSmall),
        if (plan.description != null) ...[
          const SizedBox(height: AppSpacing.xs),
          Text(plan.description!),
        ],
        const SizedBox(height: AppSpacing.sm),
        Wrap(spacing: AppSpacing.sm, children: [
          Chip(label: Text('${plan.startDate.day}/${plan.startDate.month} — ${plan.endDate.day}/${plan.endDate.month}')),
          if (plan.targetCalories != null) Chip(label: Text('${plan.targetCalories} kcal/día')),
        ]),
        const SizedBox(height: AppSpacing.lg),
        Text('Días', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: AppSpacing.sm),
        for (final MapEntry(key: dayIndex, value: day) in plan.days.asMap().entries)
          Card(
            margin: const EdgeInsets.only(bottom: AppSpacing.sm),
            child: Padding(
              padding: const EdgeInsets.all(AppSpacing.sm),
              child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                Text.rich(TextSpan(children: [
                  TextSpan(text: 'Día ${dayIndex + 1} ', style: Theme.of(context).textTheme.titleSmall),
                  TextSpan(
                    text: '· ${day.planDate.day}/${day.planDate.month}/${day.planDate.year}',
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        color: Theme.of(context).colorScheme.onSurfaceVariant, fontWeight: FontWeight.normal),
                  ),
                ])),
                for (final meal in day.meals)
                  Padding(
                    padding: const EdgeInsets.only(top: AppSpacing.xs),
                    child: Row(children: [
                      SizedBox(
                          width: 90,
                          child: Text(labelFor(mealTypeLabels, meal.mealType),
                              style: Theme.of(context).textTheme.bodySmall)),
                      Expanded(child: RecipeNameText(recipeId: meal.recipeId)),
                      Text('x${meal.servings}'),
                    ]),
                  ),
                if (day.meals.isEmpty)
                  const Padding(
                    padding: EdgeInsets.only(top: AppSpacing.xs),
                    child: Text('Sin comidas cargadas'),
                  ),
              ]),
            ),
          ),
      ],
    );
  }
}
