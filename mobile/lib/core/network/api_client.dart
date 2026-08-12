// =====================================================================
// KineticOs — Cliente HTTP (Dio) centralizado.
// - Adjunta el JWT de acceso a cada request (salvo endpoints públicos de auth).
// - Si una respuesta es 401, intenta refrescar el token una vez y reintenta.
// - Si el refresh falla, limpia la sesión y notifica via [onSessionExpired]
//   (lo asigna el provider de auth para poder redirigir a login).
// - Agrega X-Correlation-Id para trazabilidad (docs/03-api-contracts.md).
// =====================================================================
import 'dart:math';

import 'package:dio/dio.dart';

import '../storage/token_storage.dart';

const String apiBaseUrl = String.fromEnvironment(
  'API_BASE_URL',
  defaultValue: 'http://localhost:8081/api/v1',
);

const List<String> _publicAuthPaths = ['/auth/register', '/auth/login', '/auth/refresh'];

class ApiClient {
  final Dio dio;
  final TokenStorage tokenStorage;

  /// Se asigna desde el provider de auth para reaccionar cuando la sesión expira.
  void Function()? onSessionExpired;

  ApiClient({required this.tokenStorage, String baseUrl = apiBaseUrl})
      : dio = Dio(BaseOptions(
          baseUrl: baseUrl,
          connectTimeout: const Duration(seconds: 10),
          receiveTimeout: const Duration(seconds: 15),
        )) {
    final refreshDio = Dio(BaseOptions(baseUrl: baseUrl, connectTimeout: const Duration(seconds: 10)));

    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        options.headers['X-Correlation-Id'] = _newCorrelationId();
        if (!_isPublicAuthPath(options.path)) {
          final token = await tokenStorage.readAccessToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
        }
        handler.next(options);
      },
      onError: (error, handler) async {
        final isUnauthorized = error.response?.statusCode == 401;
        if (isUnauthorized && !_isPublicAuthPath(error.requestOptions.path)) {
          final refreshed = await _tryRefresh(refreshDio);
          if (refreshed != null) {
            final retryOptions = error.requestOptions;
            retryOptions.headers['Authorization'] = 'Bearer $refreshed';
            try {
              final response = await dio.fetch(retryOptions);
              handler.resolve(response);
              return;
            } on DioException catch (retryError) {
              handler.next(retryError);
              return;
            }
          }
          await tokenStorage.clear();
          onSessionExpired?.call();
        }
        handler.next(error);
      },
    ));
  }

  Future<String?> _tryRefresh(Dio refreshDio) async {
    final refreshToken = await tokenStorage.readRefreshToken();
    if (refreshToken == null) return null;
    try {
      final response = await refreshDio.post('/auth/refresh', data: {'refreshToken': refreshToken});
      final accessToken = response.data['accessToken'] as String;
      final newRefreshToken = response.data['refreshToken'] as String;
      await tokenStorage.saveTokens(accessToken: accessToken, refreshToken: newRefreshToken);
      return accessToken;
    } catch (_) {
      return null;
    }
  }

  bool _isPublicAuthPath(String path) => _publicAuthPaths.any((p) => path.endsWith(p));

  String _newCorrelationId() {
    final random = Random();
    final bytes = List<int>.generate(16, (_) => random.nextInt(256));
    return bytes.map((b) => b.toRadixString(16).padLeft(2, '0')).join();
  }
}
