// =====================================================================
// KineticOs — Métrica de seguimiento (mirror de ProgressDtos en backend).
// =====================================================================
class ProgressEntry {
  final int id;
  final String metricType;
  final double value;
  final String unit;
  final DateTime measuredAt;
  final String? notes;

  const ProgressEntry({
    required this.id,
    required this.metricType,
    required this.value,
    required this.unit,
    required this.measuredAt,
    this.notes,
  });

  factory ProgressEntry.fromJson(Map<String, dynamic> json) => ProgressEntry(
        id: json['id'] as int,
        metricType: json['metricType'] as String,
        value: (json['value'] as num).toDouble(),
        unit: json['unit'] as String,
        measuredAt: DateTime.parse(json['measuredAt'] as String),
        notes: json['notes'] as String?,
      );
}
