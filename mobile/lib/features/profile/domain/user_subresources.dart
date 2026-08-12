// =====================================================================
// KineticOs — Sub-recursos del perfil de salud (mirror de UserDtos en backend).
// =====================================================================

class UserGoal {
  final int id;
  final String goalType;
  final double? targetValue;
  final String? targetUnit;
  final DateTime? targetDate;
  final int? priority;
  final bool active;

  const UserGoal({
    required this.id,
    required this.goalType,
    this.targetValue,
    this.targetUnit,
    this.targetDate,
    this.priority,
    this.active = true,
  });

  factory UserGoal.fromJson(Map<String, dynamic> json) => UserGoal(
        id: json['id'] as int,
        goalType: json['goalType'] as String,
        targetValue: (json['targetValue'] as num?)?.toDouble(),
        targetUnit: json['targetUnit'] as String?,
        targetDate: json['targetDate'] != null ? DateTime.parse(json['targetDate'] as String) : null,
        priority: json['priority'] as int?,
        active: json['active'] as bool? ?? true,
      );
}

class UserPathology {
  final int id;
  final String pathology;
  final String? notes;
  final DateTime? diagnosedAt;

  const UserPathology({required this.id, required this.pathology, this.notes, this.diagnosedAt});

  factory UserPathology.fromJson(Map<String, dynamic> json) => UserPathology(
        id: json['id'] as int,
        pathology: json['pathology'] as String,
        notes: json['notes'] as String?,
        diagnosedAt: json['diagnosedAt'] != null ? DateTime.parse(json['diagnosedAt'] as String) : null,
      );
}

class UserInjury {
  final int id;
  final String bodyPart;
  final String injuryType;
  final String? status;
  final String? notes;
  final DateTime? occurredAt;

  const UserInjury({
    required this.id,
    required this.bodyPart,
    required this.injuryType,
    this.status,
    this.notes,
    this.occurredAt,
  });

  factory UserInjury.fromJson(Map<String, dynamic> json) => UserInjury(
        id: json['id'] as int,
        bodyPart: json['bodyPart'] as String,
        injuryType: json['injuryType'] as String,
        status: json['status'] as String?,
        notes: json['notes'] as String?,
        occurredAt: json['occurredAt'] != null ? DateTime.parse(json['occurredAt'] as String) : null,
      );
}

class UserMedication {
  final int id;
  final String medicationName;
  final String? dosage;
  final String? schedule;
  final String? notes;
  final bool active;

  const UserMedication({
    required this.id,
    required this.medicationName,
    this.dosage,
    this.schedule,
    this.notes,
    this.active = true,
  });

  factory UserMedication.fromJson(Map<String, dynamic> json) => UserMedication(
        id: json['id'] as int,
        medicationName: json['medicationName'] as String,
        dosage: json['dosage'] as String?,
        schedule: json['schedule'] as String?,
        notes: json['notes'] as String?,
        active: json['active'] as bool? ?? true,
      );
}
