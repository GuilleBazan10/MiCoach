// =====================================================================
// MiCoach — Diálogo para buscar y elegir un ejercicio del catálogo.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/workout_providers.dart';
import '../../domain/exercise.dart';
import '../../domain/exercise_filter.dart';
import '../workout_labels.dart';

/// Muestra el diálogo y devuelve el ejercicio elegido, o null si se cancela.
Future<Exercise?> showExercisePickerDialog(BuildContext context) {
  return showDialog<Exercise>(
    context: context,
    builder: (_) => const _ExercisePickerDialog(),
  );
}

class _ExercisePickerDialog extends ConsumerStatefulWidget {
  const _ExercisePickerDialog();

  @override
  ConsumerState<_ExercisePickerDialog> createState() => _ExercisePickerDialogState();
}

class _ExercisePickerDialogState extends ConsumerState<_ExercisePickerDialog> {
  String _search = '';

  @override
  Widget build(BuildContext context) {
    final filter = ExerciseFilter(search: _search.isEmpty ? null : _search);
    final exercisesAsync = ref.watch(exerciseCatalogProvider(filter));

    return Dialog(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 480, maxHeight: 560),
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.md),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text('Elegir ejercicio', style: Theme.of(context).textTheme.titleLarge),
              const SizedBox(height: AppSpacing.sm),
              TextField(
                decoration: const InputDecoration(labelText: 'Buscar', prefixIcon: Icon(Icons.search)),
                onChanged: (v) => setState(() => _search = v),
              ),
              const SizedBox(height: AppSpacing.sm),
              Expanded(
                child: exercisesAsync.when(
                  loading: () => const Center(child: CircularProgressIndicator()),
                  error: (e, _) => Center(child: Text('Error: $e')),
                  data: (exercises) => exercises.isEmpty
                      ? const Center(child: Text('Sin resultados'))
                      : ListView.builder(
                          itemCount: exercises.length,
                          itemBuilder: (context, index) {
                            final exercise = exercises[index];
                            return ListTile(
                              title: Text(exercise.name),
                              subtitle:
                                  Text('${labelFor(categoryLabels, exercise.category)} · ${labelFor(difficultyLabels, exercise.difficulty)}'),
                              onTap: () => Navigator.of(context).pop(exercise),
                            );
                          },
                        ),
                ),
              ),
              const SizedBox(height: AppSpacing.sm),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cancelar')),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
