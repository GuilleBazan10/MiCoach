// =====================================================================
// MiCoach — Miniatura de receta: foto real si existe, si no un ícono de
// color por categoría de comida. Mismo tratamiento que ExerciseThumb
// (docs/10-recomendaciones-coach-nutricion.md § H.4 — nivela el módulo de
// nutrición, que hoy es más pobre visualmente que el de entrenamiento).
// =====================================================================
import { UtensilsCrossed } from 'lucide-react';
import { MEAL_TYPE_COLORS, colorFor } from '../domain/nutritionLabels';

export function RecipeThumb({
  imageUrl,
  mealType,
  size = 'md',
}: {
  imageUrl?: string | null;
  mealType?: string | null;
  size?: 'sm' | 'md';
}) {
  const dimensions = size === 'sm' ? 'size-9' : 'size-11';

  if (imageUrl) {
    return (
      <img
        src={imageUrl}
        alt=""
        className={`${dimensions} shrink-0 rounded-lg object-cover ring-1 ring-border`}
      />
    );
  }

  return (
    <span
      className={`flex ${dimensions} shrink-0 items-center justify-center rounded-lg ${colorFor(MEAL_TYPE_COLORS, mealType)}`}
    >
      <UtensilsCrossed className={size === 'sm' ? 'size-4' : 'size-5'} />
    </span>
  );
}
