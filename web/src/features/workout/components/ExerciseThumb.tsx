// =====================================================================
// MiCoach — Miniatura de ejercicio: foto real si existe (ver
// docs/00-progress.md § catálogo ampliado con free-exercise-db), si no un
// ícono de color por categoría en vez de un espacio vacío.
// =====================================================================
import { Dumbbell } from 'lucide-react';
import { useState } from 'react';
import { CATEGORY_COLORS, colorFor } from '../domain/workoutLabels';

export function ExerciseThumb({
  imageUrl,
  category,
  size = 'md',
}: {
  imageUrl?: string | null;
  category?: string | null;
  size?: 'sm' | 'md';
}) {
  const [broken, setBroken] = useState(false);
  const dimensions = size === 'sm' ? 'size-9' : 'size-11';

  if (imageUrl && !broken) {
    return (
      <img
        src={imageUrl}
        alt=""
        className={`${dimensions} shrink-0 rounded-lg object-cover ring-1 ring-border`}
        onError={() => setBroken(true)}
      />
    );
  }

  return (
    <span
      className={`flex ${dimensions} shrink-0 items-center justify-center rounded-lg ${colorFor(CATEGORY_COLORS, category)}`}
    >
      <Dumbbell className={size === 'sm' ? 'size-4' : 'size-5'} />
    </span>
  );
}
