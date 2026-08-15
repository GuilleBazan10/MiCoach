// =====================================================================
// MiCoach — Pestaña "Métricas": lista filtrable + registrar nueva.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../../../../core/theme/app_spacing.dart';
import '../../application/progress_providers.dart';
import '../progress_labels.dart';
import 'add_entry_dialog.dart';

final _dateFormat = DateFormat('dd/MM/yyyy HH:mm');

class MetricEntriesView extends ConsumerStatefulWidget {
  const MetricEntriesView({super.key});

  @override
  ConsumerState<MetricEntriesView> createState() => _MetricEntriesViewState();
}

class _MetricEntriesViewState extends ConsumerState<MetricEntriesView> {
  String? _filter;

  Future<void> _addEntry() async {
    final result = await showAddEntryDialog(context, initialMetricType: _filter);
    if (result == null) return;
    await ref.read(progressActionsProvider).addEntry(
          metricType: result.metricType,
          value: result.value,
          unit: result.unit,
          notes: result.notes,
        );
  }

  @override
  Widget build(BuildContext context) {
    final entriesAsync = ref.watch(progressEntriesProvider(_filter));

    return Scaffold(
      body: Column(children: [
        SizedBox(
          height: 48,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md, vertical: AppSpacing.xs),
            children: [
              Padding(
                padding: const EdgeInsets.only(right: AppSpacing.xs),
                child: ChoiceChip(label: const Text('Todas'), selected: _filter == null, onSelected: (_) => setState(() => _filter = null)),
              ),
              for (final entry in metricTypeLabels.entries)
                Padding(
                  padding: const EdgeInsets.only(right: AppSpacing.xs),
                  child: ChoiceChip(
                    label: Text(entry.value),
                    selected: _filter == entry.key,
                    onSelected: (_) => setState(() => _filter = entry.key),
                  ),
                ),
            ],
          ),
        ),
        Expanded(
          child: entriesAsync.when(
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text('Error: $e')),
            data: (entries) {
              if (entries.isEmpty) {
                return const Center(
                  child: Padding(
                    padding: EdgeInsets.all(AppSpacing.lg),
                    child: Text('Todavía no registraste ninguna métrica.', textAlign: TextAlign.center),
                  ),
                );
              }
              return RefreshIndicator(
                onRefresh: () async => ref.invalidate(progressEntriesProvider),
                child: ListView.builder(
                  padding: const EdgeInsets.all(AppSpacing.md),
                  itemCount: entries.length,
                  itemBuilder: (context, index) {
                    final entry = entries[index];
                    return Card(
                      margin: const EdgeInsets.only(bottom: AppSpacing.xs),
                      child: ListTile(
                        title: Text('${labelFor(metricTypeLabels, entry.metricType)}: ${entry.value} ${entry.unit}'),
                        subtitle: Text(_dateFormat.format(entry.measuredAt.toLocal())),
                        trailing: IconButton(
                          icon: const Icon(Icons.delete_outline),
                          onPressed: () => ref.read(progressActionsProvider).deleteEntry(entry.id),
                        ),
                      ),
                    );
                  },
                ),
              );
            },
          ),
        ),
      ]),
      floatingActionButton: FloatingActionButton(onPressed: _addEntry, child: const Icon(Icons.add)),
    );
  }
}
