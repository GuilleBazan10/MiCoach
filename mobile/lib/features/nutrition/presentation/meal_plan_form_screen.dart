// =====================================================================
// MiCoach — Crear / editar un plan de alimentación.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_spacing.dart';
import '../application/nutrition_providers.dart';
import '../domain/meal_plan.dart';
import '../domain/meal_plan_draft.dart';
import 'widgets/meal_plan_day_editor.dart';

class MealPlanFormScreen extends ConsumerStatefulWidget {
  final int? mealPlanId;

  const MealPlanFormScreen({super.key, this.mealPlanId});

  @override
  ConsumerState<MealPlanFormScreen> createState() => _MealPlanFormScreenState();
}

class _MealPlanFormScreenState extends ConsumerState<MealPlanFormScreen> {
  late MealPlanDraft _draft;
  late final TextEditingController _nameController;
  late final TextEditingController _descriptionController;
  late final TextEditingController _caloriesController;
  bool _initialized = false;
  bool _saving = false;

  bool get _isEditing => widget.mealPlanId != null;

  void _initFromPlan(MealPlan plan) {
    _draft = MealPlanDraft(
      name: plan.name,
      description: plan.description,
      startDate: plan.startDate,
      endDate: plan.endDate,
      targetCalories: plan.targetCalories,
      days: plan.days
          .map((d) => MealPlanDayDraft(
                planDate: d.planDate,
                meals: d.meals
                    .map((m) => MealPlanMealDraft.fromExisting(m, 'Receta #${m.recipeId}'))
                    .toList(),
              ))
          .toList(),
    );
    _nameController.text = _draft.name;
    _descriptionController.text = _draft.description ?? '';
    _caloriesController.text = _draft.targetCalories?.toString() ?? '';
  }

  @override
  void initState() {
    super.initState();
    _draft = MealPlanDraft();
    _nameController = TextEditingController();
    _descriptionController = TextEditingController();
    _caloriesController = TextEditingController();
    if (!_isEditing) _initialized = true;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _caloriesController.dispose();
    super.dispose();
  }

  Future<void> _pickStartDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _draft.startDate,
      firstDate: DateTime.now().subtract(const Duration(days: 365)),
      lastDate: DateTime.now().add(const Duration(days: 365)),
    );
    if (picked != null) setState(() => _draft.startDate = picked);
  }

  Future<void> _pickEndDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _draft.endDate,
      firstDate: DateTime.now().subtract(const Duration(days: 365)),
      lastDate: DateTime.now().add(const Duration(days: 365)),
    );
    if (picked != null) setState(() => _draft.endDate = picked);
  }

  void _addDay() {
    setState(() => _draft.days.add(MealPlanDayDraft(planDate: _draft.startDate)));
  }

  Future<void> _save() async {
    if (_nameController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('El nombre es obligatorio')));
      return;
    }
    setState(() => _saving = true);
    _draft.name = _nameController.text.trim();
    _draft.description = _descriptionController.text.trim().isEmpty ? null : _descriptionController.text.trim();
    _draft.targetCalories = int.tryParse(_caloriesController.text);

    try {
      final actions = ref.read(nutritionActionsProvider);
      final MealPlan saved;
      if (_isEditing) {
        saved = await actions.updateMealPlan(widget.mealPlanId!, _draft);
      } else {
        saved = await actions.createMealPlan(_draft);
      }
      if (mounted) context.go('/nutrition/plans/${saved.id}');
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isEditing && !_initialized) {
      final planAsync = ref.watch(mealPlanDetailProvider(widget.mealPlanId!));
      return Scaffold(
        appBar: AppBar(title: const Text('Editar plan')),
        body: planAsync.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(child: Text('Error: $e')),
          data: (plan) {
            _initFromPlan(plan);
            _initialized = true;
            return _buildForm();
          },
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(title: Text(_isEditing ? 'Editar plan' : 'Nuevo plan')),
      body: _buildForm(),
    );
  }

  Widget _buildForm() {
    return ListView(
      padding: const EdgeInsets.all(AppSpacing.md),
      children: [
        TextField(controller: _nameController, decoration: const InputDecoration(labelText: 'Nombre *')),
        const SizedBox(height: AppSpacing.sm),
        TextField(
            controller: _descriptionController,
            decoration: const InputDecoration(labelText: 'Descripción'),
            maxLines: 2),
        const SizedBox(height: AppSpacing.sm),
        Row(children: [
          Expanded(
            child: InkWell(
              onTap: _pickStartDate,
              child: InputDecorator(
                decoration: const InputDecoration(labelText: 'Desde'),
                child: Text('${_draft.startDate.day}/${_draft.startDate.month}/${_draft.startDate.year}'),
              ),
            ),
          ),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: InkWell(
              onTap: _pickEndDate,
              child: InputDecorator(
                decoration: const InputDecoration(labelText: 'Hasta'),
                child: Text('${_draft.endDate.day}/${_draft.endDate.month}/${_draft.endDate.year}'),
              ),
            ),
          ),
        ]),
        const SizedBox(height: AppSpacing.sm),
        TextField(
          controller: _caloriesController,
          decoration: const InputDecoration(labelText: 'Calorías objetivo (opcional)'),
          keyboardType: TextInputType.number,
        ),
        const SizedBox(height: AppSpacing.lg),
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Text('Días', style: Theme.of(context).textTheme.titleMedium),
          TextButton.icon(icon: const Icon(Icons.add), label: const Text('Agregar día'), onPressed: _addDay),
        ]),
        const SizedBox(height: AppSpacing.sm),
        for (var i = 0; i < _draft.days.length; i++)
          MealPlanDayEditor(
            key: ValueKey(_draft.days[i]),
            day: _draft.days[i],
            onRemove: () => setState(() => _draft.days.removeAt(i)),
          ),
        const SizedBox(height: AppSpacing.lg),
        FilledButton(
          onPressed: _saving ? null : _save,
          child: _saving
              ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
              : Text(_isEditing ? 'Guardar cambios' : 'Crear plan'),
        ),
        const SizedBox(height: AppSpacing.xl),
      ],
    );
  }
}
