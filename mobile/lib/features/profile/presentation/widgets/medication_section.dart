// =====================================================================
// KineticOs — Sección "Medicación" del perfil.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/profile_providers.dart';
import '../../domain/user_subresources.dart';

class MedicationSection extends ConsumerWidget {
  final List<UserMedication> medications;

  const MedicationSection({super.key, required this.medications});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      child: ExpansionTile(
        title: Text('Medicación (${medications.length})'),
        children: [
          for (final item in medications)
            ListTile(
              title: Text(item.medicationName),
              subtitle: item.schedule != null ? Text(item.schedule!) : null,
              trailing: IconButton(
                icon: const Icon(Icons.delete_outline),
                onPressed: () => ref.read(profileControllerProvider.notifier).deleteMedication(item.id),
              ),
            ),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.sm),
            child: OutlinedButton.icon(
              icon: const Icon(Icons.add),
              label: const Text('Agregar medicación'),
              onPressed: () => _showAddDialog(context, ref),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _showAddDialog(BuildContext context, WidgetRef ref) async {
    final nameController = TextEditingController();
    final dosageController = TextEditingController();
    final scheduleController = TextEditingController();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Nueva medicación'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: nameController, decoration: const InputDecoration(labelText: 'Nombre')),
          TextField(controller: dosageController, decoration: const InputDecoration(labelText: 'Dosis (opcional)')),
          TextField(
              controller: scheduleController,
              decoration: const InputDecoration(labelText: 'Frecuencia (opcional)')),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
        ],
      ),
    );

    if (confirmed == true && nameController.text.trim().isNotEmpty) {
      await ref.read(profileControllerProvider.notifier).addMedication(
            medicationName: nameController.text.trim(),
            dosage: dosageController.text.trim().isEmpty ? null : dosageController.text.trim(),
            schedule: scheduleController.text.trim().isEmpty ? null : scheduleController.text.trim(),
          );
    }
  }
}
