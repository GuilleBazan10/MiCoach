import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { extractErrorMessage } from '@/core/api/apiError';
import { useUnsavedChangesGuard } from '@/core/hooks/useUnsavedChangesGuard';
import type { MealPlanDraft } from '../domain/nutritionTypes';
import { useCreateMealPlan, useUpdateMealPlan } from '../application/mutations';
import { MealPlanDayEditor } from './MealPlanDayEditor';

export function MealPlanForm({ initialDraft, mealPlanId }: { initialDraft: MealPlanDraft; mealPlanId?: number }) {
  const navigate = useNavigate();
  const isEditing = mealPlanId != null;
  const [draft, setDraft] = useState(initialDraft);
  const [saved, setSaved] = useState(false);

  const createPlan = useCreateMealPlan();
  const updatePlan = useUpdateMealPlan(mealPlanId ?? -1);
  const saving = createPlan.isPending || updatePlan.isPending;
  useUnsavedChangesGuard(!saved && JSON.stringify(draft) !== JSON.stringify(initialDraft));

  function addDay() {
    setDraft((d) => ({ ...d, days: [...d.days, { planDate: d.startDate, meals: [] }] }));
  }

  function updateDay(index: number, day: (typeof draft.days)[number]) {
    setDraft((d) => ({ ...d, days: d.days.map((existing, i) => (i === index ? day : existing)) }));
  }

  function removeDay(index: number) {
    setDraft((d) => ({ ...d, days: d.days.filter((_, i) => i !== index) }));
  }

  function handleSave() {
    if (!draft.name.trim()) {
      toast.error('El nombre es obligatorio');
      return;
    }
    const mutation = isEditing ? updatePlan : createPlan;
    mutation.mutate(draft, {
      onSuccess: (result) => {
        setSaved(true);
        navigate(`/nutrition/plans/${result.id}`);
      },
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-4 pb-12">
      <h1 className="text-xl font-semibold">{isEditing ? 'Editar plan' : 'Nuevo plan'}</h1>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="plan-name">Nombre *</Label>
        <Input id="plan-name" value={draft.name} onChange={(e) => setDraft({ ...draft, name: e.target.value })} />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="plan-description">Descripción</Label>
        <Textarea
          id="plan-description"
          rows={2}
          value={draft.description ?? ''}
          onChange={(e) => setDraft({ ...draft, description: e.target.value })}
        />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="plan-start">Desde</Label>
          <Input
            id="plan-start"
            type="date"
            value={draft.startDate}
            onChange={(e) => setDraft({ ...draft, startDate: e.target.value })}
          />
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="plan-end">Hasta</Label>
          <Input
            id="plan-end"
            type="date"
            value={draft.endDate}
            onChange={(e) => setDraft({ ...draft, endDate: e.target.value })}
          />
        </div>
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="plan-calories">Calorías objetivo (opcional)</Label>
        <Input
          id="plan-calories"
          inputMode="numeric"
          value={draft.targetCalories?.toString() ?? ''}
          onChange={(e) => {
            const raw = e.target.value.trim();
            const parsed = raw === '' ? null : Number.parseInt(raw, 10);
            if (raw !== '' && Number.isNaN(parsed)) return;
            setDraft({ ...draft, targetCalories: parsed });
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
          <MealPlanDayEditor
            key={index}
            day={day}
            onChange={(updated) => updateDay(index, updated)}
            onRemove={() => removeDay(index)}
          />
        ))}
      </div>

      <Button onClick={handleSave} disabled={saving} className="self-start">
        {saving ? 'Guardando…' : isEditing ? 'Guardar cambios' : 'Crear plan'}
      </Button>
    </div>
  );
}
