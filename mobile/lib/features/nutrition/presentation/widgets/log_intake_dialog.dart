// =====================================================================
// MiCoach — Diálogo para registrar una comida en el diario alimentario.
// Si se elige una receta, las macros se auto-completan (receta x porciones)
// pero siguen siendo editables a mano.
// =====================================================================
import 'package:flutter/material.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../domain/recipe.dart';
import '../nutrition_labels.dart';
import 'recipe_picker_dialog.dart';

class LogIntakeResult {
  final int? recipeId;
  final String mealType;
  final double? amount;
  final double? calories;
  final double? proteinG;
  final double? carbsG;
  final double? fatG;

  const LogIntakeResult({
    this.recipeId,
    required this.mealType,
    this.amount,
    this.calories,
    this.proteinG,
    this.carbsG,
    this.fatG,
  });
}

Future<LogIntakeResult?> showLogIntakeDialog(BuildContext context) {
  return showDialog<LogIntakeResult>(context: context, builder: (_) => const _LogIntakeDialog());
}

class _LogIntakeDialog extends StatefulWidget {
  const _LogIntakeDialog();

  @override
  State<_LogIntakeDialog> createState() => _LogIntakeDialogState();
}

class _LogIntakeDialogState extends State<_LogIntakeDialog> {
  String _mealType = 'breakfast';
  Recipe? _recipe;
  final _servingsController = TextEditingController(text: '1');
  final _caloriesController = TextEditingController();
  final _proteinController = TextEditingController();
  final _carbsController = TextEditingController();
  final _fatController = TextEditingController();

  @override
  void dispose() {
    _servingsController.dispose();
    _caloriesController.dispose();
    _proteinController.dispose();
    _carbsController.dispose();
    _fatController.dispose();
    super.dispose();
  }

  Future<void> _pickRecipe() async {
    final recipe = await showRecipePickerDialog(context, mealCategory: _mealType);
    if (recipe == null) return;
    setState(() {
      _recipe = recipe;
      _recalculate();
    });
  }

  void _recalculate() {
    final servings = double.tryParse(_servingsController.text.replaceAll(',', '.')) ?? 1;
    if (_recipe == null) return;
    if (_recipe!.caloriesPerServing != null) {
      _caloriesController.text = (_recipe!.caloriesPerServing! * servings).toStringAsFixed(1);
    }
    if (_recipe!.proteinPerServing != null) {
      _proteinController.text = (_recipe!.proteinPerServing! * servings).toStringAsFixed(1);
    }
    if (_recipe!.carbsPerServing != null) {
      _carbsController.text = (_recipe!.carbsPerServing! * servings).toStringAsFixed(1);
    }
    if (_recipe!.fatPerServing != null) {
      _fatController.text = (_recipe!.fatPerServing! * servings).toStringAsFixed(1);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Registrar comida'),
      content: SingleChildScrollView(
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          DropdownButtonFormField<String>(
            initialValue: _mealType,
            decoration: const InputDecoration(labelText: 'Comida'),
            items: mealTypeLabels.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
            onChanged: (v) => setState(() => _mealType = v ?? _mealType),
          ),
          const SizedBox(height: AppSpacing.sm),
          OutlinedButton.icon(
            icon: const Icon(Icons.restaurant_menu),
            label: Text(_recipe?.name ?? 'Elegir receta (opcional)'),
            onPressed: _pickRecipe,
          ),
          if (_recipe != null) ...[
            const SizedBox(height: AppSpacing.sm),
            TextField(
              controller: _servingsController,
              decoration: const InputDecoration(labelText: 'Porciones'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              onChanged: (_) => setState(_recalculate),
            ),
          ],
          const SizedBox(height: AppSpacing.sm),
          TextField(
              controller: _caloriesController,
              decoration: const InputDecoration(labelText: 'Calorías'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
          TextField(
              controller: _proteinController,
              decoration: const InputDecoration(labelText: 'Proteína (g)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
          TextField(
              controller: _carbsController,
              decoration: const InputDecoration(labelText: 'Carbohidratos (g)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
          TextField(
              controller: _fatController,
              decoration: const InputDecoration(labelText: 'Grasas (g)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true)),
        ]),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cancelar')),
        FilledButton(
          onPressed: () => Navigator.of(context).pop(LogIntakeResult(
            recipeId: _recipe?.id,
            mealType: _mealType,
            amount: double.tryParse(_servingsController.text.replaceAll(',', '.')),
            calories: double.tryParse(_caloriesController.text.replaceAll(',', '.')),
            proteinG: double.tryParse(_proteinController.text.replaceAll(',', '.')),
            carbsG: double.tryParse(_carbsController.text.replaceAll(',', '.')),
            fatG: double.tryParse(_fatController.text.replaceAll(',', '.')),
          )),
          child: const Text('Registrar'),
        ),
      ],
    );
  }
}
