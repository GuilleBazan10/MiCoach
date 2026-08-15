// =====================================================================
// MiCoach — Llamadas HTTP del módulo auth (/api/v1/auth).
// =====================================================================
import { apiClient } from '@/core/api/client';
import type { AuthResponse, AuthUser } from '../domain/authTypes';

export const authApi = {
  register(email: string, password: string) {
    return apiClient.post<AuthResponse>('/auth/register', { email, password }).then((r) => r.data);
  },

  login(email: string, password: string) {
    return apiClient.post<AuthResponse>('/auth/login', { email, password }).then((r) => r.data);
  },

  me() {
    return apiClient.get<AuthUser>('/auth/me').then((r) => r.data);
  },
};
