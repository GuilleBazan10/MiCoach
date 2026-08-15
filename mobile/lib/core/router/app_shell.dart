// =====================================================================
// MiCoach — Shell con navegación inferior (Rutinas / Nutrición / Progreso / Perfil).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

const _tabPaths = ['/workouts', '/nutrition', '/progress', '/profile'];

class AppShell extends StatelessWidget {
  final Widget child;

  const AppShell({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final location = GoRouterState.of(context).matchedLocation;
    final currentIndex = _tabPaths.indexWhere((p) => location.startsWith(p)).clamp(0, _tabPaths.length - 1);

    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: currentIndex,
        onDestinationSelected: (index) => context.go(_tabPaths[index]),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.fitness_center_outlined), label: 'Rutinas'),
          NavigationDestination(icon: Icon(Icons.restaurant_outlined), label: 'Nutrición'),
          NavigationDestination(icon: Icon(Icons.show_chart_outlined), label: 'Progreso'),
          NavigationDestination(icon: Icon(Icons.person_outline), label: 'Perfil'),
        ],
      ),
    );
  }
}
