// =====================================================================
// KineticOs — Usuario autenticado (respuesta de /api/v1/auth/*).
// =====================================================================
class AuthUser {
  final int id;
  final String email;
  final List<String> roles;

  const AuthUser({required this.id, required this.email, required this.roles});

  factory AuthUser.fromJson(Map<String, dynamic> json) {
    return AuthUser(
      id: json['id'] as int,
      email: json['email'] as String,
      roles: (json['roles'] as List<dynamic>? ?? const []).map((e) => e as String).toList(),
    );
  }
}
