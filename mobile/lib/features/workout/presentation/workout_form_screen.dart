// =====================================================================
// MiCoach — Crear / editar una rutina propia.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_spacing.dart';
import '../application/workout_providers.dart';
import '../domain/workout.dart';
import '../domain/workout_draft.dart';
import 'widgets/workout_day_editor.dart';
import 'workout_labels.dart';

class WorkoutFormScreen extends ConsumerStatefulWidget {
  final int? workoutId;

  const WorkoutFormScreen({super.key, this.workoutId});

  @override
  ConsumerState<WorkoutFormScreen> createState() => _WorkoutFormScreenState();
}

class _WorkoutFormScreenState extends ConsumerState<WorkoutFormScreen> {
  late WorkoutDraft _draft;
  late final TextEditingController _nameController;
  late final TextEditingController _descriptionController;
  late final TextEditingController _durationController;
  String? _objective;
  String? _level;
  bool _initialized = false;
  bool _saving = false;

  bool get _isEditing => widget.workoutId != null;

  void _initFromWorkout(Workout workout) {
    _draft = WorkoutDraft(
      name: workout.name,
      description: workout.description,
      objective: workout.objective,
      level: workout.level,
      durationWeeks: workout.durationWeeks,
      days: workout.days
          .map((d) => WorkoutDayDraft(
                dayIndex: d.dayIndex,
                name: d.name,
                restDay: d.restDay,
                exercises: d.exercises
                    .map((e) => PlannedExerciseDraft.fromExisting(e, 'Ejercicio #${e.exerciseId}'))
                    .toList(),
              ))
          .toList(),
    );
    _nameController.text = _draft.name;
    _descriptionController.text = _draft.description ?? '';
    _durationController.text = _draft.durationWeeks?.toString() ?? '';
    _objective = _draft.objective;
    _level = _draft.level;
  }

  @override
  void initState() {
    super.initState();
    _draft = WorkoutDraft();
    _nameController = TextEditingController();
    _descriptionController = TextEditingController();
    _durationController = TextEditingController();
    if (!_isEditing) _initialized = true;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _durationController.dispose();
    super.dispose();
  }

  void _addDay() {
    setState(() {
      _draft.days.add(WorkoutDayDraft(dayIndex: _draft.days.length + 1));
    });
  }

  void _removeDay(int index) {
    setState(() {
      _draft.days.removeAt(index);
      for (var i = 0; i < _draft.days.length; i++) {
        _draft.days[i].dayIndex = i + 1;
      }
    });
  }

  Future<void> _save() async {
    if (_nameController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('El nombre es obligatorio')));
      return;
    }
    setState(() => _saving = true);
    _draft.name = _nameController.text.trim();
    _draft.description = _descriptionController.text.trim().isEmpty ? null : _descriptionController.text.trim();
    _draft.objective = _objective;
    _draft.level = _level;
    _draft.durationWeeks = int.tryParse(_durationController.text);

    try {
      final actions = ref.read(workoutActionsProvider);
      final Workout saved;
      if (_isEditing) {
        saved = await actions.updateWorkout(widget.workoutId!, _draft);
      } else {
        saved = await actions.createWorkout(_draft);
      }
      if (mounted) context.go('/workouts/${saved.id}');
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isEditing && !_initialized) {
      final workoutAsync = ref.watch(workoutDetailProvider(widget.workoutId!));
      return Scaffold(
        appBar: AppBar(title: const Text('Editar rutina')),
        body: workoutAsync.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(child: Text('Error: $e')),
          data: (workout) {
            _initFromWorkout(workout);
            _initialized = true;
            return _buildForm();
          },
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(title: Text(_isEditing ? 'Editar rutina' : 'Nueva rutina')),
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
        DropdownButtonFormField<String>(
          initialValue: _objective,
          decoration: const InputDecoration(labelText: 'Objetivo'),
          items: objectiveLabels.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
          onChanged: (v) => setState(() => _objective = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _level,
          decoration: const InputDecoration(labelText: 'Nivel'),
          items: levelLabels.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
          onChanged: (v) => setState(() => _level = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        TextField(
          controller: _durationController,
          decoration: const InputDecoration(labelText: 'Duración (semanas)'),
          keyboardType: TextInputType.number,
        ),
        const SizedBox(height: AppSpacing.lg),
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Text('Días', style: Theme.of(context).textTheme.titleMedium),
          TextButton.icon(icon: const Icon(Icons.add), label: const Text('Agregar día'), onPressed: _addDay),
        ]),
        const SizedBox(height: AppSpacing.sm),
        for (var i = 0; i < _draft.days.length; i++)
          WorkoutDayEditor(
            key: ValueKey(_draft.days[i]),
            day: _draft.days[i],
            onChanged: () {},
            onRemove: () => _removeDay(i),
          ),
        const SizedBox(height: AppSpacing.lg),
        FilledButton(
          onPressed: _saving ? null : _save,
          child: _saving
              ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
              : Text(_isEditing ? 'Guardar cambios' : 'Crear rutina'),
        ),
        const SizedBox(height: AppSpacing.xl),
      ],
    );
  }
}
