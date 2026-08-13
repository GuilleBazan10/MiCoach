// =====================================================================
// KineticOs — Diálogo para buscar y elegir una receta del catálogo.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/nutrition_providers.dart';
import '../../domain/recipe.dart';
import '../../domain/recipe_filter.dart';
import '../nutrition_labels.dart';

/// Muestra el diálogo y devuelve la receta elegida, o null si se cancela.
Future<Recipe?> showRecipePickerDialog(BuildContext context, {String? mealCategory}) {
  return showDialog<Recipe>(
    context: context,
    builder: (_) => _RecipePickerDialog(mealCategory: mealCategory),
  );
}

class _RecipePickerDialog extends ConsumerStatefulWidget {
  final String? mealCategory;

  const _RecipePickerDialog({this.mealCategory});

  @override
  ConsumerState<_RecipePickerDialog> createState() => _RecipePickerDialogState();
}

class _RecipePickerDialogState extends ConsumerState<_RecipePickerDialog> {
  String _search = '';

  @override
  Widget build(BuildContext context) {
    final filter = RecipeFilter(mealCategory: widget.mealCategory, search: _search.isEmpty ? null : _search);
    final recipesAsync = ref.watch(recipeCatalogProvider(filter));

    return Dialog(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 480, maxHeight: 560),
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.md),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text('Elegir receta', style: Theme.of(context).textTheme.titleLarge),
              const SizedBox(height: AppSpacing.sm),
              TextField(
                decoration: const InputDecoration(labelText: 'Buscar', prefixIcon: Icon(Icons.search)),
                onChanged: (v) => setState(() => _search = v),
              ),
              const SizedBox(height: AppSpacing.sm),
              Expanded(
                child: recipesAsync.when(
                  loading: () => const Center(child: CircularProgressIndicator()),
                  error: (e, _) => Center(child: Text('Error: $e')),
                  data: (recipes) => recipes.isEmpty
                      ? const Center(child: Text('Sin resultados'))
                      : ListView.builder(
                          itemCount: recipes.length,
                          itemBuilder: (context, index) {
                            final recipe = recipes[index];
                            return ListTile(
                              title: Text(recipe.name),
                              subtitle: Text([
                                labelFor(mealCategoryLabels, recipe.mealCategory),
                                if (recipe.caloriesPerServing != null)
                                  '${recipe.caloriesPerServing!.round()} kcal/porción',
                              ].join(' · ')),
                              onTap: () => Navigator.of(context).pop(recipe),
                            );
                          },
                        ),
                ),
              ),
              const SizedBox(height: AppSpacing.sm),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cancelar')),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
