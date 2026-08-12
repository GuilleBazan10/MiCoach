// =====================================================================
// KineticOs — Sección "Lesiones" del perfil.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/profile_providers.dart';
import '../../domain/user_subresources.dart';

const _injuryStatusOptions = {'active': 'Activa', 'recovered': 'Recuperada', 'chronic': 'Crónica'};

class InjurySection extends ConsumerWidget {
  final List<UserInjury> injuries;

  const InjurySection({super.key, required this.injuries});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      child: ExpansionTile(
        title: Text('Lesiones (${injuries.length})'),
        children: [
          for (final item in injuries)
            ListTile(
              title: Text('${item.bodyPart} — ${item.injuryType}'),
              subtitle: Text(_injuryStatusOptions[item.status] ?? item.status ?? ''),
              trailing: IconButton(
                icon: const Icon(Icons.delete_outline),
                onPressed: () => ref.read(profileControllerProvider.notifier).deleteInjury(item.id),
              ),
            ),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.sm),
            child: OutlinedButton.icon(
              icon: const Icon(Icons.add),
              label: const Text('Agregar lesión'),
              onPressed: () => _showAddDialog(context, ref),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _showAddDialog(BuildContext context, WidgetRef ref) async {
    final bodyPartController = TextEditingController();
    final injuryTypeController = TextEditingController();
    String status = 'active';

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => StatefulBuilder(builder: (dialogContext, setDialogState) {
        return AlertDialog(
          title: const Text('Nueva lesión'),
          content: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(
                controller: bodyPartController,
                decoration: const InputDecoration(labelText: 'Zona (ej: rodilla)')),
            TextField(
                controller: injuryTypeController,
                decoration: const InputDecoration(labelText: 'Tipo de lesión')),
            DropdownButtonFormField<String>(
              initialValue: status,
              decoration: const InputDecoration(labelText: 'Estado'),
              items: _injuryStatusOptions.entries
                  .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
                  .toList(),
              onChanged: (v) => setDialogState(() => status = v ?? status),
            ),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
            FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
          ],
        );
      }),
    );

    if (confirmed == true &&
        bodyPartController.text.trim().isNotEmpty &&
        injuryTypeController.text.trim().isNotEmpty) {
      await ref.read(profileControllerProvider.notifier).addInjury(
            bodyPart: bodyPartController.text.trim(),
            injuryType: injuryTypeController.text.trim(),
            status: status,
          );
    }
  }
}
