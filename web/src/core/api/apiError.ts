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

/** Extrae un mensaje legible de un error de Axios contra la API de MiCoach. */
export function extractErrorMessage(error: unknown, fallback = 'Ocurrió un error inesperado.'): string {
  if (isAxiosError(error) && isApiError(error.response?.data)) {
    return error.response.data.message;
  }
  return fallback;
}
