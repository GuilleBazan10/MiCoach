// =====================================================================
// MiCoach — Guarda de rutas admin: además de autenticado, exige ROLE_ADMIN.
// Va anidado dentro de <RequireAuth /> en router.tsx.
// =====================================================================
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/features/auth/application/useAuth';

export function RequireAdmin() {
  const { user } = useAuth();

  if (!user?.roles.includes('ROLE_ADMIN')) {
    return <Navigate to="/" replace />;
  }
  return <Outlet />;
}
