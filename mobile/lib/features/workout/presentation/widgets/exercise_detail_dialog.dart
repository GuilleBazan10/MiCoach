// =====================================================================
// KineticOs — Detalle de un ejercicio: cómo se hace (imagen/video/instrucciones).
// Se abre al tocar el nombre de un ejercicio en una rutina o sesión.
// Equivalente a web/src/features/workout/components/ExerciseDetailDialog.tsx.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../domain/exercise.dart';
import '../workout_labels.dart';

Future<void> showExerciseDetailDialog(BuildContext context, Exercise exercise) {
  return showDialog<void>(context: context, builder: (_) => _ExerciseDetailDialog(exercise: exercise));
}

class _ExerciseDetailDialog extends StatelessWidget {
  final Exercise exercise;

  const _ExerciseDetailDialog({required this.exercise});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(exercise.name),
      content: SingleChildScrollView(
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisSize: MainAxisSize.min, children: [
          Wrap(spacing: AppSpacing.xs, runSpacing: AppSpacing.xs, children: [
            Chip(label: Text(labelFor(categoryLabels, exercise.category))),
            Chip(label: Text(labelFor(difficultyLabels, exercise.difficulty))),
            for (final eq in exercise.equipment) Chip(label: Text(eq)),
          ]),
          const SizedBox(height: AppSpacing.sm),
          if (exercise.imageUrl != null)
            ClipRRect(
              borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
              child: Image.network(exercise.imageUrl!, fit: BoxFit.cover),
            )
          else
            Container(
              height: 120,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(AppSpacing.radiusSm),
              ),
              alignment: Alignment.center,
              child: Column(mainAxisSize: MainAxisSize.min, children: [
                Icon(Icons.image_not_supported_outlined, color: Theme.of(context).colorScheme.onSurfaceVariant),
                const SizedBox(height: AppSpacing.xs),
                Text('Todavía no hay imagen de referencia',
                    style: Theme.of(context).textTheme.bodySmall, textAlign: TextAlign.center),
              ]),
            ),
          const SizedBox(height: AppSpacing.sm),
          if (exercise.videoUrl != null)
            InkWell(
              onTap: () => launchUrl(Uri.parse(exercise.videoUrl!), mode: LaunchMode.externalApplication),
              child: Row(mainAxisSize: MainAxisSize.min, children: [
                Icon(Icons.play_circle_outline, color: Theme.of(context).colorScheme.primary, size: 18),
                const SizedBox(width: AppSpacing.xs),
                Text('Ver video de demostración',
                    style: TextStyle(color: Theme.of(context).colorScheme.primary, decoration: TextDecoration.underline)),
              ]),
            )
          else
            Row(mainAxisSize: MainAxisSize.min, children: [
              Icon(Icons.play_circle_outline, size: 18, color: Theme.of(context).colorScheme.onSurfaceVariant),
              const SizedBox(width: AppSpacing.xs),
              Text('Todavía no hay video de demostración', style: Theme.of(context).textTheme.bodySmall),
            ]),
          const SizedBox(height: AppSpacing.sm),
          if (exercise.instructions?.isNotEmpty == true) ...[
            Text('Cómo hacerlo', style: Theme.of(context).textTheme.labelLarge),
            const SizedBox(height: AppSpacing.xs),
            Text(exercise.instructions!, style: Theme.of(context).textTheme.bodyMedium),
          ] else
            const Text('Sin instrucciones cargadas todavía.'),
        ]),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cerrar')),
      ],
    );
  }
}
