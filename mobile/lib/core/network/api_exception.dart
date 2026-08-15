// =====================================================================
// MiCoach — Excepción tipada para errores de API.
// Mapea el formato unificado de error del backend (docs/03-api-contracts.md):
// {timestamp, status, code, message, path}
// =====================================================================
import 'package:dio/dio.dart';

class ApiException implements Exception {
  final int status;
  final String code;
  final String message;

  const ApiException({required this.status, required this.code, required this.message});

  factory ApiException.fromDioException(DioException error) {
    final data = error.response?.data;
    if (data is Map<String, dynamic>) {
      return ApiException(
        status: (data['status'] as num?)?.toInt() ?? error.response?.statusCode ?? 0,
        code: data['code'] as String? ?? 'UNKNOWN_ERROR',
        message: data['message'] as String? ?? 'Error desconocido',
      );
    }
    return ApiException(
      status: error.response?.statusCode ?? 0,
      code: 'NETWORK_ERROR',
      message: error.message ?? 'No se pudo conectar con el servidor',
    );
  }

  @override
  String toString() => message;
}
