// =====================================================================
// KineticOs — Providers transversales (Riverpod): storage + cliente HTTP.
// Los providers de cada feature dependen de estos para construir sus
// repositorios (ver features/*/infrastructure).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../network/api_client.dart';
import '../storage/token_storage.dart';

final tokenStorageProvider = Provider<TokenStorage>((ref) => TokenStorage());

final apiClientProvider = Provider<ApiClient>((ref) {
  return ApiClient(tokenStorage: ref.watch(tokenStorageProvider));
});
