// =====================================================================
// MiCoach — Providers del módulo auth (DI + controller de sesión).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/network/api_exception.dart';
import '../../../core/providers/core_providers.dart';
import '../infrastructure/auth_api.dart';
import '../infrastructure/auth_repository.dart';
import 'auth_state.dart';

final authApiProvider = Provider<AuthApi>((ref) => AuthApi(ref.watch(apiClientProvider).dio));

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepository(api: ref.watch(authApiProvider), tokenStorage: ref.watch(tokenStorageProvider));
});

final authControllerProvider = NotifierProvider<AuthController, AuthState>(AuthController.new);

class AuthController extends Notifier<AuthState> {
  @override
  AuthState build() {
    // El cliente HTTP notifica acá cuando el refresh token también expiró,
    // para que el router redirija a login.
    ref.watch(apiClientProvider).onSessionExpired = () {
      state = const AuthState(status: AuthStatus.unauthenticated);
    };
    Future.microtask(_restoreSession);
    return const AuthState(status: AuthStatus.unknown);
  }

  AuthRepository get _repository => ref.read(authRepositoryProvider);

  Future<void> _restoreSession() async {
    final user = await _repository.tryRestoreSession();
    state = user != null
        ? AuthState(status: AuthStatus.authenticated, user: user)
        : const AuthState(status: AuthStatus.unauthenticated);
  }

  Future<void> login(String email, String password) async {
    state = state.copyWith(status: AuthStatus.authenticating, clearError: true);
    try {
      final user = await _repository.login(email, password);
      state = AuthState(status: AuthStatus.authenticated, user: user);
    } on ApiException catch (e) {
      state = AuthState(status: AuthStatus.unauthenticated, errorMessage: e.message);
    }
  }

  Future<void> register(String email, String password) async {
    state = state.copyWith(status: AuthStatus.authenticating, clearError: true);
    try {
      final user = await _repository.register(email, password);
      state = AuthState(status: AuthStatus.authenticated, user: user);
    } on ApiException catch (e) {
      state = AuthState(status: AuthStatus.unauthenticated, errorMessage: e.message);
    }
  }

  Future<void> logout() async {
    await _repository.logout();
    state = const AuthState(status: AuthStatus.unauthenticated);
  }
}
