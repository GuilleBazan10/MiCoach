import { useState } from 'react';
import { Activity, Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { ConfirmDeleteDialog } from '@/components/ConfirmDeleteDialog';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { extractErrorMessage } from '@/core/api/apiError';
import { useProgressEntries } from '../application/queries';
import { useDeleteEntry } from '../application/mutations';
import type { ProgressEntry } from '../domain/progressTypes';
import { METRIC_TYPE_LABELS, labelFor } from '../domain/progressLabels';
import { AddEntryDialog } from './AddEntryDialog';

const dateFormatter = new Intl.DateTimeFormat('es-AR', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
});

export function MetricEntriesView() {
  const [filter, setFilter] = useState<string | undefined>(undefined);
  const { data: entries, isLoading, isError, refetch } = useProgressEntries(filter);
  const deleteEntry = useDeleteEntry();
  const [pendingDelete, setPendingDelete] = useState<ProgressEntry | null>(null);

  return (
    <div className="flex flex-col gap-3">
      <div className="flex min-w-0 items-center justify-between gap-3">
        <div className="flex min-w-0 flex-1 gap-2 overflow-x-auto pb-1">
          <FilterChip label="Todas" selected={filter === undefined} onClick={() => setFilter(undefined)} />
          {Object.entries(METRIC_TYPE_LABELS).map(([key, label]) => (
            <FilterChip key={key} label={label} selected={filter === key} onClick={() => setFilter(key)} />
          ))}
        </div>
        <AddEntryDialog
          initialMetricType={filter}
          trigger={
            <Button size="sm" className="shrink-0">
              <Plus /> Registrar
            </Button>
          }
        />
      </div>

      {isLoading && (
        <div className="flex justify-center py-12">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
        </div>
      )}
      {isError && <ErrorState onRetry={() => refetch()} />}
      {!isLoading && entries?.length === 0 && (
        <EmptyState icon={Activity} message="Todavía no registraste ninguna métrica. ¡Sumá la primera!" />
      )}
      <div className="flex flex-col gap-2">
        {entries?.map((entry) => (
          <Card key={entry.id}>
            <CardContent className="flex items-center gap-3 py-3">
              <span className="flex size-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                <Activity className="size-4" />
              </span>
              <div className="min-w-0 flex-1">
                <p className="text-sm font-medium">
                  {labelFor(METRIC_TYPE_LABELS, entry.metricType)}: {entry.value} {entry.unit}
                </p>
                <p className="text-xs text-muted-foreground">{dateFormatter.format(new Date(entry.measuredAt))}</p>
              </div>
              <Button variant="ghost" size="icon-sm" aria-label="Borrar métrica" onClick={() => setPendingDelete(entry)}>
                <Trash2 />
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
      <ConfirmDeleteDialog
        open={pendingDelete != null}
        onOpenChange={(next) => !next && setPendingDelete(null)}
        title="Borrar métrica"
        message={`¿Seguro que querés borrar "${pendingDelete ? `${labelFor(METRIC_TYPE_LABELS, pendingDelete.metricType)}: ${pendingDelete.value} ${pendingDelete.unit}` : ''}"? Esta acción no se puede deshacer.`}
        pending={deleteEntry.isPending}
        onConfirm={() => {
          if (!pendingDelete) return;
          deleteEntry.mutate(pendingDelete.id, {
            onSuccess: () => setPendingDelete(null),
            onError: (error) => toast.error(extractErrorMessage(error)),
          });
        }}
      />
    </div>
  );
}

function FilterChip({ label, selected, onClick }: { label: string; selected: boolean; onClick: () => void }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        'shrink-0 rounded-full border px-3 py-1 text-sm whitespace-nowrap transition-colors',
        selected
          ? 'border-primary bg-primary text-primary-foreground'
          : 'border-border bg-background text-foreground hover:bg-muted',
      )}
    >
      {label}
    </button>
  );
}
