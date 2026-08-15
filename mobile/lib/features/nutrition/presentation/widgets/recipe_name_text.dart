// =====================================================================
// MiCoach — Resuelve y muestra el nombre de una receta por id (las
// comidas de un plan del backend solo traen recipeId).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../application/nutrition_providers.dart';

class RecipeNameText extends ConsumerWidget {
  final int? recipeId;
  final TextStyle? style;

  const RecipeNameText({super.key, required this.recipeId, this.style});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (recipeId == null) return Text('Comida libre', style: style);
    final recipeAsync = ref.watch(recipeDetailProvider(recipeId!));
    return recipeAsync.when(
      loading: () => Text('Cargando…', style: style),
      error: (e, _) => Text('Receta #$recipeId', style: style),
      data: (recipe) => Text(recipe.name, style: style),
    );
  }
}
