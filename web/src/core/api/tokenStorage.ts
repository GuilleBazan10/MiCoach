// =====================================================================
// MiCoach — Almacenamiento de tokens JWT (access + refresh).
// Equivalente a mobile/lib/core/storage/token_storage.dart. localStorage
// (no hay guardado seguro nativo en un SPA); el access token vive 15 min.
// =====================================================================
const ACCESS_TOKEN_KEY = 'micoach_access_token';
const REFRESH_TOKEN_KEY = 'micoach_refresh_token';

export const tokenStorage = {
  readAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  readRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  },

  clear(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  hasSession(): boolean {
    return this.readAccessToken() !== null;
  },
};
