import { useParams } from 'react-router-dom';
import { useMealPlanDetail } from '../application/queries';
import { draftFromMealPlan, newMealPlanDraft } from '../domain/nutritionTypes';
import { MealPlanForm } from '../components/MealPlanForm';

export function MealPlanFormPage() {
  const params = useParams();
  const mealPlanId = params.id ? Number(params.id) : undefined;

  if (mealPlanId == null) {
    return <MealPlanForm initialDraft={newMealPlanDraft()} />;
  }
  return <EditMealPlanForm mealPlanId={mealPlanId} />;
}

function EditMealPlanForm({ mealPlanId }: { mealPlanId: number }) {
  const { data, isLoading, isError } = useMealPlanDetail(mealPlanId);

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError || !data) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar el plan.</p>;
  }
  return <MealPlanForm initialDraft={draftFromMealPlan(data)} mealPlanId={mealPlanId} />;
}
