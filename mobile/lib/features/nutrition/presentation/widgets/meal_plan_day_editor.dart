// =====================================================================
// MiCoach — Editor de un día del plan (fecha + comidas).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../domain/meal_plan_draft.dart';
import '../nutrition_labels.dart';
import 'recipe_picker_dialog.dart';

final _dateFormat = DateFormat('dd/MM/yyyy');

class MealPlanDayEditor extends StatefulWidget {
  final MealPlanDayDraft day;
  final VoidCallback onRemove;

  const MealPlanDayEditor({super.key, required this.day, required this.onRemove});

  @override
  State<MealPlanDayEditor> createState() => _MealPlanDayEditorState();
}

class _MealPlanDayEditorState extends State<MealPlanDayEditor> {
  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: widget.day.planDate,
      firstDate: DateTime.now().subtract(const Duration(days: 365)),
      lastDate: DateTime.now().add(const Duration(days: 365)),
    );
    if (picked != null) setState(() => widget.day.planDate = picked);
  }

  Future<void> _addMeal() async {
    final recipe = await showRecipePickerDialog(context);
    if (recipe == null) return;
    setState(() {
      widget.day.meals.add(MealPlanMealDraft(recipeId: recipe.id, recipeName: recipe.name));
    });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: AppSpacing.sm),
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.sm),
        child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
          Row(children: [
            Expanded(
              child: InkWell(
                onTap: _pickDate,
                child: InputDecorator(
                  decoration: const InputDecoration(labelText: 'Fecha'),
                  child: Text(_dateFormat.format(widget.day.planDate)),
                ),
              ),
            ),
            IconButton(icon: const Icon(Icons.delete_outline), onPressed: widget.onRemove),
          ]),
          const Divider(),
          for (var i = 0; i < widget.day.meals.length; i++) _mealRow(i),
          Align(
            alignment: Alignment.centerLeft,
            child: TextButton.icon(icon: const Icon(Icons.add), label: const Text('Agregar comida'), onPressed: _addMeal),
          ),
        ]),
      ),
    );
  }

  Widget _mealRow(int index) {
    final meal = widget.day.meals[index];
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppSpacing.xs),
      child: Row(children: [
        Expanded(flex: 3, child: Text(meal.recipeName, overflow: TextOverflow.ellipsis)),
        Expanded(
          flex: 2,
          child: DropdownButtonFormField<String>(
            initialValue: meal.mealType,
            decoration: const InputDecoration(isDense: true),
            items: mealTypeLabels.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
            onChanged: (v) => setState(() => meal.mealType = v ?? meal.mealType),
          ),
        ),
        const SizedBox(width: AppSpacing.xs),
        Expanded(
          flex: 1,
          child: TextFormField(
            initialValue: meal.servings.toString(),
            decoration: const InputDecoration(labelText: 'Porc.', isDense: true),
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            onChanged: (v) => meal.servings = double.tryParse(v.replaceAll(',', '.')) ?? meal.servings,
          ),
        ),
        IconButton(
          icon: const Icon(Icons.close, size: 18),
          onPressed: () => setState(() => widget.day.meals.removeAt(index)),
        ),
      ]),
    );
  }
}
