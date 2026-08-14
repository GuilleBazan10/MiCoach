// =====================================================================
// KineticOs — Fila de un ejercicio planeado dentro de un día de rutina:
// nombre (tappable, ver detalle) + series/reps ya formateados según el
// tipo de medición del ejercicio (reps vs. segundos).
// Equivalente a web/src/features/workout/components/PlannedExerciseRow.tsx.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/workout_providers.dart';
import '../../domain/workout.dart';
import '../workout_labels.dart';
import 'exercise_name_text.dart';

class PlannedExerciseRow extends ConsumerWidget {
  final PlannedExercise exercise;

  const PlannedExerciseRow({super.key, required this.exercise});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final exerciseAsync = ref.watch(exerciseDetailProvider(exercise.exerciseId));
    final measurementType = exerciseAsync.value?.measurementType;

    return Padding(
      padding: const EdgeInsets.only(top: AppSpacing.xs),
      child: Row(children: [
        Expanded(child: ExerciseNameText(exerciseId: exercise.exerciseId)),
        Text(formatSetsReps(
          sets: exercise.sets,
          repsMin: exercise.repsMin,
          repsMax: exercise.repsMax,
          measurementType: measurementType,
        )),
      ]),
    );
  }
}
