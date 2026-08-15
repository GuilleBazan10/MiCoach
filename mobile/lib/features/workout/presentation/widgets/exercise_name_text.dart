// =====================================================================
// MiCoach — Resuelve y muestra el nombre de un ejercicio por id
// (los planned exercises del backend solo traen exerciseId).
// Tappable: abre el detalle (imagen/video/instrucciones) del ejercicio.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../application/workout_providers.dart';
import 'exercise_detail_dialog.dart';

class ExerciseNameText extends ConsumerWidget {
  final int exerciseId;
  final TextStyle? style;

  const ExerciseNameText({super.key, required this.exerciseId, this.style});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final exerciseAsync = ref.watch(exerciseDetailProvider(exerciseId));
    return exerciseAsync.when(
      loading: () => Text('Cargando…', style: style),
      error: (e, _) => Text('Ejercicio #$exerciseId', style: style),
      data: (exercise) => InkWell(
        onTap: () => showExerciseDetailDialog(context, exercise),
        child: Text(exercise.name, style: (style ?? const TextStyle()).copyWith(decoration: TextDecoration.underline)),
      ),
    );
  }
}
