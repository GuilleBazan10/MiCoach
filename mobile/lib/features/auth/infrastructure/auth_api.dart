// =====================================================================
// KineticOs — Cliente REST del módulo auth (/api/v1/auth).
// =====================================================================
import 'package:dio/dio.dart';

import '../domain/auth_user.dart';

class AuthResult {
  final String accessToken;
  final String refreshToken;
  final AuthUser user;

  const AuthResult({required this.accessToken, required this.refreshToken, required this.user});

  factory AuthResult.fromJson(Map<String, dynamic> json) {
    return AuthResult(
      accessToken: json['accessToken'] as String,
      refreshToken: json['refreshToken'] as String,
      user: AuthUser.fromJson(json['user'] as Map<String, dynamic>),
    );
  }
}

class AuthApi {
  final Dio _dio;

  AuthApi(this._dio);

  Future<AuthResult> register(String email, String password) async {
    final response = await _dio.post('/auth/register', data: {'email': email, 'password': password});
    return AuthResult.fromJson(response.data as Map<String, dynamic>);
  }

  Future<AuthResult> login(String email, String password) async {
    final response = await _dio.post('/auth/login', data: {'email': email, 'password': password});
    return AuthResult.fromJson(response.data as Map<String, dynamic>);
  }

  Future<AuthUser> me() async {
    final response = await _dio.get('/auth/me');
    return AuthUser.fromJson(response.data as Map<String, dynamic>);
  }
}
