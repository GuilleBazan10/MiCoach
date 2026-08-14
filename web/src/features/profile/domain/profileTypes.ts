// =====================================================================
// KineticOs — Perfil de salud (mirror de UserDtos en el backend).
// Paridad con mobile/lib/features/profile/domain/{user_profile,user_subresources}.dart.
// =====================================================================
export interface UserProfile {
  id?: number;
  sex?: string | null;
  birthDate?: string | null;
  heightCm?: number | null;
  weightKg?: number | null;
  activityLevel?: string | null;
  experienceLevel?: string | null;
  equipment: string[];
  trainingDaysPerWeek?: number | null;
  trainingMinutes?: number | null;
  preferredTime?: string | null;
  timezone?: string | null;
  tdeeCalories?: number | null;
  dietaryGoal?: string | null;
  notes?: string | null;
}

export interface UserGoal {
  id: number;
  goalType: string;
  targetValue?: number | null;
  targetUnit?: string | null;
  targetDate?: string | null;
  priority?: number | null;
  active: boolean;
}

export interface UserPathology {
  id: number;
  pathology: string;
  notes?: string | null;
  diagnosedAt?: string | null;
}

export interface UserInjury {
  id: number;
  bodyPart: string;
  injuryType: string;
  status?: string | null;
  notes?: string | null;
  occurredAt?: string | null;
}

export interface UserMedication {
  id: number;
  medicationName: string;
  dosage?: string | null;
  schedule?: string | null;
  notes?: string | null;
  active: boolean;
}
