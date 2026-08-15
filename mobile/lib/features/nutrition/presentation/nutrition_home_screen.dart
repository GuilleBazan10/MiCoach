// =====================================================================
// MiCoach — Home del módulo nutrition: planes / diario / compras.
// =====================================================================
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'widgets/daily_intake_view.dart';
import 'widgets/generate_meal_plan_dialog.dart';
import 'widgets/meal_plan_list_view.dart';
import 'widgets/shopping_list_list_view.dart';

class NutritionHomeScreen extends ConsumerStatefulWidget {
  const NutritionHomeScreen({super.key});

  @override
  ConsumerState<NutritionHomeScreen> createState() => _NutritionHomeScreenState();
}

class _NutritionHomeScreenState extends ConsumerState<NutritionHomeScreen> with SingleTickerProviderStateMixin {
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
        title: const Text('Nutrición'),
        actions: [
          AnimatedBuilder(
            animation: _tabController,
            builder: (context, _) => _tabController.index == 0
                ? IconButton(
                    icon: const Icon(Icons.auto_awesome),
                    tooltip: 'Generar con IA',
                    onPressed: () => showGenerateMealPlanDialog(context, ref),
                  )
                : const SizedBox.shrink(),
          ),
        ],
        bottom: TabBar(controller: _tabController, tabs: const [
          Tab(text: 'Planes'),
          Tab(text: 'Diario'),
          Tab(text: 'Compras'),
        ]),
      ),
      body: TabBarView(controller: _tabController, children: const [
        MealPlanListView(),
        DailyIntakeView(),
        ShoppingListListView(),
      ]),
      floatingActionButton: AnimatedBuilder(
        animation: _tabController,
        builder: (context, _) => _tabController.index == 0
            ? FloatingActionButton(
                onPressed: () => context.push('/nutrition/plans/new'),
                child: const Icon(Icons.add),
              )
            : const SizedBox.shrink(),
      ),
    );
  }
}
