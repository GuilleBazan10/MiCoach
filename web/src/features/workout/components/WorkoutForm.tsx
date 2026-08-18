import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import { useUnsavedChangesGuard } from '@/core/hooks/useUnsavedChangesGuard';
import { OBJECTIVE_LABELS, LEVEL_LABELS } from '../domain/workoutLabels';
import type { WorkoutDraft } from '../domain/workoutTypes';
import { useCreateWorkout, useUpdateWorkout } from '../application/mutations';
import { WorkoutDayEditor } from './WorkoutDayEditor';

export function WorkoutForm({ initialDraft, workoutId }: { initialDraft: WorkoutDraft; workoutId?: number }) {
  const navigate = useNavigate();
  const isEditing = workoutId != null;
  const [draft, setDraft] = useState(initialDraft);
  const [saved, setSaved] = useState(false);

  const createWorkout = useCreateWorkout();
  const updateWorkout = useUpdateWorkout(workoutId ?? -1);
  const saving = createWorkout.isPending || updateWorkout.isPending;
  useUnsavedChangesGuard(!saved && JSON.stringify(draft) !== JSON.stringify(initialDraft));

  function addDay() {
    setDraft((d) => ({ ...d, days: [...d.days, { dayIndex: d.days.length + 1, restDay: false, exercises: [] }] }));
  }

  function removeDay(index: number) {
    setDraft((d) => ({
      ...d,
      days: d.days.filter((_, i) => i !== index).map((day, i) => ({ ...day, dayIndex: i + 1 })),
    }));
  }

  function updateDay(index: number, day: typeof draft.days[number]) {
    setDraft((d) => ({ ...d, days: d.days.map((existing, i) => (i === index ? day : existing)) }));
  }

  function handleSave() {
    if (!draft.name.trim()) {
      toast.error('El nombre es obligatorio');
      return;
    }
    const mutation = isEditing ? updateWorkout : createWorkout;
    mutation.mutate(draft, {
      onSuccess: (result) => {
        setSaved(true);
        navigate(`/workouts/${result.id}`);
      },
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-4 pb-12">
      <h1 className="text-xl font-semibold">{isEditing ? 'Editar rutina' : 'Nueva rutina'}</h1>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="workout-name">Nombre *</Label>
        <Input id="workout-name" value={draft.name} onChange={(e) => setDraft({ ...draft, name: e.target.value })} />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="workout-description">Descripción</Label>
        <Textarea
          id="workout-description"
          rows={2}
          value={draft.description ?? ''}
          onChange={(e) => setDraft({ ...draft, description: e.target.value })}
        />
      </div>
      <OptionSelect
        label="Objetivo"
        value={draft.objective}
        onChange={(v) => setDraft({ ...draft, objective: v })}
        options={OBJECTIVE_LABELS}
      />
      <OptionSelect
        label="Nivel"
        value={draft.level}
        onChange={(v) => setDraft({ ...draft, level: v })}
        options={LEVEL_LABELS}
      />
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="workout-duration">Duración (semanas)</Label>
        <Input
          id="workout-duration"
          inputMode="numeric"
          value={draft.durationWeeks?.toString() ?? ''}
          onChange={(e) => {
            const raw = e.target.value.trim();
            const parsed = raw === '' ? null : Number.parseInt(raw, 10);
            if (raw !== '' && Number.isNaN(parsed)) return;
            setDraft({ ...draft, durationWeeks: parsed });
          }}
        />
      </div>

      <div className="mt-2 flex items-center justify-between">
        <h2 className="text-base font-semibold">Días</h2>
        <Button type="button" variant="ghost" onClick={addDay}>
          <Plus /> Agregar día
        </Button>
      </div>
      <div className="flex flex-col gap-3">
        {draft.days.map((day, index) => (
          <WorkoutDayEditor
            key={index}
            day={day}
            onChange={(updated) => updateDay(index, updated)}
            onRemove={() => removeDay(index)}
          />
        ))}
      </div>

      <Button onClick={handleSave} disabled={saving} className="self-start">
        {saving ? 'Guardando…' : isEditing ? 'Guardar cambios' : 'Crear rutina'}
      </Button>
    </div>
  );
}
