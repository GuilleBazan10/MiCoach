// =====================================================================
// KineticOs — Sección "Objetivos" del perfil.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/profile_providers.dart';
import '../../domain/user_subresources.dart';

const _goalTypeOptions = {
  'lose_fat': 'Perder grasa',
  'gain_muscle': 'Ganar músculo',
  'maintain_weight': 'Mantener peso',
  'endurance': 'Resistencia',
  'strength': 'Fuerza',
  'flexibility': 'Flexibilidad',
  'general_health': 'Salud general',
};

class GoalSection extends ConsumerWidget {
  final List<UserGoal> goals;

  const GoalSection({super.key, required this.goals});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      child: ExpansionTile(
        title: Text('Objetivos (${goals.length})'),
        children: [
          for (final goal in goals)
            ListTile(
              title: Text(_goalTypeOptions[goal.goalType] ?? goal.goalType),
              subtitle: goal.targetValue != null
                  ? Text('Meta: ${goal.targetValue} ${goal.targetUnit ?? ''}')
                  : null,
              trailing: IconButton(
                icon: const Icon(Icons.delete_outline),
                onPressed: () => ref.read(profileControllerProvider.notifier).deleteGoal(goal.id),
              ),
            ),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.sm),
            child: OutlinedButton.icon(
              icon: const Icon(Icons.add),
              label: const Text('Agregar objetivo'),
              onPressed: () => _showAddDialog(context, ref),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _showAddDialog(BuildContext context, WidgetRef ref) async {
    String goalType = _goalTypeOptions.keys.first;
    final targetValueController = TextEditingController();
    final targetUnitController = TextEditingController();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => StatefulBuilder(builder: (dialogContext, setDialogState) {
        return AlertDialog(
          title: const Text('Nuevo objetivo'),
          content: Column(mainAxisSize: MainAxisSize.min, children: [
            DropdownButtonFormField<String>(
              initialValue: goalType,
              decoration: const InputDecoration(labelText: 'Tipo'),
              items: _goalTypeOptions.entries
                  .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
                  .toList(),
              onChanged: (v) => setDialogState(() => goalType = v ?? goalType),
            ),
            TextField(
              controller: targetValueController,
              decoration: const InputDecoration(labelText: 'Valor objetivo (opcional)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
            ),
            TextField(
              controller: targetUnitController,
              decoration: const InputDecoration(labelText: 'Unidad (kg, cm, min...)'),
            ),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
            FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
          ],
        );
      }),
    );

    if (confirmed == true) {
      await ref.read(profileControllerProvider.notifier).addGoal(
            goalType: goalType,
            targetValue: double.tryParse(targetValueController.text.replaceAll(',', '.')),
            targetUnit: targetUnitController.text.trim().isEmpty ? null : targetUnitController.text.trim(),
          );
    }
  }
}
