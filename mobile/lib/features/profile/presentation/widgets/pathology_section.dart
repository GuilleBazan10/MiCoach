// =====================================================================
// KineticOs — Sección "Patologías" del perfil.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/profile_providers.dart';
import '../../domain/user_subresources.dart';

class PathologySection extends ConsumerWidget {
  final List<UserPathology> pathologies;

  const PathologySection({super.key, required this.pathologies});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      child: ExpansionTile(
        title: Text('Patologías (${pathologies.length})'),
        children: [
          for (final item in pathologies)
            ListTile(
              title: Text(item.pathology),
              subtitle: item.notes != null ? Text(item.notes!) : null,
              trailing: IconButton(
                icon: const Icon(Icons.delete_outline),
                onPressed: () => ref.read(profileControllerProvider.notifier).deletePathology(item.id),
              ),
            ),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.sm),
            child: OutlinedButton.icon(
              icon: const Icon(Icons.add),
              label: const Text('Agregar patología'),
              onPressed: () => _showAddDialog(context, ref),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _showAddDialog(BuildContext context, WidgetRef ref) async {
    final pathologyController = TextEditingController();
    final notesController = TextEditingController();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Nueva patología'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: pathologyController, decoration: const InputDecoration(labelText: 'Patología')),
          TextField(controller: notesController, decoration: const InputDecoration(labelText: 'Notas (opcional)')),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
        ],
      ),
    );

    if (confirmed == true && pathologyController.text.trim().isNotEmpty) {
      await ref.read(profileControllerProvider.notifier).addPathology(
            pathology: pathologyController.text.trim(),
            notes: notesController.text.trim().isEmpty ? null : notesController.text.trim(),
          );
    }
  }
}
