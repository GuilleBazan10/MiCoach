// =====================================================================
// MiCoach — Cliente HTTP (Axios) centralizado.
// Equivalente a mobile/lib/core/network/api_client.dart:
// - Adjunta el JWT de acceso a cada request (salvo endpoints públicos de auth).
// - Si una respuesta es 401, intenta refrescar el token una vez y reintenta.
// - Si el refresh falla, limpia la sesión y notifica via onSessionExpired
//   (lo asigna el provider de auth para poder redirigir a login).
// - Agrega X-Correlation-Id para trazabilidad (docs/03-api-contracts.md).
// =====================================================================
import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import { tokenStorage } from './tokenStorage';

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';

const PUBLIC_AUTH_PATHS = ['/auth/register', '/auth/login', '/auth/refresh'];

function isPublicAuthPath(path: string): boolean {
  return PUBLIC_AUTH_PATHS.some((p) => path.endsWith(p));
}

function newCorrelationId(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(16));
  return Array.from(bytes, (b) => b.toString(16).padStart(2, '0')).join('');
}

/** Se asigna desde el provider de auth para reaccionar cuando la sesión expira. */
let onSessionExpired: (() => void) | null = null;
export function setOnSessionExpired(handler: () => void): void {
  onSessionExpired = handler;
}

// 60s: en Render free tier, el backend puede estar dormido y tardar en
// arrancar (cold start) más que un timeout corto habitual.
export const apiClient: AxiosInstance = axios.create({
  baseURL: apiBaseUrl,
  timeout: 60000,
});

const refreshClient = axios.create({ baseURL: apiBaseUrl, timeout: 60000 });

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  config.headers.set('X-Correlation-Id', newCorrelationId());
  if (!isPublicAuthPath(config.url ?? '')) {
    const token = tokenStorage.readAccessToken();
    if (token) {
      config.headers.set('Authorization', `Bearer ${token}`);
    }
  }
  return config;
});

let refreshPromise: Promise<string | null> | null = null;

async function tryRefresh(): Promise<string | null> {
  const refreshToken = tokenStorage.readRefreshToken();
  if (!refreshToken) return null;
  try {
    const response = await refreshClient.post('/auth/refresh', { refreshToken });
    const { accessToken, refreshToken: newRefreshToken } = response.data;
    tokenStorage.saveTokens(accessToken, newRefreshToken);
    return accessToken;
  } catch {
    return null;
  }
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as (InternalAxiosRequestConfig & { _retried?: boolean }) | undefined;
    const isUnauthorized = error.response?.status === 401;

    if (isUnauthorized && originalRequest && !isPublicAuthPath(originalRequest.url ?? '') && !originalRequest._retried) {
      originalRequest._retried = true;
      refreshPromise ??= tryRefresh().finally(() => {
        refreshPromise = null;
      });
      const refreshedToken = await refreshPromise;

      if (refreshedToken) {
        originalRequest.headers.set('Authorization', `Bearer ${refreshedToken}`);
        return apiClient(originalRequest);
      }

      tokenStorage.clear();
      onSessionExpired?.();
    }

    return Promise.reject(error);
  },
);
