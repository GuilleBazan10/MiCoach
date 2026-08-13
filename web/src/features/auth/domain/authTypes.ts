// =====================================================================
// KineticOs — Tipos del dominio auth (docs/03-api-contracts.md § Módulo auth).
// =====================================================================
export interface AuthUser {
  id: number;
  email: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: AuthUser;
}
