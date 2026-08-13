// =====================================================================
// KineticOs — Lista de listas de compra del usuario.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/nutrition_providers.dart';

class ShoppingListListView extends ConsumerWidget {
  const ShoppingListListView({super.key});

  Future<void> _createList(BuildContext context, WidgetRef ref) async {
    final controller = TextEditingController(text: 'Lista de la semana');
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Nueva lista'),
        content: TextField(controller: controller, decoration: const InputDecoration(labelText: 'Nombre')),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Crear')),
        ],
      ),
    );
    if (confirmed == true) {
      final created = await ref.read(nutritionActionsProvider).createShoppingList(name: controller.text.trim());
      if (context.mounted) context.push('/nutrition/shopping-lists/${created.id}');
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final listsAsync = ref.watch(shoppingListsProvider);

    return Scaffold(
      body: listsAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (lists) {
          if (lists.isEmpty) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(AppSpacing.lg),
                child: Text('Todavía no creaste ninguna lista de compras.', textAlign: TextAlign.center),
              ),
            );
          }
          return RefreshIndicator(
            onRefresh: () async => ref.invalidate(shoppingListsProvider),
            child: ListView.builder(
              padding: const EdgeInsets.all(AppSpacing.md),
              itemCount: lists.length,
              itemBuilder: (context, index) {
                final list = lists[index];
                final checkedCount = list.items.where((i) => i.checked).length;
                return Card(
                  margin: const EdgeInsets.only(bottom: AppSpacing.sm),
                  child: ListTile(
                    title: Text(list.name),
                    subtitle: Text('${list.items.length} ítems · $checkedCount comprados'),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => context.push('/nutrition/shopping-lists/${list.id}'),
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _createList(context, ref),
        child: const Icon(Icons.add),
      ),
    );
  }
}
