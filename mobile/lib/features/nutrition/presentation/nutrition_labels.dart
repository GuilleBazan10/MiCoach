// =====================================================================
// MiCoach — Etiquetas en español para los valores fijos del backend
// (CHECK constraints documentados en docs/02-database.md).
// =====================================================================
const mealCategoryLabels = {
  'breakfast': 'Desayuno',
  'lunch': 'Almuerzo',
  'dinner': 'Cena',
  'snack': 'Snack',
  'dessert': 'Postre',
  'drink': 'Bebida',
};

const mealTypeLabels = {
  'breakfast': 'Desayuno',
  'lunch': 'Almuerzo',
  'dinner': 'Cena',
  'snack': 'Snack',
};

const recipeDifficultyLabels = {'easy': 'Fácil', 'medium': 'Media', 'hard': 'Difícil'};

String labelFor(Map<String, String> labels, String? key) => key == null ? '—' : (labels[key] ?? key);
