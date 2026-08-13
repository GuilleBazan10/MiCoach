// =====================================================================
// KineticOs — Foto de progreso (mirror de ProgressDtos en backend).
// =====================================================================
class ProgressPhoto {
  final int id;
  final String photoUrl;
  final String? angle;
  final DateTime takenAt;
  final String? notes;

  const ProgressPhoto({
    required this.id,
    required this.photoUrl,
    this.angle,
    required this.takenAt,
    this.notes,
  });

  factory ProgressPhoto.fromJson(Map<String, dynamic> json) => ProgressPhoto(
        id: json['id'] as int,
        photoUrl: json['photoUrl'] as String,
        angle: json['angle'] as String?,
        takenAt: DateTime.parse(json['takenAt'] as String),
        notes: json['notes'] as String?,
      );
}
