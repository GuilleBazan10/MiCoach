// =====================================================================
// MiCoach — Estado vacío reutilizable: ícono en círculo de color +
// mensaje, en vez de un texto gris centrado sin nada más.
// =====================================================================
import type { ComponentType } from 'react';

export function EmptyState({
  icon: Icon,
  message,
}: {
  icon: ComponentType<{ className?: string }>;
  message: string;
}) {
  return (
    <div className="flex flex-col items-center gap-3 py-14 text-center">
      <div className="flex size-14 items-center justify-center rounded-full bg-primary/10">
        <Icon className="size-6 text-primary" />
      </div>
      <p className="max-w-xs text-sm text-muted-foreground">{message}</p>
    </div>
  );
}
