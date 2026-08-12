// =====================================================================
// KineticOs — Detalle de una rutina: días, ejercicios prescritos y
// acceso para iniciar una sesión de entrenamiento por día.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_spacing.dart';
import '../../auth/application/auth_providers.dart';
import '../application/workout_providers.dart';
import '../domain/workout.dart';
import 'widgets/exercise_name_text.dart';
import 'workout_labels.dart';

class WorkoutDetailScreen extends ConsumerWidget {
  final int workoutId;

  const WorkoutDetailScreen({super.key, required this.workoutId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final workoutAsync = ref.watch(workoutDetailProvider(workoutId));
    final currentUserId = ref.watch(authControllerProvider).user?.id;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Rutina'),
        actions: [
          workoutAsync.maybeWhen(
            data: (workout) => workout.userId == currentUserId
                ? Row(children: [
                    IconButton(
                      icon: const Icon(Icons.edit_outlined),
                      onPressed: () => context.push('/workouts/$workoutId/edit'),
                    ),
                    IconButton(
                      icon: const Icon(Icons.delete_outline),
                      onPressed: () => _confirmDelete(context, ref, workout),
                    ),
                  ])
                : const SizedBox.shrink(),
            orElse: () => const SizedBox.shrink(),
          ),
        ],
      ),
      body: workoutAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (workout) => _WorkoutDetailBody(workout: workout),
      ),
    );
  }

  Future<void> _confirmDelete(BuildContext context, WidgetRef ref, Workout workout) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Borrar rutina'),
        content: Text('¿Seguro que querés borrar "${workout.name}"? Esta acción no se puede deshacer.'),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Borrar')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(workoutActionsProvider).deleteWorkout(workout.id);
      if (context.mounted) context.go('/workouts');
    }
  }
}

class _WorkoutDetailBody extends ConsumerWidget {
  final Workout workout;

  const _WorkoutDetailBody({required this.workout});

  Future<void> _startSession(BuildContext context, WidgetRef ref, int? workoutDayId) async {
    final session =
        await ref.read(workoutActionsProvider).startSession(workoutId: workout.id, workoutDayId: workoutDayId);
    if (context.mounted) context.push('/sessions/${session.id}');
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return ListView(
      padding: const EdgeInsets.all(AppSpacing.md),
      children: [
        Text(workout.name, style: Theme.of(context).textTheme.headlineSmall),
        if (workout.description != null) ...[
          const SizedBox(height: AppSpacing.xs),
          Text(workout.description!),
        ],
        const SizedBox(height: AppSpacing.sm),
        Wrap(spacing: AppSpacing.sm, children: [
          if (workout.objective != null) Chip(label: Text(labelFor(objectiveLabels, workout.objective))),
          if (workout.level != null) Chip(label: Text(labelFor(levelLabels, workout.level))),
          if (workout.durationWeeks != null) Chip(label: Text('${workout.durationWeeks} semanas')),
          if (workout.template) const Chip(label: Text('Plantilla')),
        ]),
        const SizedBox(height: AppSpacing.lg),
        Text('Días', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: AppSpacing.sm),
        for (final day in workout.days)
          Card(
            margin: const EdgeInsets.only(bottom: AppSpacing.sm),
            child: Padding(
              padding: const EdgeInsets.all(AppSpacing.sm),
              child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                Row(children: [
                  Expanded(
                    child: Text(day.name?.isNotEmpty == true ? day.name! : 'Día ${day.dayIndex}',
                        style: Theme.of(context).textTheme.titleSmall),
                  ),
                  if (day.restDay)
                    const Chip(label: Text('Descanso'))
                  else
                    OutlinedButton.icon(
                      icon: const Icon(Icons.play_arrow),
                      label: const Text('Iniciar'),
                      onPressed: () => _startSession(context, ref, day.id),
                    ),
                ]),
                for (final exercise in day.exercises)
                  Padding(
                    padding: const EdgeInsets.only(top: AppSpacing.xs),
                    child: Row(children: [
                      Expanded(child: ExerciseNameText(exerciseId: exercise.exerciseId)),
                      Text('${exercise.sets ?? '-'} x ${exercise.repsMin ?? '?'}-${exercise.repsMax ?? '?'}'),
                    ]),
                  ),
              ]),
            ),
          ),
        const SizedBox(height: AppSpacing.md),
        OutlinedButton.icon(
          icon: const Icon(Icons.play_circle_outline),
          label: const Text('Iniciar sesión libre'),
          onPressed: () => _startSession(context, ref, null),
        ),
      ],
    );
  }
}
