// =====================================================================
// MiCoach — Detalle de una lista de compra: ítems con checkbox.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_spacing.dart';
import '../application/nutrition_providers.dart';
import '../domain/shopping_list.dart';

class ShoppingListDetailScreen extends ConsumerWidget {
  final int shoppingListId;

  const ShoppingListDetailScreen({super.key, required this.shoppingListId});

  Future<void> _addItem(BuildContext context, WidgetRef ref) async {
    final nameController = TextEditingController();
    final amountController = TextEditingController();
    final unitController = TextEditingController();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Agregar ítem'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          TextField(controller: nameController, decoration: const InputDecoration(labelText: 'Ítem')),
          TextField(
              controller: amountController,
              decoration: const InputDecoration(labelText: 'Cantidad (opcional)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
          TextField(controller: unitController, decoration: const InputDecoration(labelText: 'Unidad (opcional)')),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Agregar')),
        ],
      ),
    );

    if (confirmed == true && nameController.text.trim().isNotEmpty) {
      await ref.read(nutritionActionsProvider).addShoppingListItem(
            shoppingListId,
            itemName: nameController.text.trim(),
            amount: double.tryParse(amountController.text.replaceAll(',', '.')),
            unit: unitController.text.trim().isEmpty ? null : unitController.text.trim(),
          );
    }
  }

  Future<void> _confirmDeleteList(BuildContext context, WidgetRef ref, ShoppingList list) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Borrar lista'),
        content: Text('¿Seguro que querés borrar "${list.name}"?'),
        actions: [
          TextButton(onPressed: () => Navigator.of(dialogContext).pop(false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.of(dialogContext).pop(true), child: const Text('Borrar')),
        ],
      ),
    );
    if (confirmed == true) {
      await ref.read(nutritionActionsProvider).deleteShoppingList(list.id);
      if (context.mounted) context.go('/nutrition');
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final listAsync = ref.watch(shoppingListDetailProvider(shoppingListId));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Lista de compras'),
        actions: [
          listAsync.maybeWhen(
            data: (list) => IconButton(
              icon: const Icon(Icons.delete_outline),
              onPressed: () => _confirmDeleteList(context, ref, list),
            ),
            orElse: () => const SizedBox.shrink(),
          ),
        ],
      ),
      body: listAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
        data: (list) => ListView(
          padding: const EdgeInsets.all(AppSpacing.md),
          children: [
            if (list.items.isEmpty) const Text('Todavía no agregaste ítems.'),
            for (final item in list.items)
              CheckboxListTile(
                value: item.checked,
                onChanged: (checked) => ref
                    .read(nutritionActionsProvider)
                    .setItemChecked(shoppingListId, item.id, checked ?? false),
                title: Text(
                  item.itemName ?? 'Ítem #${item.id}',
                  style: item.checked ? const TextStyle(decoration: TextDecoration.lineThrough) : null,
                ),
                subtitle: (item.amount != null || item.unit != null)
                    ? Text('${item.amount ?? ''} ${item.unit ?? ''}'.trim())
                    : null,
                secondary: IconButton(
                  icon: const Icon(Icons.delete_outline),
                  onPressed: () =>
                      ref.read(nutritionActionsProvider).deleteShoppingListItem(shoppingListId, item.id),
                ),
              ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _addItem(context, ref),
        child: const Icon(Icons.add),
      ),
    );
  }
}
