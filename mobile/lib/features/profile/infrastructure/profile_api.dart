// =====================================================================
// MiCoach — Cliente REST del módulo user (/api/v1/users/me).
// =====================================================================
import 'package:dio/dio.dart';

import '../domain/user_profile.dart';
import '../domain/user_subresources.dart';

class ProfileApi {
  final Dio _dio;

  ProfileApi(this._dio);

  Future<UserProfile> getProfile() async {
    final response = await _dio.get('/users/me/profile');
    return UserProfile.fromJson(response.data as Map<String, dynamic>);
  }

  Future<UserProfile> updateProfile(UserProfile profile) async {
    final response = await _dio.put('/users/me/profile', data: profile.toJson());
    return UserProfile.fromJson(response.data as Map<String, dynamic>);
  }

  // ------------------------- Objetivos -------------------------

  Future<List<UserGoal>> getGoals() async {
    final response = await _dio.get('/users/me/goals');
    return (response.data as List<dynamic>).map((e) => UserGoal.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<void> addGoal({
    required String goalType,
    double? targetValue,
    String? targetUnit,
    DateTime? targetDate,
    int? priority,
  }) {
    return _dio.post('/users/me/goals', data: {
      'goalType': goalType,
      'targetValue': targetValue,
      'targetUnit': targetUnit,
      'targetDate': targetDate?.toIso8601String().split('T').first,
      'priority': priority,
    });
  }

  Future<void> deleteGoal(int id) => _dio.delete('/users/me/goals/$id');

  // ------------------------- Patologías -------------------------

  Future<List<UserPathology>> getPathologies() async {
    final response = await _dio.get('/users/me/pathologies');
    return (response.data as List<dynamic>)
        .map((e) => UserPathology.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> addPathology({required String pathology, String? notes, DateTime? diagnosedAt}) {
    return _dio.post('/users/me/pathologies', data: {
      'pathology': pathology,
      'notes': notes,
      'diagnosedAt': diagnosedAt?.toIso8601String().split('T').first,
    });
  }

  Future<void> deletePathology(int id) => _dio.delete('/users/me/pathologies/$id');

  // ------------------------- Lesiones -------------------------

  Future<List<UserInjury>> getInjuries() async {
    final response = await _dio.get('/users/me/injuries');
    return (response.data as List<dynamic>).map((e) => UserInjury.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<void> addInjury({
    required String bodyPart,
    required String injuryType,
    String? status,
    String? notes,
    DateTime? occurredAt,
  }) {
    return _dio.post('/users/me/injuries', data: {
      'bodyPart': bodyPart,
      'injuryType': injuryType,
      'status': status,
      'notes': notes,
      'occurredAt': occurredAt?.toIso8601String().split('T').first,
    });
  }

  Future<void> deleteInjury(int id) => _dio.delete('/users/me/injuries/$id');

  // ------------------------- Medicación -------------------------

  Future<List<UserMedication>> getMedications() async {
    final response = await _dio.get('/users/me/medications');
    return (response.data as List<dynamic>)
        .map((e) => UserMedication.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> addMedication({required String medicationName, String? dosage, String? schedule, String? notes}) {
    return _dio.post('/users/me/medications', data: {
      'medicationName': medicationName,
      'dosage': dosage,
      'schedule': schedule,
      'notes': notes,
    });
  }

  Future<void> deleteMedication(int id) => _dio.delete('/users/me/medications/$id');
}
