// =====================================================================
// MiCoach — Etiquetas y unidades por defecto para las métricas de
// seguimiento (CHECK constraints documentados en docs/02-database.md).
// =====================================================================
const metricTypeLabels = {
  'weight': 'Peso',
  'bmi': 'IMC',
  'body_fat': '% Grasa corporal',
  'waist': 'Cintura',
  'chest': 'Pecho',
  'arm': 'Brazo',
  'hip': 'Cadera',
  'thigh': 'Muslo',
  'neck': 'Cuello',
  'calf': 'Pantorrilla',
  'water_percent': '% Agua corporal',
  'muscle_mass': 'Masa muscular',
  'bone_mass': 'Masa ósea',
  'visceral_fat': 'Grasa visceral',
  'resting_hr': 'Frec. cardíaca en reposo',
};

const metricTypeDefaultUnit = {
  'weight': 'kg',
  'bmi': 'points',
  'body_fat': 'pct',
  'waist': 'cm',
  'chest': 'cm',
  'arm': 'cm',
  'hip': 'cm',
  'thigh': 'cm',
  'neck': 'cm',
  'calf': 'cm',
  'water_percent': 'pct',
  'muscle_mass': 'kg',
  'bone_mass': 'kg',
  'visceral_fat': 'points',
  'resting_hr': 'bpm',
};

const photoAngleLabels = {'front': 'Frente', 'side': 'Perfil', 'back': 'Espalda'};

String labelFor(Map<String, String> labels, String? key) => key == null ? '—' : (labels[key] ?? key);
