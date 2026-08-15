// =====================================================================
// MiCoach — Forma unificada de error de la API (docs/03-api-contracts.md).
// Equivalente a web/src/core/api/apiError.ts.
// =====================================================================
import 'package:dio/dio.dart';

/// Extrae un mensaje legible de un error de Dio contra la API de MiCoach.
String extractErrorMessage(Object error, {String fallback = 'Ocurrió un error inesperado.'}) {
  if (error is DioException) {
    final data = error.response?.data;
    if (data is Map<String, dynamic> && data['message'] is String) {
      return data['message'] as String;
    }
  }
  return fallback;
}
