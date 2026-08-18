import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import { ErrorState } from '@/components/ErrorState';
import { Card } from '@/components/ui/card';
import { useSessionList } from '../application/queries';
import { SESSION_STATUS_LABELS, labelFor } from '../domain/workoutLabels';

const dateFormatter = new Intl.DateTimeFormat('es-AR', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
});

export function SessionHistoryList() {
  const { data: sessions, isLoading, isError, refetch } = useSessionList();

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }

  if (isError) {
    return <ErrorState onRetry={() => refetch()} />;
  }

  if (!sessions || sessions.length === 0) {
    return <p className="py-12 text-center text-sm text-muted-foreground">Todavía no registraste ninguna sesión.</p>;
  }

  return (
    <div className="flex flex-col gap-2">
      {sessions.map((session) => (
        <Link key={session.id} to={`/sessions/${session.id}`}>
          <Card className="flex-row items-center justify-between px-4 py-3 transition-colors hover:bg-muted/50">
            <div>
              <p className="font-medium">
                {session.startedAt ? dateFormatter.format(new Date(session.startedAt)) : `Sesión #${session.id}`}
              </p>
              <p className="text-sm text-muted-foreground">{labelFor(SESSION_STATUS_LABELS, session.status)}</p>
            </div>
            <ChevronRight className="text-muted-foreground" />
          </Card>
        </Link>
      ))}
    </div>
  );
}
