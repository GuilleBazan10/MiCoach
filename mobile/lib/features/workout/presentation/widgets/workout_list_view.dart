// =====================================================================
// KineticOs — Lista de rutinas (propias o plantillas).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/workout_providers.dart';
import '../workout_labels.dart';

class WorkoutListView extends ConsumerWidget {
  final bool templates;

  const WorkoutListView({super.key, required this.templates});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final workoutsAsync = ref.watch(workoutListProvider(templates));

    return workoutsAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Error: $e')),
      data: (workouts) {
        if (workouts.isEmpty) {
          return Center(
            child: Padding(
              padding: const EdgeInsets.all(AppSpacing.lg),
              child: Text(
                templates ? 'Todavía no hay plantillas disponibles.' : 'Todavía no creaste ninguna rutina.',
                textAlign: TextAlign.center,
              ),
            ),
          );
        }
        return RefreshIndicator(
          onRefresh: () async => ref.invalidate(workoutListProvider(templates)),
          child: ListView.builder(
            padding: const EdgeInsets.all(AppSpacing.md),
            itemCount: workouts.length,
            itemBuilder: (context, index) {
              final workout = workouts[index];
              return Card(
                margin: const EdgeInsets.only(bottom: AppSpacing.sm),
                child: ListTile(
                  title: Text(workout.name),
                  subtitle: Text([
                    if (workout.objective != null) labelFor(objectiveLabels, workout.objective),
                    if (workout.level != null) labelFor(levelLabels, workout.level),
                    '${workout.days.length} días',
                  ].join(' · ')),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push('/workouts/${workout.id}'),
                ),
              );
            },
          ),
        );
      },
    );
  }
}
