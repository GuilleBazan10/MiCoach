// =====================================================================
// KineticOs — Pestaña "Fotos": galería de progreso físico.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/progress_providers.dart';
import '../progress_labels.dart';

final _dateFormat = DateFormat('dd/MM/yyyy');

class ProgressPhotosView extends ConsumerWidget {
  const ProgressPhotosView({super.key});

  Future<void> _addPhoto(BuildContext context, WidgetRef ref) async {
    final urlController = TextEditingController();
    String? angle;

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => StatefulBuilder(builder: (dialogContext, setDialogState) {
        return AlertDialog(
          title: const Text('Agregar foto'),
          content: Column(mainAxisSize: MainAxisSize.min, children: [
            TextField(
              controller: urlController,
              decoration: const InputDecoration(labelText: 'URL de la foto'),
            ),
            const SizedBox(height: AppSpacing.sm),
            DropdownButtonFormField<String>(
              initialValue: angle,
              decoration: const InputDecoration(labelText: 'Ángulo (opcional)'),
              items: photoAngleLabels.entries
                  .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
                  .toList(),
              onChanged: (v) => setDialogState(() => angle = v),
            ),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
            FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
          ],
        );
      }),
    );

    if (confirmed == true && urlController.text.trim().isNotEmpty) {
      await ref.read(progressActionsProvider).addPhoto(photoUrl: urlController.text.trim(), angle: angle);
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final photosAsync = ref.watch(progressPhotosProvider);

    return Scaffold(
      body: photosAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (photos) {
          if (photos.isEmpty) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(AppSpacing.lg),
                child: Text('Todavía no subiste ninguna foto de progreso.', textAlign: TextAlign.center),
              ),
            );
          }
          return RefreshIndicator(
            onRefresh: () async => ref.invalidate(progressPhotosProvider),
            child: GridView.builder(
              padding: const EdgeInsets.all(AppSpacing.md),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                mainAxisSpacing: AppSpacing.sm,
                crossAxisSpacing: AppSpacing.sm,
                childAspectRatio: 0.8,
              ),
              itemCount: photos.length,
              itemBuilder: (context, index) {
                final photo = photos[index];
                return Card(
                  clipBehavior: Clip.antiAlias,
                  child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                    Expanded(
                      child: Image.network(
                        photo.photoUrl,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) =>
                            const Center(child: Icon(Icons.broken_image_outlined)),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(AppSpacing.xs),
                      child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                        Expanded(
                          child: Text(
                            '${labelFor(photoAngleLabels, photo.angle)} · ${_dateFormat.format(photo.takenAt.toLocal())}',
                            style: Theme.of(context).textTheme.bodySmall,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete_outline, size: 18),
                          onPressed: () => ref.read(progressActionsProvider).deletePhoto(photo.id),
                        ),
                      ]),
                    ),
                  ]),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _addPhoto(context, ref),
        child: const Icon(Icons.add_a_photo_outlined),
      ),
    );
  }
}
