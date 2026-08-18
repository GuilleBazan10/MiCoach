import { ChevronRight, Dumbbell, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { useProfile } from '@/features/profile/application/useProfile';
import { useWorkoutList } from '../application/queries';
import { OBJECTIVE_LABELS, LEVEL_LABELS, colorFor, labelFor, OBJECTIVE_COLORS } from '../domain/workoutLabels';

export function WorkoutListView({ templates }: { templates: boolean }) {
  const { data: workouts, isLoading, isError, refetch } = useWorkoutList(templates);
  const { data: profileData } = useProfile();

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

  if (!workouts || workouts.length === 0) {
    const profile = profileData?.profile;
    const profileIncomplete = !templates && profile != null && (!profile.experienceLevel || !profile.equipment?.length);

    if (profileIncomplete) {
      return (
        <EmptyState
          icon={Dumbbell}
          message="Completá tu nivel de experiencia y equipamiento disponible en tu perfil para que la IA te genere rutinas realmente personalizadas."
          action={
            <Button asChild size="sm">
              <Link to="/profile">Completar perfil</Link>
            </Button>
          }
        />
      );
    }

    return (
      <EmptyState
        icon={Dumbbell}
        message={templates ? 'Todavía no hay plantillas disponibles.' : 'Todavía no creaste ninguna rutina. ¡Arrancá con una nueva o generá una con IA!'}
      />
    );
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {workouts.map((workout) => (
        <Link key={workout.id} to={`/workouts/${workout.id}`}>
          <Card className="flex-row items-center gap-3 px-4 py-3 transition-all hover:-translate-y-0.5 hover:shadow-md">
            <span
              className={`flex size-10 shrink-0 items-center justify-center rounded-full ${colorFor(OBJECTIVE_COLORS, workout.objective)}`}
            >
              <Dumbbell className="size-4.5" />
            </span>
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-1.5">
                <p className="truncate font-medium">{workout.name}</p>
                {workout.aiGenerated && <Sparkles className="size-3.5 shrink-0 text-highlight" />}
              </div>
              <p className="truncate text-sm text-muted-foreground">
                {[
                  workout.objective ? labelFor(OBJECTIVE_LABELS, workout.objective) : null,
                  workout.level ? labelFor(LEVEL_LABELS, workout.level) : null,
                  `${workout.days.length} días`,
                ]
                  .filter(Boolean)
                  .join(' · ')}
              </p>
            </div>
            <ChevronRight className="shrink-0 text-muted-foreground" />
          </Card>
        </Link>
      ))}
    </div>
  );
}
