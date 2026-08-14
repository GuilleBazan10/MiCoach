// Paridad con mobile/lib/features/progress/presentation/progress_labels.dart.
export const METRIC_TYPE_LABELS: Record<string, string> = {
  weight: 'Peso',
  bmi: 'IMC',
  body_fat: '% Grasa corporal',
  waist: 'Cintura',
  chest: 'Pecho',
  arm: 'Brazo',
  hip: 'Cadera',
  thigh: 'Muslo',
  neck: 'Cuello',
  calf: 'Pantorrilla',
  water_percent: '% Agua corporal',
  muscle_mass: 'Masa muscular',
  bone_mass: 'Masa ósea',
  visceral_fat: 'Grasa visceral',
  resting_hr: 'Frec. cardíaca en reposo',
};

export const METRIC_TYPE_DEFAULT_UNIT: Record<string, string> = {
  weight: 'kg',
  bmi: 'points',
  body_fat: 'pct',
  waist: 'cm',
  chest: 'cm',
  arm: 'cm',
  hip: 'cm',
  thigh: 'cm',
  neck: 'cm',
  calf: 'cm',
  water_percent: 'pct',
  muscle_mass: 'kg',
  bone_mass: 'kg',
  visceral_fat: 'points',
  resting_hr: 'bpm',
};

export const PHOTO_ANGLE_LABELS: Record<string, string> = {
  front: 'Frente',
  side: 'Perfil',
  back: 'Espalda',
};

export function labelFor(labels: Record<string, string>, key?: string | null): string {
  if (!key) return '—';
  return labels[key] ?? key;
}
