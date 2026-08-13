// =====================================================================
// KineticOs — Diálogo para registrar una métrica de seguimiento.
// =====================================================================
import 'package:flutter/material.dart';

import '../../../../core/theme/app_spacing.dart';
import '../progress_labels.dart';

class AddEntryResult {
  final String metricType;
  final double value;
  final String unit;
  final String? notes;

  const AddEntryResult({required this.metricType, required this.value, required this.unit, this.notes});
}

Future<AddEntryResult?> showAddEntryDialog(BuildContext context, {String? initialMetricType}) {
  return showDialog<AddEntryResult>(
    context: context,
    builder: (_) => _AddEntryDialog(initialMetricType: initialMetricType),
  );
}

class _AddEntryDialog extends StatefulWidget {
  final String? initialMetricType;

  const _AddEntryDialog({this.initialMetricType});

  @override
  State<_AddEntryDialog> createState() => _AddEntryDialogState();
}

class _AddEntryDialogState extends State<_AddEntryDialog> {
  late String _metricType;
  final _valueController = TextEditingController();
  late final TextEditingController _unitController;
  final _notesController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _metricType = widget.initialMetricType ?? metricTypeLabels.keys.first;
    _unitController = TextEditingController(text: metricTypeDefaultUnit[_metricType]);
  }

  @override
  void dispose() {
    _valueController.dispose();
    _unitController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Registrar métrica'),
      content: SingleChildScrollView(
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          DropdownButtonFormField<String>(
            initialValue: _metricType,
            decoration: const InputDecoration(labelText: 'Métrica'),
            items: metricTypeLabels.entries.map((e) => DropdownMenuItem(value: e.key, child: Text(e.value))).toList(),
            onChanged: (v) => setState(() {
              _metricType = v ?? _metricType;
              _unitController.text = metricTypeDefaultUnit[_metricType] ?? _unitController.text;
            }),
          ),
          const SizedBox(height: AppSpacing.sm),
          TextField(
            controller: _valueController,
            decoration: const InputDecoration(labelText: 'Valor'),
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
          ),
          const SizedBox(height: AppSpacing.sm),
          TextField(controller: _unitController, decoration: const InputDecoration(labelText: 'Unidad')),
          const SizedBox(height: AppSpacing.sm),
          TextField(controller: _notesController, decoration: const InputDecoration(labelText: 'Notas (opcional)')),
        ]),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cancelar')),
        FilledButton(
          onPressed: () {
            final value = double.tryParse(_valueController.text.replaceAll(',', '.'));
            if (value == null || _unitController.text.trim().isEmpty) return;
            Navigator.of(context).pop(AddEntryResult(
              metricType: _metricType,
              value: value,
              unit: _unitController.text.trim(),
              notes: _notesController.text.trim().isEmpty ? null : _notesController.text.trim(),
            ));
          },
          child: const Text('Registrar'),
        ),
      ],
    );
  }
}
