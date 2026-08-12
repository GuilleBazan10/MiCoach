// =====================================================================
// KineticOs — Home del módulo workout: mis rutinas / plantillas / historial.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'widgets/session_history_list.dart';
import 'widgets/workout_list_view.dart';

class WorkoutHomeScreen extends StatefulWidget {
  const WorkoutHomeScreen({super.key});

  @override
  State<WorkoutHomeScreen> createState() => _WorkoutHomeScreenState();
}

class _WorkoutHomeScreenState extends State<WorkoutHomeScreen> with SingleTickerProviderStateMixin {
  late final TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
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
        title: const Text('Rutinas'),
        bottom: TabBar(controller: _tabController, tabs: const [
          Tab(text: 'Mis rutinas'),
          Tab(text: 'Plantillas'),
          Tab(text: 'Historial'),
        ]),
      ),
      body: TabBarView(controller: _tabController, children: const [
        WorkoutListView(templates: false),
        WorkoutListView(templates: true),
        SessionHistoryList(),
      ]),
      floatingActionButton: AnimatedBuilder(
        animation: _tabController,
        builder: (context, _) => _tabController.index == 0
            ? FloatingActionButton(
                onPressed: () => context.push('/workouts/new'),
                child: const Icon(Icons.add),
              )
            : const SizedBox.shrink(),
      ),
    );
  }
}
