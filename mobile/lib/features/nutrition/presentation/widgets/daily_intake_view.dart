// =====================================================================
// KineticOs — Pestaña "Diario": comidas registradas hoy + totales.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/nutrition_providers.dart';
import '../nutrition_labels.dart';
import 'log_intake_dialog.dart';
import 'recipe_name_text.dart';

DateTime _today() {
  final now = DateTime.now();
  return DateTime(now.year, now.month, now.day);
}

class DailyIntakeView extends ConsumerWidget {
  const DailyIntakeView({super.key});

  Future<void> _logIntake(BuildContext context, WidgetRef ref) async {
    final result = await showLogIntakeDialog(context);
    if (result == null) return;
    await ref.read(nutritionActionsProvider).logIntake(
          recipeId: result.recipeId,
          foodDate: _today(),
          mealType: result.mealType,
          amount: result.amount,
          calories: result.calories,
          proteinG: result.proteinG,
          carbsG: result.carbsG,
          fatG: result.fatG,
        );
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final intakeAsync = ref.watch(dailyIntakeProvider(_today()));

    return Scaffold(
      body: intakeAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (entries) {
          final totalCalories = entries.fold<double>(0, (sum, e) => sum + (e.calories ?? 0));
          final totalProtein = entries.fold<double>(0, (sum, e) => sum + (e.proteinG ?? 0));
          final totalCarbs = entries.fold<double>(0, (sum, e) => sum + (e.carbsG ?? 0));
          final totalFat = entries.fold<double>(0, (sum, e) => sum + (e.fatG ?? 0));

          return RefreshIndicator(
            onRefresh: () async => ref.invalidate(dailyIntakeProvider),
            child: ListView(
              padding: const EdgeInsets.all(AppSpacing.md),
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(AppSpacing.md),
                    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Text('Hoy', style: Theme.of(context).textTheme.titleMedium),
                      const SizedBox(height: AppSpacing.xs),
                      Text('${totalCalories.round()} kcal · '
                          'P ${totalProtein.round()}g · C ${totalCarbs.round()}g · G ${totalFat.round()}g'),
                    ]),
                  ),
                ),
                const SizedBox(height: AppSpacing.md),
                if (entries.isEmpty)
                  const Padding(
                    padding: EdgeInsets.all(AppSpacing.lg),
                    child: Text('Todavía no registraste ninguna comida hoy.', textAlign: TextAlign.center),
                  ),
                for (final entry in entries)
                  Card(
                    margin: const EdgeInsets.only(bottom: AppSpacing.xs),
                    child: ListTile(
                      title: RecipeNameText(recipeId: entry.recipeId),
                      subtitle: Text(
                          '${labelFor(mealTypeLabels, entry.mealType)} · ${entry.calories?.round() ?? '?'} kcal'),
                      trailing: IconButton(
                        icon: const Icon(Icons.delete_outline),
                        onPressed: () => ref.read(nutritionActionsProvider).deleteIntake(entry.id),
                      ),
                    ),
                  ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _logIntake(context, ref),
        icon: const Icon(Icons.add),
        label: const Text('Registrar comida'),
      ),
    );
  }
}
