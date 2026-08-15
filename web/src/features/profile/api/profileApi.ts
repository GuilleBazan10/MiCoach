// =====================================================================
// MiCoach — Cliente REST del módulo user (/api/v1/users/me).
// Paridad con mobile/lib/features/profile/infrastructure/profile_api.dart.
// =====================================================================
import { apiClient } from '@/core/api/client';
import type { UserGoal, UserInjury, UserMedication, UserPathology, UserProfile } from '../domain/profileTypes';

export const profileApi = {
  getProfile: () => apiClient.get<UserProfile>('/users/me/profile').then((r) => r.data),
  updateProfile: (profile: UserProfile) =>
    apiClient.put<UserProfile>('/users/me/profile', profile).then((r) => r.data),

  getGoals: () => apiClient.get<UserGoal[]>('/users/me/goals').then((r) => r.data),
  addGoal: (body: {
    goalType: string;
    targetValue?: number | null;
    targetUnit?: string | null;
    targetDate?: string | null;
    priority?: number | null;
  }) => apiClient.post('/users/me/goals', body),
  deleteGoal: (id: number) => apiClient.delete(`/users/me/goals/${id}`),

  getPathologies: () => apiClient.get<UserPathology[]>('/users/me/pathologies').then((r) => r.data),
  addPathology: (body: { pathology: string; notes?: string | null; diagnosedAt?: string | null }) =>
    apiClient.post('/users/me/pathologies', body),
  deletePathology: (id: number) => apiClient.delete(`/users/me/pathologies/${id}`),

  getInjuries: () => apiClient.get<UserInjury[]>('/users/me/injuries').then((r) => r.data),
  addInjury: (body: {
    bodyPart: string;
    injuryType: string;
    status?: string | null;
    notes?: string | null;
    occurredAt?: string | null;
  }) => apiClient.post('/users/me/injuries', body),
  deleteInjury: (id: number) => apiClient.delete(`/users/me/injuries/${id}`),

  getMedications: () => apiClient.get<UserMedication[]>('/users/me/medications').then((r) => r.data),
  addMedication: (body: { medicationName: string; dosage?: string | null; schedule?: string | null; notes?: string | null }) =>
    apiClient.post('/users/me/medications', body),
  deleteMedication: (id: number) => apiClient.delete(`/users/me/medications/${id}`),
};
