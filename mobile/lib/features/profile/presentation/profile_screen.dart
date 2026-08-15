// =====================================================================
// MiCoach — Pantalla de perfil de salud: datos + objetivos/patologías/
// lesiones/medicación (sub-recursos del módulo user).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/app_spacing.dart';
import '../../auth/application/auth_providers.dart';
import '../application/profile_providers.dart';
import '../domain/user_profile.dart';
import 'widgets/injury_section.dart';
import 'widgets/medication_section.dart';
import 'widgets/pathology_section.dart';
import 'widgets/goal_section.dart';

const _sexOptions = {'male': 'Masculino', 'female': 'Femenino', 'other': 'Otro'};
const _activityOptions = {
  'sedentary': 'Sedentario',
  'light': 'Actividad ligera',
  'moderate': 'Actividad moderada',
  'active': 'Activo',
  'very_active': 'Muy activo',
};
const _experienceOptions = {'beginner': 'Principiante', 'intermediate': 'Intermedio', 'advanced': 'Avanzado'};
const _preferredTimeOptions = {
  'morning': 'Mañana',
  'midday': 'Mediodía',
  'afternoon': 'Tarde',
  'evening': 'Noche',
};
const _dietaryGoalOptions = {
  'lose_fat': 'Perder grasa',
  'gain_muscle': 'Ganar músculo',
  'maintain': 'Mantener',
  'endurance': 'Resistencia',
  'health': 'Salud general',
};

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profileState = ref.watch(profileControllerProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mi perfil'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Cerrar sesión',
            onPressed: () => ref.read(authControllerProvider.notifier).logout(),
          ),
        ],
      ),
      body: profileState.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => Center(
          child: Padding(
            padding: const EdgeInsets.all(AppSpacing.lg),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text('No se pudo cargar el perfil.\n$error', textAlign: TextAlign.center),
                const SizedBox(height: AppSpacing.md),
                FilledButton(
                  onPressed: () => ref.invalidate(profileControllerProvider),
                  child: const Text('Reintentar'),
                ),
              ],
            ),
          ),
        ),
        data: (data) => _ProfileBody(data: data),
      ),
    );
  }
}

class _ProfileBody extends ConsumerStatefulWidget {
  final ProfileData data;

  const _ProfileBody({required this.data});

  @override
  ConsumerState<_ProfileBody> createState() => _ProfileBodyState();
}

class _ProfileBodyState extends ConsumerState<_ProfileBody> {
  late final TextEditingController _heightController;
  late final TextEditingController _weightController;
  late final TextEditingController _equipmentController;
  late final TextEditingController _trainingDaysController;
  late final TextEditingController _trainingMinutesController;
  late final TextEditingController _notesController;

  String? _sex;
  DateTime? _birthDate;
  String? _activityLevel;
  String? _experienceLevel;
  String? _preferredTime;
  String? _dietaryGoal;
  bool _saving = false;

  @override
  void initState() {
    super.initState();
    final p = widget.data.profile;
    _heightController = TextEditingController(text: p.heightCm?.toString() ?? '');
    _weightController = TextEditingController(text: p.weightKg?.toString() ?? '');
    _equipmentController = TextEditingController(text: p.equipment.join(', '));
    _trainingDaysController = TextEditingController(text: p.trainingDaysPerWeek?.toString() ?? '');
    _trainingMinutesController = TextEditingController(text: p.trainingMinutes?.toString() ?? '');
    _notesController = TextEditingController(text: p.notes ?? '');
    _sex = p.sex;
    _birthDate = p.birthDate;
    _activityLevel = p.activityLevel;
    _experienceLevel = p.experienceLevel;
    _preferredTime = p.preferredTime;
    _dietaryGoal = p.dietaryGoal;
  }

  @override
  void dispose() {
    _heightController.dispose();
    _weightController.dispose();
    _equipmentController.dispose();
    _trainingDaysController.dispose();
    _trainingMinutesController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  Future<void> _pickBirthDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _birthDate ?? DateTime(2000, 1, 1),
      firstDate: DateTime(1920),
      lastDate: DateTime.now(),
    );
    if (picked != null) setState(() => _birthDate = picked);
  }

  Future<void> _save() async {
    setState(() => _saving = true);
    final updated = UserProfile(
      sex: _sex,
      birthDate: _birthDate,
      heightCm: double.tryParse(_heightController.text.replaceAll(',', '.')),
      weightKg: double.tryParse(_weightController.text.replaceAll(',', '.')),
      activityLevel: _activityLevel,
      experienceLevel: _experienceLevel,
      equipment: _equipmentController.text.split(',').map((e) => e.trim()).where((e) => e.isNotEmpty).toList(),
      trainingDaysPerWeek: int.tryParse(_trainingDaysController.text),
      trainingMinutes: int.tryParse(_trainingMinutesController.text),
      preferredTime: _preferredTime,
      timezone: widget.data.profile.timezone,
      tdeeCalories: widget.data.profile.tdeeCalories,
      dietaryGoal: _dietaryGoal,
      notes: _notesController.text.trim().isEmpty ? null : _notesController.text.trim(),
    );
    try {
      await ref.read(profileControllerProvider.notifier).updateProfile(updated);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Perfil actualizado')));
      }
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AppSpacing.md),
      children: [
        Text('Datos físicos', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _sex,
          decoration: const InputDecoration(labelText: 'Sexo'),
          items: _sexOptions.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
          onChanged: (v) => setState(() => _sex = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        InkWell(
          onTap: _pickBirthDate,
          child: InputDecorator(
            decoration: const InputDecoration(labelText: 'Fecha de nacimiento'),
            child: Text(_birthDate != null
                ? '${_birthDate!.day}/${_birthDate!.month}/${_birthDate!.year}'
                : 'Sin especificar'),
          ),
        ),
        const SizedBox(height: AppSpacing.sm),
        Row(children: [
          Expanded(
            child: TextField(
              controller: _heightController,
              decoration: const InputDecoration(labelText: 'Altura (cm)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
            ),
          ),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: TextField(
              controller: _weightController,
              decoration: const InputDecoration(labelText: 'Peso (kg)'),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
            ),
          ),
        ]),
        const SizedBox(height: AppSpacing.lg),
        Text('Entrenamiento', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _activityLevel,
          decoration: const InputDecoration(labelText: 'Nivel de actividad'),
          items:
              _activityOptions.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
          onChanged: (v) => setState(() => _activityLevel = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _experienceLevel,
          decoration: const InputDecoration(labelText: 'Nivel de experiencia'),
          items: _experienceOptions.entries
              .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
              .toList(),
          onChanged: (v) => setState(() => _experienceLevel = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        TextField(
          controller: _equipmentController,
          decoration: const InputDecoration(labelText: 'Equipamiento (separado por comas)'),
        ),
        const SizedBox(height: AppSpacing.sm),
        Row(children: [
          Expanded(
            child: TextField(
              controller: _trainingDaysController,
              decoration: const InputDecoration(labelText: 'Días/semana'),
              keyboardType: TextInputType.number,
            ),
          ),
          const SizedBox(width: AppSpacing.sm),
          Expanded(
            child: TextField(
              controller: _trainingMinutesController,
              decoration: const InputDecoration(labelText: 'Minutos/sesión'),
              keyboardType: TextInputType.number,
            ),
          ),
        ]),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _preferredTime,
          decoration: const InputDecoration(labelText: 'Horario preferido'),
          items: _preferredTimeOptions.entries
              .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
              .toList(),
          onChanged: (v) => setState(() => _preferredTime = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        DropdownButtonFormField<String>(
          initialValue: _dietaryGoal,
          decoration: const InputDecoration(labelText: 'Objetivo principal'),
          items: _dietaryGoalOptions.entries
              .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value)))
              .toList(),
          onChanged: (v) => setState(() => _dietaryGoal = v),
        ),
        const SizedBox(height: AppSpacing.sm),
        TextField(
          controller: _notesController,
          decoration: const InputDecoration(labelText: 'Notas'),
          maxLines: 3,
        ),
        const SizedBox(height: AppSpacing.md),
        FilledButton(
          onPressed: _saving ? null : _save,
          child: _saving
              ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
              : const Text('Guardar perfil'),
        ),
        const SizedBox(height: AppSpacing.xl),
        GoalSection(goals: widget.data.goals),
        const SizedBox(height: AppSpacing.md),
        PathologySection(pathologies: widget.data.pathologies),
        const SizedBox(height: AppSpacing.md),
        InjurySection(injuries: widget.data.injuries),
        const SizedBox(height: AppSpacing.md),
        MedicationSection(medications: widget.data.medications),
        const SizedBox(height: AppSpacing.xl),
      ],
    );
  }
}
