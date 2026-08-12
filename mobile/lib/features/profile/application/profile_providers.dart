// =====================================================================
// KineticOs — Providers del módulo profile (DI + controller de perfil).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers/core_providers.dart';
import '../domain/user_profile.dart';
import '../domain/user_subresources.dart';
import '../infrastructure/profile_api.dart';

final profileApiProvider = Provider<ProfileApi>((ref) => ProfileApi(ref.watch(apiClientProvider).dio));

class ProfileData {
  final UserProfile profile;
  final List<UserGoal> goals;
  final List<UserPathology> pathologies;
  final List<UserInjury> injuries;
  final List<UserMedication> medications;

  const ProfileData({
    required this.profile,
    required this.goals,
    required this.pathologies,
    required this.injuries,
    required this.medications,
  });
}

final profileControllerProvider = AsyncNotifierProvider<ProfileController, ProfileData>(ProfileController.new);

class ProfileController extends AsyncNotifier<ProfileData> {
  ProfileApi get _api => ref.read(profileApiProvider);

  @override
  Future<ProfileData> build() => _loadAll();

  Future<ProfileData> _loadAll() async {
    final profile = await _api.getProfile();
    final goals = await _api.getGoals();
    final pathologies = await _api.getPathologies();
    final injuries = await _api.getInjuries();
    final medications = await _api.getMedications();
    return ProfileData(
        profile: profile, goals: goals, pathologies: pathologies, injuries: injuries, medications: medications);
  }

  Future<void> _refresh() async {
    ref.invalidateSelf();
    await future;
  }

  Future<void> updateProfile(UserProfile updated) async {
    await _api.updateProfile(updated);
    await _refresh();
  }

  Future<void> addGoal({required String goalType, double? targetValue, String? targetUnit,
      DateTime? targetDate, int? priority}) async {
    await _api.addGoal(
        goalType: goalType, targetValue: targetValue, targetUnit: targetUnit, targetDate: targetDate,
        priority: priority);
    await _refresh();
  }

  Future<void> deleteGoal(int id) async {
    await _api.deleteGoal(id);
    await _refresh();
  }

  Future<void> addPathology({required String pathology, String? notes, DateTime? diagnosedAt}) async {
    await _api.addPathology(pathology: pathology, notes: notes, diagnosedAt: diagnosedAt);
    await _refresh();
  }

  Future<void> deletePathology(int id) async {
    await _api.deletePathology(id);
    await _refresh();
  }

  Future<void> addInjury({required String bodyPart, required String injuryType, String? status,
      String? notes, DateTime? occurredAt}) async {
    await _api.addInjury(
        bodyPart: bodyPart, injuryType: injuryType, status: status, notes: notes, occurredAt: occurredAt);
    await _refresh();
  }

  Future<void> deleteInjury(int id) async {
    await _api.deleteInjury(id);
    await _refresh();
  }

  Future<void> addMedication({required String medicationName, String? dosage, String? schedule,
      String? notes}) async {
    await _api.addMedication(medicationName: medicationName, dosage: dosage, schedule: schedule, notes: notes);
    await _refresh();
  }

  Future<void> deleteMedication(int id) async {
    await _api.deleteMedication(id);
    await _refresh();
  }
}
