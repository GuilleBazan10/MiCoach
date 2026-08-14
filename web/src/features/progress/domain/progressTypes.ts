// =====================================================================
// KineticOs — Dominio del módulo progress (mirror de ProgressDtos backend).
// Paridad con mobile/lib/features/progress/domain/*.dart.
// =====================================================================
export interface ProgressEntry {
  id: number;
  metricType: string;
  value: number;
  unit: string;
  measuredAt: string;
  notes?: string | null;
}

export interface ProgressPhoto {
  id: number;
  photoUrl: string;
  angle?: string | null;
  takenAt: string;
  notes?: string | null;
}
