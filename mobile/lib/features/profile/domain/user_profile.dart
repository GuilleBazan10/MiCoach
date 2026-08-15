// =====================================================================
// MiCoach — Perfil de salud (mirror de UserDtos.ProfileResponse en backend).
// =====================================================================
class UserProfile {
  final int? id;
  final String? sex;
  final DateTime? birthDate;
  final double? heightCm;
  final double? weightKg;
  final String? activityLevel;
  final String? experienceLevel;
  final List<String> equipment;
  final int? trainingDaysPerWeek;
  final int? trainingMinutes;
  final String? preferredTime;
  final String? timezone;
  final int? tdeeCalories;
  final String? dietaryGoal;
  final String? notes;

  const UserProfile({
    this.id,
    this.sex,
    this.birthDate,
    this.heightCm,
    this.weightKg,
    this.activityLevel,
    this.experienceLevel,
    this.equipment = const [],
    this.trainingDaysPerWeek,
    this.trainingMinutes,
    this.preferredTime,
    this.timezone,
    this.tdeeCalories,
    this.dietaryGoal,
    this.notes,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      id: json['id'] as int?,
      sex: json['sex'] as String?,
      birthDate: json['birthDate'] != null ? DateTime.parse(json['birthDate'] as String) : null,
      heightCm: (json['heightCm'] as num?)?.toDouble(),
      weightKg: (json['weightKg'] as num?)?.toDouble(),
      activityLevel: json['activityLevel'] as String?,
      experienceLevel: json['experienceLevel'] as String?,
      equipment: (json['equipment'] as List<dynamic>? ?? const []).map((e) => e as String).toList(),
      trainingDaysPerWeek: json['trainingDaysPerWeek'] as int?,
      trainingMinutes: json['trainingMinutes'] as int?,
      preferredTime: json['preferredTime'] as String?,
      timezone: json['timezone'] as String?,
      tdeeCalories: json['tdeeCalories'] as int?,
      dietaryGoal: json['dietaryGoal'] as String?,
      notes: json['notes'] as String?,
    );
  }

  Map<String, dynamic> toJson() => {
        'sex': sex,
        'birthDate': birthDate?.toIso8601String().split('T').first,
        'heightCm': heightCm,
        'weightKg': weightKg,
        'activityLevel': activityLevel,
        'experienceLevel': experienceLevel,
        'equipment': equipment,
        'trainingDaysPerWeek': trainingDaysPerWeek,
        'trainingMinutes': trainingMinutes,
        'preferredTime': preferredTime,
        'timezone': timezone,
        'tdeeCalories': tdeeCalories,
        'dietaryGoal': dietaryGoal,
        'notes': notes,
      };

  UserProfile copyWith({
    String? sex,
    DateTime? birthDate,
    double? heightCm,
    double? weightKg,
    String? activityLevel,
    String? experienceLevel,
    List<String>? equipment,
    int? trainingDaysPerWeek,
    int? trainingMinutes,
    String? preferredTime,
    String? timezone,
    int? tdeeCalories,
    String? dietaryGoal,
    String? notes,
  }) {
    return UserProfile(
      id: id,
      sex: sex ?? this.sex,
      birthDate: birthDate ?? this.birthDate,
      heightCm: heightCm ?? this.heightCm,
      weightKg: weightKg ?? this.weightKg,
      activityLevel: activityLevel ?? this.activityLevel,
      experienceLevel: experienceLevel ?? this.experienceLevel,
      equipment: equipment ?? this.equipment,
      trainingDaysPerWeek: trainingDaysPerWeek ?? this.trainingDaysPerWeek,
      trainingMinutes: trainingMinutes ?? this.trainingMinutes,
      preferredTime: preferredTime ?? this.preferredTime,
      timezone: timezone ?? this.timezone,
      tdeeCalories: tdeeCalories ?? this.tdeeCalories,
      dietaryGoal: dietaryGoal ?? this.dietaryGoal,
      notes: notes ?? this.notes,
    );
  }
}
