// =====================================================================
// MiCoach — Cliente REST del módulo progress (/api/v1/progress).
// =====================================================================
import 'package:dio/dio.dart';

import '../domain/progress_entry.dart';
import '../domain/progress_photo.dart';

class ProgressApi {
  final Dio _dio;

  ProgressApi(this._dio);

  // ------------------------- Métricas -------------------------

  Future<List<ProgressEntry>> listEntries({String? metricType}) async {
    final response = await _dio
        .get('/progress/entries', queryParameters: {if (metricType != null) 'metricType': metricType});
    return (response.data as List<dynamic>).map((e) => ProgressEntry.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<ProgressEntry> addEntry({
    required String metricType,
    required double value,
    required String unit,
    DateTime? measuredAt,
    String? notes,
  }) async {
    final response = await _dio.post('/progress/entries', data: {
      'metricType': metricType,
      'value': value,
      'unit': unit,
      'measuredAt': measuredAt?.toIso8601String(),
      'notes': notes,
    });
    return ProgressEntry.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteEntry(int id) => _dio.delete('/progress/entries/$id');

  // ------------------------- Fotos -------------------------

  Future<List<ProgressPhoto>> listPhotos() async {
    final response = await _dio.get('/progress/photos');
    return (response.data as List<dynamic>).map((e) => ProgressPhoto.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<ProgressPhoto> addPhoto({required String photoUrl, String? angle, DateTime? takenAt, String? notes}) async {
    final response = await _dio.post('/progress/photos', data: {
      'photoUrl': photoUrl,
      'angle': angle,
      'takenAt': takenAt?.toIso8601String(),
      'notes': notes,
    });
    return ProgressPhoto.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deletePhoto(int id) => _dio.delete('/progress/photos/$id');
}
