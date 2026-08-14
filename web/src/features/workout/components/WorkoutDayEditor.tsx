// Editor de un día de rutina (nombre, descanso, ejercicios). Paridad con
// workout_day_editor.dart, adaptado a actualización inmutable (React).
import { useState } from 'react';
import { Plus, Trash2, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import type { PlannedExerciseDraft, WorkoutDayDraft } from '../domain/workoutTypes';
import { ExercisePickerDialog } from './ExercisePickerDialog';

interface WorkoutDayEditorProps {
  day: WorkoutDayDraft;
  onChange: (day: WorkoutDayDraft) => void;
  onRemove: () => void;
}

export function WorkoutDayEditor({ day, onChange, onRemove }: WorkoutDayEditorProps) {
  const [pickerOpen, setPickerOpen] = useState(false);

  function updateExercise(index: number, patch: Partial<PlannedExerciseDraft>) {
    const exercises = day.exercises.map((e, i) => (i === index ? { ...e, ...patch } : e));
    onChange({ ...day, exercises });
  }

  function removeExercise(index: number) {
    onChange({ ...day, exercises: day.exercises.filter((_, i) => i !== index) });
  }

  return (
    <Card>
      <CardContent className="flex flex-col gap-3">
        <div className="flex items-end gap-3">
          <div className="flex-1">
            <Label htmlFor={`day-name-${day.dayIndex}`}>Día {day.dayIndex}</Label>
            <Input
              id={`day-name-${day.dayIndex}`}
              value={day.name ?? ''}
              onChange={(e) => onChange({ ...day, name: e.target.value })}
              className="mt-1.5"
            />
          </div>
          <div className="flex flex-col items-center gap-1.5">
            <span className="text-xs text-muted-foreground">Descanso</span>
            <Switch checked={day.restDay} onCheckedChange={(checked) => onChange({ ...day, restDay: checked })} />
          </div>
          <Button type="button" variant="ghost" size="icon" aria-label="Borrar día" onClick={onRemove}>
            <Trash2 />
          </Button>
        </div>

        {!day.restDay && (
          <div className="flex flex-col gap-2 border-t border-border pt-3">
            {day.exercises.map((exercise, index) => (
              <div key={index} className="grid grid-cols-[2fr_1fr_1fr_1fr_auto] items-end gap-1.5">
                <p className="truncate text-sm" title={exercise.exerciseName}>
                  {exercise.exerciseName}
                </p>
                <div>
                  <Label className="text-xs">Series</Label>
                  <Input
                    type="number"
                    value={exercise.sets}
                    onChange={(e) => updateExercise(index, { sets: Number(e.target.value) || 0 })}
                    className="mt-1 h-7"
                  />
                </div>
                <div>
                  <Label className="text-xs">Reps min</Label>
                  <Input
                    type="number"
                    value={exercise.repsMin ?? ''}
                    onChange={(e) => updateExercise(index, { repsMin: e.target.value ? Number(e.target.value) : null })}
                    className="mt-1 h-7"
                  />
                </div>
                <div>
                  <Label className="text-xs">Reps max</Label>
                  <Input
                    type="number"
                    value={exercise.repsMax ?? ''}
                    onChange={(e) => updateExercise(index, { repsMax: e.target.value ? Number(e.target.value) : null })}
                    className="mt-1 h-7"
                  />
                </div>
                <Button type="button" variant="ghost" size="icon-sm" aria-label="Quitar ejercicio" onClick={() => removeExercise(index)}>
                  <X className="size-4" />
                </Button>
              </div>
            ))}
            <Button type="button" variant="ghost" className="self-start" onClick={() => setPickerOpen(true)}>
              <Plus /> Agregar ejercicio
            </Button>
            <ExercisePickerDialog
              open={pickerOpen}
              onOpenChange={setPickerOpen}
              onSelect={(exercise) =>
                onChange({
                  ...day,
                  exercises: [...day.exercises, { exerciseId: exercise.id, exerciseName: exercise.name, sets: 3 }],
                })
              }
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
}
