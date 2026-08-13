// =====================================================================
// KineticOs — Guarda de rutas autenticadas.
// Equivalente al `redirect` de GoRouter en mobile/lib/core/router/app_router.dart.
// =====================================================================
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/auth/application/useAuth';
import { SplashScreen } from './SplashScreen';

export function RequireAuth() {
  const { status } = useAuth();
  const location = useLocation();

  if (status === 'unknown') return <SplashScreen />;
  if (status === 'unauthenticated') {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  return <Outlet />;
}

/** Para /login y /register: si ya hay sesión, no tiene sentido mostrarlas. */
export function RedirectIfAuthenticated() {
  const { status } = useAuth();

  if (status === 'unknown') return <SplashScreen />;
  if (status === 'authenticated') return <Navigate to="/" replace />;
  return <Outlet />;
}
