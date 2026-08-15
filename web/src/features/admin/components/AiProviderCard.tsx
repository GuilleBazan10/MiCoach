// =====================================================================
// MiCoach — Tarjeta de un proveedor de IA en el panel admin: editar
// baseUrl/modelo/API key, habilitar, probar conexión y activarlo.
// =====================================================================
import { useState } from 'react';
import { Loader2, Zap } from 'lucide-react';
import { toast } from 'sonner';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { extractErrorMessage } from '@/core/api/apiError';
import { useActivateAiProvider, useTestAiProvider, useUpdateAiProvider } from '../application/mutations';
import type { AiProviderConfig } from '../domain/aiProviderTypes';

export function AiProviderCard({ config }: { config: AiProviderConfig }) {
  const [displayName, setDisplayName] = useState(config.displayName);
  const [baseUrl, setBaseUrl] = useState(config.baseUrl ?? '');
  const [model, setModel] = useState(config.model);
  const [apiKey, setApiKey] = useState('');
  const [enabled, setEnabled] = useState(config.enabled);

  const updateProvider = useUpdateAiProvider(config.provider);
  const activateProvider = useActivateAiProvider();
  const testProvider = useTestAiProvider();

  const dirty =
    displayName !== config.displayName ||
    baseUrl !== (config.baseUrl ?? '') ||
    model !== config.model ||
    apiKey !== '' ||
    enabled !== config.enabled;

  function handleSave() {
    updateProvider.mutate(
      { displayName, baseUrl: baseUrl || null, model, apiKey: apiKey || undefined, enabled },
      {
        onSuccess: () => {
          setApiKey('');
          toast.success(`${displayName}: configuración guardada.`);
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  function handleTest() {
    testProvider.mutate(config.provider, {
      onSuccess: (result) => {
        if (result.ok) {
          toast.success(`${displayName} respondió: "${result.message}"`);
        } else {
          toast.error(`${displayName} falló: ${result.message}`);
        }
      },
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  function handleActivate() {
    activateProvider.mutate(config.provider, {
      onSuccess: () => toast.success(`${displayName} es ahora el proveedor activo.`),
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  return (
    <Card>
      <CardHeader className="flex items-center justify-between gap-2">
        <CardTitle className="text-base">{config.displayName}</CardTitle>
        <div className="flex items-center gap-2">
          {config.active && <Badge>Activo</Badge>}
          {!config.enabled && <Badge variant="outline">Deshabilitado</Badge>}
        </div>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <div className="flex items-center justify-between gap-3">
          <Label htmlFor={`${config.provider}-enabled`} className="text-sm font-normal">
            Habilitado
          </Label>
          <Switch id={`${config.provider}-enabled`} checked={enabled} onCheckedChange={setEnabled} />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor={`${config.provider}-name`}>Nombre</Label>
          <Input id={`${config.provider}-name`} value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor={`${config.provider}-model`}>Modelo</Label>
          <Input id={`${config.provider}-model`} value={model} onChange={(e) => setModel(e.target.value)} />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor={`${config.provider}-base-url`}>Base URL</Label>
          <Input
            id={`${config.provider}-base-url`}
            value={baseUrl}
            onChange={(e) => setBaseUrl(e.target.value)}
            placeholder={config.provider === 'gemini' ? 'No aplica' : 'https://...'}
          />
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor={`${config.provider}-api-key`}>API key</Label>
          <Input
            id={`${config.provider}-api-key`}
            type="password"
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
            placeholder={config.hasApiKey ? 'Configurada — dejar vacío para no cambiarla' : 'Sin configurar'}
          />
        </div>

        <div className="flex flex-wrap items-center gap-2 pt-1">
          <Button size="sm" onClick={handleSave} disabled={!dirty || updateProvider.isPending}>
            {updateProvider.isPending && <Loader2 className="animate-spin" />}
            Guardar
          </Button>
          <Button size="sm" variant="outline" onClick={handleTest} disabled={testProvider.isPending}>
            {testProvider.isPending ? <Loader2 className="animate-spin" /> : <Zap />}
            Probar conexión
          </Button>
          <Button
            size="sm"
            variant="secondary"
            onClick={handleActivate}
            disabled={config.active || activateProvider.isPending}
          >
            {config.active ? 'Es el activo' : 'Activar'}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
