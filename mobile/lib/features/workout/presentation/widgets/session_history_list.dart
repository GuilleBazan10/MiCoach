// =====================================================================
// KineticOs — Historial de sesiones de entrenamiento.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/workout_providers.dart';

const _statusLabels = {'in_progress': 'En curso', 'completed': 'Completada', 'aborted': 'Abandonada'};
final _dateFormat = DateFormat('dd/MM/yyyy HH:mm');

class SessionHistoryList extends ConsumerWidget {
  const SessionHistoryList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessionsAsync = ref.watch(sessionListProvider);

    return sessionsAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Error: $e')),
      data: (sessions) {
        if (sessions.isEmpty) {
          return const Center(
            child: Padding(
              padding: EdgeInsets.all(AppSpacing.lg),
              child: Text('Todavía no registraste ninguna sesión.', textAlign: TextAlign.center),
            ),
          );
        }
        return RefreshIndicator(
          onRefresh: () async => ref.invalidate(sessionListProvider),
          child: ListView.builder(
            padding: const EdgeInsets.all(AppSpacing.md),
            itemCount: sessions.length,
            itemBuilder: (context, index) {
              final session = sessions[index];
              return Card(
                margin: const EdgeInsets.only(bottom: AppSpacing.sm),
                child: ListTile(
                  title: Text(session.startedAt != null ? _dateFormat.format(session.startedAt!.toLocal()) : 'Sesión #${session.id}'),
                  subtitle: Text(_statusLabels[session.status] ?? session.status),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () => context.push('/sessions/${session.id}'),
                ),
              );
            },
          ),
        );
      },
    );
  }
}
