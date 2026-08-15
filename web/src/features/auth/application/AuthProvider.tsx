// =====================================================================
// MiCoach — Estado de sesión global (Context + reducer).
// Equivalente a mobile/lib/features/auth/application/{auth_providers,auth_state}.dart:
// - status 'unknown' mientras se restaura la sesión guardada (splash).
// - login/register guardan tokens y pasan a 'authenticated'.
// - logout / expiración de sesión (ver core/api/client.ts onSessionExpired)
//   pasan a 'unauthenticated'.
// =====================================================================
import { useEffect, useMemo, useReducer } from 'react';
import type { ReactNode } from 'react';
import { setOnSessionExpired } from '@/core/api/client';
import { tokenStorage } from '@/core/api/tokenStorage';
import { authApi } from '../api/authApi';
import type { AuthUser } from '../domain/authTypes';
import { AuthContext, type AuthContextValue } from './authContext';

interface AuthState {
  status: AuthContextValue['status'];
  user: AuthUser | null;
}

type AuthAction = { type: 'RESOLVED'; user: AuthUser | null };

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'RESOLVED':
      return { status: action.user ? 'authenticated' : 'unauthenticated', user: action.user };
    default:
      return state;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(authReducer, { status: 'unknown', user: null });

  useEffect(() => {
    setOnSessionExpired(() => {
      tokenStorage.clear();
      dispatch({ type: 'RESOLVED', user: null });
    });
  }, []);

  useEffect(() => {
    if (!tokenStorage.hasSession()) {
      dispatch({ type: 'RESOLVED', user: null });
      return;
    }
    authApi
      .me()
      .then((user) => dispatch({ type: 'RESOLVED', user }))
      .catch(() => {
        tokenStorage.clear();
        dispatch({ type: 'RESOLVED', user: null });
      });
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      ...state,
      async login(email, password) {
        const response = await authApi.login(email, password);
        tokenStorage.saveTokens(response.accessToken, response.refreshToken);
        dispatch({ type: 'RESOLVED', user: response.user });
      },
      async register(email, password) {
        const response = await authApi.register(email, password);
        tokenStorage.saveTokens(response.accessToken, response.refreshToken);
        dispatch({ type: 'RESOLVED', user: response.user });
      },
      logout() {
        tokenStorage.clear();
        dispatch({ type: 'RESOLVED', user: null });
      },
    }),
    [state],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
