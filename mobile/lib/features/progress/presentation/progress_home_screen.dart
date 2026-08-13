// =====================================================================
// KineticOs — Home del módulo progress: métricas / fotos.
// =====================================================================
import 'package:flutter/material.dart';

import 'widgets/metric_entries_view.dart';
import 'widgets/progress_photos_view.dart';

class ProgressHomeScreen extends StatefulWidget {
  const ProgressHomeScreen({super.key});

  @override
  State<ProgressHomeScreen> createState() => _ProgressHomeScreenState();
}

class _ProgressHomeScreenState extends State<ProgressHomeScreen> with SingleTickerProviderStateMixin {
  late final TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Progreso'),
        bottom: TabBar(controller: _tabController, tabs: const [
          Tab(text: 'Métricas'),
          Tab(text: 'Fotos'),
        ]),
      ),
      body: TabBarView(controller: _tabController, children: const [
        MetricEntriesView(),
        ProgressPhotosView(),
      ]),
    );
  }
}
