// =====================================================================
// MiCoach — Forma unificada de error de la API (docs/03-api-contracts.md
// § Convenciones ya definidas).
// =====================================================================
import { isAxiosError } from 'axios';

export interface ApiError {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
}

export function isApiError(value: unknown): value is ApiError {
  return (
    typeof value === 'object' &&
    value !== null &&
    'code' in value &&
    'message' in value
  );
}

/**
 * Mensaje para timeouts/errores de red (sin response de la API) — en Render free
 * tier el backend puede estar dormido y tardar en arrancar (cold start), y "Ocurrió
 * un error inesperado" no le da al usuario ninguna pista de qué está pasando.
 */
export const NETWORK_ERROR_MESSAGE =
  'No pudimos conectar con el servidor. Si es la primera vez que abrís la app hoy, puede tardar hasta un minuto en despertar — probá de nuevo en un rato.';

/** Extrae un mensaje legible de un error de Axios contra la API de MiCoach. */
export function extractErrorMessage(error: unknown, fallback = 'Ocurrió un error inesperado.'): string {
  if (isAxiosError(error)) {
    if (isApiError(error.response?.data)) {
      return error.response.data.message;
    }
    if (!error.response) {
      return NETWORK_ERROR_MESSAGE;
    }
  }
  return fallback;
}
