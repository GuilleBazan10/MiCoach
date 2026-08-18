import { useState } from 'react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import {
  ACTIVITY_OPTIONS,
  DIETARY_GOAL_OPTIONS,
  EXPERIENCE_OPTIONS,
  PREFERRED_TIME_OPTIONS,
  SEX_OPTIONS,
} from '../domain/profileLabels';
import type { UserProfile } from '../domain/profileTypes';
import { useProfile } from '../application/useProfile';

function toNumberOrNull(value: string): number | null {
  const trimmed = value.trim().replace(',', '.');
  if (!trimmed) return null;
  const parsed = Number(trimmed);
  return Number.isNaN(parsed) ? null : parsed;
}

function toIntOrNull(value: string): number | null {
  const trimmed = value.trim();
  if (!trimmed) return null;
  const parsed = Number.parseInt(trimmed, 10);
  return Number.isNaN(parsed) ? null : parsed;
}

/** Vacío es válido (campo opcional) — solo bloquea si escribieron algo no numérico. */
function isValidOptionalNumber(value: string): boolean {
  const trimmed = value.trim();
  return trimmed === '' || !Number.isNaN(Number(trimmed.replace(',', '.')));
}

export function ProfileForm({ profile }: { profile: UserProfile }) {
  const { updateProfile } = useProfile();

  const [sex, setSex] = useState(profile.sex ?? undefined);
  const [birthDate, setBirthDate] = useState(profile.birthDate ?? '');
  const [heightCm, setHeightCm] = useState(profile.heightCm?.toString() ?? '');
  const [weightKg, setWeightKg] = useState(profile.weightKg?.toString() ?? '');
  const [activityLevel, setActivityLevel] = useState(profile.activityLevel ?? undefined);
  const [experienceLevel, setExperienceLevel] = useState(profile.experienceLevel ?? undefined);
  const [equipment, setEquipment] = useState(profile.equipment.join(', '));
  const [trainingDaysPerWeek, setTrainingDaysPerWeek] = useState(profile.trainingDaysPerWeek?.toString() ?? '');
  const [trainingMinutes, setTrainingMinutes] = useState(profile.trainingMinutes?.toString() ?? '');
  const [preferredTime, setPreferredTime] = useState(profile.preferredTime ?? undefined);
  const [dietaryGoal, setDietaryGoal] = useState(profile.dietaryGoal ?? undefined);
  const [notes, setNotes] = useState(profile.notes ?? '');

  function handleSave() {
    if (!isValidOptionalNumber(heightCm)) return toast.error('Altura inválida: ingresá solo números.');
    if (!isValidOptionalNumber(weightKg)) return toast.error('Peso inválido: ingresá solo números.');
    if (!isValidOptionalNumber(trainingDaysPerWeek)) return toast.error('Días por semana inválido: ingresá solo números.');
    if (!isValidOptionalNumber(trainingMinutes)) return toast.error('Minutos por sesión inválido: ingresá solo números.');

    const updated: UserProfile = {
      sex: sex ?? null,
      birthDate: birthDate || null,
      heightCm: toNumberOrNull(heightCm),
      weightKg: toNumberOrNull(weightKg),
      activityLevel: activityLevel ?? null,
      experienceLevel: experienceLevel ?? null,
      equipment: equipment
        .split(',')
        .map((e) => e.trim())
        .filter(Boolean),
      trainingDaysPerWeek: toIntOrNull(trainingDaysPerWeek),
      trainingMinutes: toIntOrNull(trainingMinutes),
      preferredTime: preferredTime ?? null,
      timezone: profile.timezone,
      tdeeCalories: profile.tdeeCalories,
      dietaryGoal: dietaryGoal ?? null,
      notes: notes.trim() || null,
    };
    updateProfile.mutate(updated, {
      onSuccess: () => toast.success('Perfil actualizado'),
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-3">
        <h2 className="text-base font-semibold">Datos físicos</h2>
        <OptionSelect label="Sexo" value={sex} onChange={setSex} options={SEX_OPTIONS} />
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="birthDate">Fecha de nacimiento</Label>
          <Input id="birthDate" type="date" value={birthDate} onChange={(e) => setBirthDate(e.target.value)} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="heightCm">Altura (cm)</Label>
            <Input id="heightCm" inputMode="decimal" value={heightCm} onChange={(e) => setHeightCm(e.target.value)} />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="weightKg">Peso (kg)</Label>
            <Input id="weightKg" inputMode="decimal" value={weightKg} onChange={(e) => setWeightKg(e.target.value)} />
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-3">
        <h2 className="text-base font-semibold">Entrenamiento</h2>
        <OptionSelect label="Nivel de actividad" value={activityLevel} onChange={setActivityLevel} options={ACTIVITY_OPTIONS} />
        <OptionSelect
          label="Nivel de experiencia"
          value={experienceLevel}
          onChange={setExperienceLevel}
          options={EXPERIENCE_OPTIONS}
        />
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="equipment">Equipamiento (separado por comas)</Label>
          <Input id="equipment" value={equipment} onChange={(e) => setEquipment(e.target.value)} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="trainingDays">Días/semana</Label>
            <Input
              id="trainingDays"
              inputMode="numeric"
              value={trainingDaysPerWeek}
              onChange={(e) => setTrainingDaysPerWeek(e.target.value)}
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="trainingMinutes">Minutos/sesión</Label>
            <Input
              id="trainingMinutes"
              inputMode="numeric"
              value={trainingMinutes}
              onChange={(e) => setTrainingMinutes(e.target.value)}
            />
          </div>
        </div>
        <OptionSelect
          label="Horario preferido"
          value={preferredTime}
          onChange={setPreferredTime}
          options={PREFERRED_TIME_OPTIONS}
        />
        <OptionSelect label="Objetivo principal" value={dietaryGoal} onChange={setDietaryGoal} options={DIETARY_GOAL_OPTIONS} />
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="notes">Notas</Label>
          <Textarea id="notes" rows={3} value={notes} onChange={(e) => setNotes(e.target.value)} />
        </div>
      </div>

      <Button onClick={handleSave} disabled={updateProfile.isPending} className="self-start">
        {updateProfile.isPending ? 'Guardando…' : 'Guardar perfil'}
      </Button>
    </div>
  );
}
