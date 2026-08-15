// =====================================================================
// MiCoach — Providers del módulo progress (DI + lecturas + acciones).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers/core_providers.dart';
import '../domain/progress_entry.dart';
import '../domain/progress_photo.dart';
import '../infrastructure/progress_api.dart';

final progressApiProvider = Provider<ProgressApi>((ref) => ProgressApi(ref.watch(apiClientProvider).dio));

final progressEntriesProvider = FutureProvider.family<List<ProgressEntry>, String?>(
  (ref, metricType) => ref.watch(progressApiProvider).listEntries(metricType: metricType),
);

final progressPhotosProvider = FutureProvider<List<ProgressPhoto>>(
  (ref) => ref.watch(progressApiProvider).listPhotos(),
);

final progressActionsProvider =
    Provider<ProgressActions>((ref) => ProgressActions(ref, ref.watch(progressApiProvider)));

class ProgressActions {
  final Ref _ref;
  final ProgressApi _api;

  ProgressActions(this._ref, this._api);

  Future<void> addEntry({
    required String metricType,
    required double value,
    required String unit,
    DateTime? measuredAt,
    String? notes,
  }) async {
    await _api.addEntry(metricType: metricType, value: value, unit: unit, measuredAt: measuredAt, notes: notes);
    _ref.invalidate(progressEntriesProvider);
  }

  Future<void> deleteEntry(int id) async {
    await _api.deleteEntry(id);
    _ref.invalidate(progressEntriesProvider);
  }

  Future<void> addPhoto({required String photoUrl, String? angle, DateTime? takenAt, String? notes}) async {
    await _api.addPhoto(photoUrl: photoUrl, angle: angle, takenAt: takenAt, notes: notes);
    _ref.invalidate(progressPhotosProvider);
  }

  Future<void> deletePhoto(int id) async {
    await _api.deletePhoto(id);
    _ref.invalidate(progressPhotosProvider);
  }
}
