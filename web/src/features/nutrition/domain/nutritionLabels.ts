// Paridad con mobile/lib/features/nutrition/presentation/nutrition_labels.dart.
export const MEAL_CATEGORY_LABELS: Record<string, string> = {
  breakfast: 'Desayuno',
  lunch: 'Almuerzo',
  dinner: 'Cena',
  snack: 'Snack',
  dessert: 'Postre',
  drink: 'Bebida',
};

export const MEAL_TYPE_LABELS: Record<string, string> = {
  breakfast: 'Desayuno',
  lunch: 'Almuerzo',
  dinner: 'Cena',
  snack: 'Snack',
};

export const RECIPE_DIFFICULTY_LABELS: Record<string, string> = {
  easy: 'Fácil',
  medium: 'Media',
  hard: 'Difícil',
};

export const SUBSTITUTION_REASON_LABELS: Record<string, string> = {
  allergy: 'Alergia',
  intolerance: 'Intolerancia',
  unavailable: 'No lo tengo disponible',
  preference: 'Preferencia personal',
};

export function labelFor(labels: Record<string, string>, key?: string | null): string {
  if (!key) return '—';
  return labels[key] ?? key;
}

/** Clases Tailwind (bg/text) por tipo de comida — le da identidad de color a cada franja. */
export const MEAL_TYPE_COLORS: Record<string, string> = {
  breakfast: 'bg-highlight/10 text-highlight',
  lunch: 'bg-primary/10 text-primary',
  dinner: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
  snack: 'bg-accent/10 text-accent',
};

export function colorFor(colors: Record<string, string>, key?: string | null): string {
  return (key && colors[key]) || 'bg-muted text-muted-foreground';
}
