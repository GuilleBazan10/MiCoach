// =====================================================================
// KineticOs — GoRouter: rutas + guarda de autenticación.
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/application/auth_providers.dart';
import '../../features/auth/application/auth_state.dart';
import '../../features/auth/presentation/login_screen.dart';
import '../../features/auth/presentation/register_screen.dart';
import '../../features/profile/presentation/profile_screen.dart';
import '../../features/workout/presentation/session_screen.dart';
import '../../features/workout/presentation/workout_detail_screen.dart';
import '../../features/workout/presentation/workout_form_screen.dart';
import '../../features/workout/presentation/workout_home_screen.dart';
import 'app_shell.dart';
import 'go_router_refresh_notifier.dart';
import 'splash_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final refreshNotifier = ref.watch(goRouterRefreshProvider);

  return GoRouter(
    initialLocation: '/',
    refreshListenable: refreshNotifier,
    redirect: (context, state) {
      final authState = ref.read(authControllerProvider);
      final location = state.matchedLocation;
      final isAuthRoute = location == '/login' || location == '/register';

      if (authState.status == AuthStatus.unknown) {
        return location == '/' ? null : '/';
      }
      if (!authState.isAuthenticated) {
        return isAuthRoute ? null : '/login';
      }
      if (location == '/' || isAuthRoute) {
        return '/workouts';
      }
      return null;
    },
    routes: [
      GoRoute(path: '/', builder: (context, state) => const SplashScreen()),
      GoRoute(path: '/login', builder: (context, state) => const LoginScreen()),
      GoRoute(path: '/register', builder: (context, state) => const RegisterScreen()),
      ShellRoute(
        builder: (context, state, child) => AppShell(child: child),
        routes: [
          GoRoute(path: '/workouts', builder: (context, state) => const WorkoutHomeScreen()),
          GoRoute(path: '/profile', builder: (context, state) => const ProfileScreen()),
        ],
      ),
      GoRoute(path: '/workouts/new', builder: (context, state) => const WorkoutFormScreen()),
      GoRoute(
        path: '/workouts/:id',
        builder: (context, state) => WorkoutDetailScreen(workoutId: int.parse(state.pathParameters['id']!)),
      ),
      GoRoute(
        path: '/workouts/:id/edit',
        builder: (context, state) => WorkoutFormScreen(workoutId: int.parse(state.pathParameters['id']!)),
      ),
      GoRoute(
        path: '/sessions/:id',
        builder: (context, state) => SessionScreen(sessionId: int.parse(state.pathParameters['id']!)),
      ),
    ],
  );
});
