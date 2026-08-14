// =====================================================================
// KineticOs — Panel admin: qué proveedor de IA usa la app (Ollama local o
// Groq/OpenRouter/Gemini en la nube), con sus API keys y cuál está activo.
// =====================================================================
import { useAiProviders } from '../application/queries';
import { AiProviderCard } from '../components/AiProviderCard';

export function AiProvidersPage() {
  const { data: providers, isLoading, isError } = useAiProviders();

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-4 pb-12">
      <div>
        <h1 className="text-xl font-semibold">Proveedores de IA</h1>
        <p className="text-sm text-muted-foreground">
          Elegí qué proveedor genera rutinas y planes de alimentación. Solo uno puede estar activo a la vez.
        </p>
      </div>

      {isLoading && (
        <div className="flex justify-center py-12">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
        </div>
      )}
      {isError && (
        <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar la configuración.</p>
      )}

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {providers?.map((config) => (
          // key incluye updatedAt a propósito: al guardar/activar remonta la tarjeta
          // para que sus campos reflejen el estado real del servidor (no el que quedó
          // tipeado localmente antes del refetch).
          <AiProviderCard key={`${config.provider}:${config.updatedAt}`} config={config} />
        ))}
      </div>
    </div>
  );
}
