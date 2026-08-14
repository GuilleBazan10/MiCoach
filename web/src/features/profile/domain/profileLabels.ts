// Paridad con las opciones definidas inline en mobile/lib/features/profile/presentation/*.
export const SEX_OPTIONS: Record<string, string> = { male: 'Masculino', female: 'Femenino', other: 'Otro' };

export const ACTIVITY_OPTIONS: Record<string, string> = {
  sedentary: 'Sedentario',
  light: 'Actividad ligera',
  moderate: 'Actividad moderada',
  active: 'Activo',
  very_active: 'Muy activo',
};

export const EXPERIENCE_OPTIONS: Record<string, string> = {
  beginner: 'Principiante',
  intermediate: 'Intermedio',
  advanced: 'Avanzado',
};

export const PREFERRED_TIME_OPTIONS: Record<string, string> = {
  morning: 'Mañana',
  midday: 'Mediodía',
  afternoon: 'Tarde',
  evening: 'Noche',
};

export const DIETARY_GOAL_OPTIONS: Record<string, string> = {
  lose_fat: 'Perder grasa',
  gain_muscle: 'Ganar músculo',
  maintain: 'Mantener',
  endurance: 'Resistencia',
  health: 'Salud general',
};

export const GOAL_TYPE_OPTIONS: Record<string, string> = {
  lose_fat: 'Perder grasa',
  gain_muscle: 'Ganar músculo',
  maintain_weight: 'Mantener peso',
  endurance: 'Resistencia',
  strength: 'Fuerza',
  flexibility: 'Flexibilidad',
  general_health: 'Salud general',
};

export const INJURY_STATUS_OPTIONS: Record<string, string> = {
  active: 'Activa',
  recovered: 'Recuperada',
  chronic: 'Crónica',
};
