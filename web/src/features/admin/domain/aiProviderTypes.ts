// =====================================================================
// MiCoach — Tipos del panel admin de proveedores de IA
// (docs/03-api-contracts.md § Módulo ai, /admin/ai/providers).
// =====================================================================
export interface AiProviderConfig {
  id: number;
  provider: string;
  displayName: string;
  baseUrl: string | null;
  model: string;
  hasApiKey: boolean;
  enabled: boolean;
  active: boolean;
  updatedAt: string;
}

export interface AiProviderConfigDraft {
  displayName: string;
  baseUrl: string | null;
  model: string;
  apiKey?: string;
  enabled: boolean;
}

export interface AiProviderTestResult {
  ok: boolean;
  message: string;
}
