// =====================================================================
// MiCoach — Diálogo para generar una rutina con IA a partir de un pedido en
// lenguaje natural. Equivalente a web/src/features/workout/components/GenerateWorkoutDialog.tsx.
// ¡Puede tardar bastante! (llega hasta minutos con Ollama corriendo por CPU).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/network/api_error.dart';
import '../../../../core/theme/app_spacing.dart';
import '../../application/workout_providers.dart';

Future<void> showGenerateWorkoutDialog(BuildContext context, WidgetRef ref) {
  return showDialog<void>(
    context: context,
    barrierDismissible: false,
    builder: (_) => const _GenerateWorkoutDialog(),
  );
}

class _GenerateWorkoutDialog extends ConsumerStatefulWidget {
  const _GenerateWorkoutDialog();

  @override
  ConsumerState<_GenerateWorkoutDialog> createState() => _GenerateWorkoutDialogState();
}

class _GenerateWorkoutDialogState extends ConsumerState<_GenerateWorkoutDialog> {
  final _goalController = TextEditingController();
  bool _generating = false;

  @override
  void dispose() {
    _goalController.dispose();
    super.dispose();
  }

  Future<void> _generate() async {
    final goal = _goalController.text.trim();
    if (goal.isEmpty) return;
    setState(() => _generating = true);
    try {
      final actions = ref.read(workoutActionsProvider);
      final workout = await actions.generateWorkout(goal);
      if (mounted) {
        Navigator.of(context).pop();
        context.go('/workouts/${workout.id}');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extractErrorMessage(e))));
        setState(() => _generating = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: !_generating,
      child: AlertDialog(
        title: const Text('Generar rutina con IA'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          Text(
            'Describí qué rutina necesitás (objetivo, días por semana, equipamiento). '
            'Se va a tener en cuenta tu perfil (nivel, lesiones, patologías).',
            style: Theme.of(context).textTheme.bodySmall,
          ),
          const SizedBox(height: AppSpacing.md),
          TextField(
            controller: _goalController,
            enabled: !_generating,
            maxLines: 3,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'Qué querés lograr',
              hintText: 'Ej: rutina de fuerza, 4 días, tengo mancuernas y banco',
            ),
          ),
          if (_generating) ...[
            const SizedBox(height: AppSpacing.md),
            const Row(children: [
              SizedBox(width: 18, height: 18, child: CircularProgressIndicator(strokeWidth: 2)),
              SizedBox(width: AppSpacing.sm),
              Expanded(child: Text('Generando... puede tardar un par de minutos.')),
            ]),
          ],
        ]),
        actions: [
          TextButton(
            onPressed: _generating ? null : () => Navigator.of(context).pop(),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            onPressed: _generating ? null : _generate,
            child: const Text('Generar'),
          ),
        ],
      ),
    );
  }
}
