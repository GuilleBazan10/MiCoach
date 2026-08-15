// =====================================================================
// MiCoach — Repositorio de auth: combina la API con la sesión persistida.
// =====================================================================
import '../../../core/storage/token_storage.dart';
import '../domain/auth_user.dart';
import 'auth_api.dart';

class AuthRepository {
  final AuthApi api;
  final TokenStorage tokenStorage;

  AuthRepository({required this.api, required this.tokenStorage});

  Future<AuthUser> register(String email, String password) async {
    final result = await api.register(email, password);
    await tokenStorage.saveTokens(accessToken: result.accessToken, refreshToken: result.refreshToken);
    return result.user;
  }

  Future<AuthUser> login(String email, String password) async {
    final result = await api.login(email, password);
    await tokenStorage.saveTokens(accessToken: result.accessToken, refreshToken: result.refreshToken);
    return result.user;
  }

  Future<void> logout() => tokenStorage.clear();

  /// Intenta restaurar la sesión con el access token guardado. Si no hay
  /// sesión o el token ya no es válido (y el refresh también falla), null.
  Future<AuthUser?> tryRestoreSession() async {
    if (!await tokenStorage.hasSession()) return null;
    try {
      return await api.me();
    } catch (_) {
      await tokenStorage.clear();
      return null;
    }
  }
}
