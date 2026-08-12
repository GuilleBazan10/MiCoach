// =====================================================================
// KineticOs — Shell con navegación inferior (Rutinas / Perfil).
// =====================================================================
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AppShell extends StatelessWidget {
  final Widget child;

  const AppShell({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final location = GoRouterState.of(context).matchedLocation;
    final currentIndex = location.startsWith('/profile') ? 1 : 0;

    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: currentIndex,
        onDestinationSelected: (index) => context.go(index == 0 ? '/workouts' : '/profile'),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.fitness_center_outlined), label: 'Rutinas'),
          NavigationDestination(icon: Icon(Icons.person_outline), label: 'Perfil'),
        ],
      ),
    );
  }
}
