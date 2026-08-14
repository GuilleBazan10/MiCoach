import { CalendarRange, ChevronRight, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { Card } from '@/components/ui/card';
import { useMealPlanList } from '../application/queries';

const shortDate = new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: '2-digit' });

export function MealPlanListView() {
  const { data: plans, isLoading, isError } = useMealPlanList();

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudieron cargar los planes.</p>;
  }
  if (!plans || plans.length === 0) {
    return (
      <EmptyState
        icon={CalendarRange}
        message="Todavía no creaste ningún plan de alimentación. ¡Arrancá con uno nuevo o generá uno con IA!"
      />
    );
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {plans.map((plan) => (
        <Link key={plan.id} to={`/nutrition/plans/${plan.id}`}>
          <Card className="flex-row items-center gap-3 px-4 py-3 transition-all hover:-translate-y-0.5 hover:shadow-md">
            <span className="flex size-10 shrink-0 items-center justify-center rounded-full bg-accent/10 text-accent">
              <CalendarRange className="size-4.5" />
            </span>
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-1.5">
                <p className="truncate font-medium">{plan.name}</p>
                {plan.aiGenerated && <Sparkles className="size-3.5 shrink-0 text-highlight" />}
              </div>
              <p className="truncate text-sm text-muted-foreground">
                {shortDate.format(new Date(plan.startDate))} — {shortDate.format(new Date(plan.endDate))} ·{' '}
                {plan.days.length} días
              </p>
            </div>
            <ChevronRight className="shrink-0 text-muted-foreground" />
          </Card>
        </Link>
      ))}
    </div>
  );
}
