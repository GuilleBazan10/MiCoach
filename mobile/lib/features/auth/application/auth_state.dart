// =====================================================================
// KineticOs — Estado de autenticación.
// =====================================================================
import '../domain/auth_user.dart';

enum AuthStatus { unknown, authenticating, authenticated, unauthenticated }

class AuthState {
  final AuthStatus status;
  final AuthUser? user;
  final String? errorMessage;

  const AuthState({this.status = AuthStatus.unknown, this.user, this.errorMessage});

  bool get isAuthenticated => status == AuthStatus.authenticated && user != null;

  AuthState copyWith({AuthStatus? status, AuthUser? user, String? errorMessage, bool clearError = false}) {
    return AuthState(
      status: status ?? this.status,
      user: user ?? this.user,
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
    );
  }
}
