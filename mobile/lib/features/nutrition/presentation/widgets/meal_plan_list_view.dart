// =====================================================================
// MiCoach — Lista de planes de alimentación del usuario.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/nutrition_providers.dart';

class MealPlanListView extends ConsumerWidget {
  const MealPlanListView({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final plansAsync = ref.watch(mealPlanListProvider);

    return plansAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Error: $e')),
      data: (plans) {
        if (plans.isEmpty) {
          return const Center(
            child: Padding(
              padding: EdgeInsets.all(AppSpacing.lg),
              child: Text('Todavía no creaste ningún plan de alimentación.', textAlign: TextAlign.center),
            ),
          );
        }
        return RefreshIndicator(
          onRefresh: () async => ref.invalidate(mealPlanListProvider),
          child: ListView.builder(
            padding: const EdgeInsets.all(AppSpacing.md),
            itemCount: plans.length,
            itemBuilder: (context, index) {
              final plan = plans[index];
              return Card(
                margin: const EdgeInsets.only(bottom: AppSpacing.sm),
                child: ListTile(
                  title: Text(plan.name),
                  subtitle: Text(
                      '${plan.startDate.day}/${plan.startDate.month} — ${plan.endDate.day}/${plan.endDate.month} · ${plan.days.length} días'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push('/nutrition/plans/${plan.id}'),
                ),
              );
            },
          ),
        );
      },
    );
  }
}
