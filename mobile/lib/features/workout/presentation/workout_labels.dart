// =====================================================================
// KineticOs — Etiquetas en español para los valores fijos del backend
// (CHECK constraints documentados en docs/02-database.md).
// =====================================================================
const objectiveLabels = {
  'lose_fat': 'Perder grasa',
  'gain_muscle': 'Ganar músculo',
  'maintain': 'Mantener',
  'endurance': 'Resistencia',
  'strength': 'Fuerza',
  'general_health': 'Salud general',
};

const levelLabels = {'beginner': 'Principiante', 'intermediate': 'Intermedio', 'advanced': 'Avanzado'};

const categoryLabels = {
  'strength': 'Fuerza',
  'cardio': 'Cardio',
  'mobility': 'Movilidad',
  'flexibility': 'Flexibilidad',
  'hiit': 'HIIT',
  'plyometric': 'Pliometría',
};

const difficultyLabels = levelLabels;

String labelFor(Map<String, String> labels, String? key) => key == null ? '—' : (labels[key] ?? key);
