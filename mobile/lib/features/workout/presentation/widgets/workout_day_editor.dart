// =====================================================================
// MiCoach — Editor de un día de rutina (nombre, descanso, ejercicios).
// =====================================================================
import 'package:flutter/material.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../domain/workout_draft.dart';
import 'exercise_picker_dialog.dart';

class WorkoutDayEditor extends StatefulWidget {
  final WorkoutDayDraft day;
  final VoidCallback onChanged;
  final VoidCallback onRemove;

  const WorkoutDayEditor({super.key, required this.day, required this.onChanged, required this.onRemove});

  @override
  State<WorkoutDayEditor> createState() => _WorkoutDayEditorState();
}

class _WorkoutDayEditorState extends State<WorkoutDayEditor> {
  Future<void> _addExercise() async {
    final exercise = await showExercisePickerDialog(context);
    if (exercise == null) return;
    setState(() {
      widget.day.exercises.add(PlannedExerciseDraft(exerciseId: exercise.id, exerciseName: exercise.name));
    });
    widget.onChanged();
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
              child: TextFormField(
                initialValue: widget.day.name,
                decoration: InputDecoration(labelText: 'Día ${widget.day.dayIndex}'),
                onChanged: (v) => widget.day.name = v,
              ),
            ),
            const SizedBox(width: AppSpacing.sm),
            Column(children: [
              const Text('Descanso', style: TextStyle(fontSize: 12)),
              Switch(
                value: widget.day.restDay,
                onChanged: (v) => setState(() {
                  widget.day.restDay = v;
                  widget.onChanged();
                }),
              ),
            ]),
            IconButton(icon: const Icon(Icons.delete_outline), onPressed: widget.onRemove),
          ]),
          if (!widget.day.restDay) ...[
            const Divider(),
            for (var i = 0; i < widget.day.exercises.length; i++) _exerciseRow(i),
            Align(
              alignment: Alignment.centerLeft,
              child: TextButton.icon(
                icon: const Icon(Icons.add),
                label: const Text('Agregar ejercicio'),
                onPressed: _addExercise,
              ),
            ),
          ],
        ]),
      ),
    );
  }

  Widget _exerciseRow(int index) {
    final exercise = widget.day.exercises[index];
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppSpacing.xs),
      child: Row(children: [
        Expanded(flex: 3, child: Text(exercise.exerciseName, overflow: TextOverflow.ellipsis)),
        Expanded(
          flex: 2,
          child: TextFormField(
            initialValue: exercise.sets.toString(),
            decoration: const InputDecoration(labelText: 'Series', isDense: true),
            keyboardType: TextInputType.number,
            onChanged: (v) => exercise.sets = int.tryParse(v) ?? exercise.sets,
          ),
        ),
        const SizedBox(width: AppSpacing.xs),
        Expanded(
          flex: 2,
          child: TextFormField(
            initialValue: exercise.repsMin?.toString() ?? '',
            decoration: const InputDecoration(labelText: 'Reps min', isDense: true),
            keyboardType: TextInputType.number,
            onChanged: (v) => exercise.repsMin = int.tryParse(v),
          ),
        ),
        const SizedBox(width: AppSpacing.xs),
        Expanded(
          flex: 2,
          child: TextFormField(
            initialValue: exercise.repsMax?.toString() ?? '',
            decoration: const InputDecoration(labelText: 'Reps max', isDense: true),
            keyboardType: TextInputType.number,
            onChanged: (v) => exercise.repsMax = int.tryParse(v),
          ),
        ),
        IconButton(
          icon: const Icon(Icons.close, size: 18),
          onPressed: () => setState(() {
            widget.day.exercises.removeAt(index);
            widget.onChanged();
          }),
        ),
      ]),
    );
  }
}
