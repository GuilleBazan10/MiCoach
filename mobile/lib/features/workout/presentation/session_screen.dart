// =====================================================================
// MiCoach — Sesión de entrenamiento: registrar ejercicios ejecutados,
// completar o abandonar la sesión.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/app_spacing.dart';
import '../application/workout_providers.dart';
import '../domain/exercise.dart';
import '../domain/workout_session.dart';
import 'widgets/exercise_name_text.dart';
import 'widgets/exercise_picker_dialog.dart';

const _statusLabels = {'in_progress': 'En curso', 'completed': 'Completada', 'aborted': 'Abandonada'};

class SessionScreen extends ConsumerWidget {
  final int sessionId;

  const SessionScreen({super.key, required this.sessionId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessionAsync = ref.watch(sessionDetailProvider(sessionId));

    return Scaffold(
      appBar: AppBar(title: const Text('Sesión de entrenamiento')),
      body: sessionAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (session) => _SessionBody(session: session),
      ),
    );
  }
}

class _SessionBody extends ConsumerWidget {
  final WorkoutSession session;

  const _SessionBody({required this.session});

  bool get _isActive => session.status == 'in_progress';

  Future<void> _logExercise(BuildContext context, WidgetRef ref) async {
    final exercise = await showExercisePickerDialog(context);
    if (exercise == null || !context.mounted) return;
    final result = await _showLogForm(context, exercise);
    if (result == null) return;
    await ref.read(workoutActionsProvider).logSessionExercise(
          session.id,
          exerciseId: exercise.id,
          setsDone: result.setsDone,
          weightKg: result.weightKg,
          reps: result.reps,
          rpe: result.rpe,
        );
  }

  Future<_LogFormResult?> _showLogForm(BuildContext context, Exercise exercise) {
    final setsController = TextEditingController();
    final weightController = TextEditingController();
    final repsController = TextEditingController();
    final rpeController = TextEditingController();

    return showDialog<_LogFormResult>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(exercise.name),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(
              controller: setsController,
              decoration: const InputDecoration(labelText: 'Series realizadas'),
              keyboardType: TextInputType.number),
          TextField(
              controller: weightController,
              decoration: const InputDecoration(labelText: 'Peso (kg)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
          TextField(
              controller: repsController,
              decoration: const InputDecoration(labelText: 'Repeticiones'),
              keyboardType: TextInputType.number),
          TextField(
              controller: rpeController,
              decoration: const InputDecoration(labelText: 'RPE (1-10)'),
              keyboardType: TextInputType.number),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(), child: const Text('Cancelar')),
          FilledButton(
            onPressed: () => Navigator.of(dialogContext).pop(_LogFormResult(
              setsDone: int.tryParse(setsController.text),
              weightKg: double.tryParse(weightController.text.replaceAll(',', '.')),
              reps: int.tryParse(repsController.text),
              rpe: int.tryParse(rpeController.text),
            )),
            child: const Text('Registrar'),
          ),
        ],
      ),
    );
  }

  Future<void> _completeSession(BuildContext context, WidgetRef ref) async {
    final minutesController = TextEditingController();
    final notesController = TextEditingController();
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Completar sesión'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(
              controller: minutesController,
              decoration: const InputDecoration(labelText: 'Duración (minutos)'),
              keyboardType: TextInputType.number),
          TextField(controller: notesController, decoration: const InputDecoration(labelText: 'Notas')),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Completar')),
        ],
      ),
    );
    if (confirmed == true) {
      final minutes = int.tryParse(minutesController.text);
      await ref.read(workoutActionsProvider).completeSession(
            session.id,
            durationSeconds: minutes != null ? minutes * 60 : null,
            notes: notesController.text.trim().isEmpty ? null : notesController.text.trim(),
          );
    }
  }

  Future<void> _abortSession(BuildContext context, WidgetRef ref) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Abandonar sesión'),
        content: const Text('¿Seguro que querés abandonar esta sesión?'),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('No')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Sí, abandonar')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(workoutActionsProvider).abortSession(session.id);
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return ListView(
      padding: const EdgeInsets.all(AppSpacing.md),
      children: [
        Chip(label: Text(_statusLabels[session.status] ?? session.status)),
        if (session.durationSeconds != null) ...[
          const SizedBox(height: AppSpacing.xs),
          Text('Duración: ${(session.durationSeconds! / 60).round()} min'),
        ],
        if (session.notes != null) ...[
          const SizedBox(height: AppSpacing.xs),
          Text(session.notes!),
        ],
        const SizedBox(height: AppSpacing.lg),
        Text('Ejercicios registrados', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: AppSpacing.sm),
        if (session.exercises.isEmpty) const Text('Todavía no registraste ningún ejercicio.'),
        for (final exercise in session.exercises)
          Card(
            margin: const EdgeInsets.only(bottom: AppSpacing.xs),
            child: ListTile(
              title: ExerciseNameText(exerciseId: exercise.exerciseId),
              subtitle: Text([
                if (exercise.setsDone != null) '${exercise.setsDone} series',
                if (exercise.reps != null) '${exercise.reps} reps',
                if (exercise.durationSeconds != null) '${exercise.durationSeconds} seg',
                if (exercise.distanceMeters != null) '${exercise.distanceMeters} m',
                if (exercise.weightKg != null) '${exercise.weightKg} kg',
                if (exercise.rpe != null) 'RPE ${exercise.rpe}',
              ].join(' · ')),
            ),
          ),
        if (_isActive) ...[
          const SizedBox(height: AppSpacing.md),
          FilledButton.icon(
            icon: const Icon(Icons.add),
            label: const Text('Registrar ejercicio'),
            onPressed: () => _logExercise(context, ref),
          ),
          const SizedBox(height: AppSpacing.sm),
          FilledButton.tonalIcon(
            icon: const Icon(Icons.check),
            label: const Text('Completar sesión'),
            onPressed: () => _completeSession(context, ref),
          ),
          const SizedBox(height: AppSpacing.sm),
          OutlinedButton.icon(
            icon: const Icon(Icons.close),
            label: const Text('Abandonar sesión'),
            onPressed: () => _abortSession(context, ref),
          ),
        ],
      ],
    );
  }
}

class _LogFormResult {
  final int? setsDone;
  final double? weightKg;
  final int? reps;
  final int? rpe;

  const _LogFormResult({this.setsDone, this.weightKg, this.reps, this.rpe});
}
