// =====================================================================
// MiCoach — Estado de error reutilizable para fallos de carga (red/timeout),
// distinto de EmptyState ("no hay datos"). docs/06-ux-ui-audit.md §3.2.
// =====================================================================
import { RefreshCw, WifiOff } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { NETWORK_ERROR_MESSAGE } from '@/core/api/apiError';

export function ErrorState({
  message = NETWORK_ERROR_MESSAGE,
  onRetry,
}: {
  message?: string;
  onRetry?: () => void;
}) {
  return (
    <div className="flex flex-col items-center gap-3 py-14 text-center">
      <div className="flex size-14 items-center justify-center rounded-full bg-destructive/10">
        <WifiOff className="size-6 text-destructive" />
      </div>
      <p className="max-w-xs text-sm text-muted-foreground">{message}</p>
      {onRetry && (
        <Button variant="outline" size="sm" onClick={onRetry}>
          <RefreshCw /> Reintentar
        </Button>
      )}
    </div>
  );
}
