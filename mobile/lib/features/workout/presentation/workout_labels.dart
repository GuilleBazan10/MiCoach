// =====================================================================
// MiCoach — Etiquetas en español para los valores fijos del backend
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

/// "3 series · 8-12 reps" en vez del "3 x 8-12" original, más claro para quien no conoce
/// la jerga. Para ejercicios medidos en tiempo (measurementType 'duration', ej. Plancha)
/// el mismo rango numérico se expresa en segundos, no en repeticiones.
String formatSetsReps({int? sets, int? repsMin, int? repsMax, String? measurementType}) {
  final unit = measurementType == 'duration' ? 'seg' : 'reps';
  final setsText = sets != null ? '$sets series' : null;
  String? amountText;
  if (repsMin != null && repsMax != null) {
    amountText = repsMin == repsMax ? '$repsMin $unit' : '$repsMin-$repsMax $unit';
  }
  final parts = [setsText, amountText].whereType<String>();
  return parts.isEmpty ? 'Sin datos' : parts.join(' · ');
}
