// =====================================================================
// KineticOs — Shell de la app autenticada.
// Equivalente a mobile/lib/core/router/app_shell.dart. Por ahora solo hay
// una sección (Inicio); a medida que aterricen profile/workout/nutrition/
// progress (ver docs/00-progress.md § Fase 3.2) esto suma una barra de
// navegación real, con el mismo layout mobile-first.
// =====================================================================
import { Outlet } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/features/auth/application/useAuth';

export function AppShell() {
  const { user, logout } = useAuth();

  return (
    <div className="flex min-h-svh flex-col bg-background">
      <header className="sticky top-0 z-10 flex items-center justify-between border-b border-border bg-card px-4 py-3 sm:px-6">
        <span className="text-lg font-semibold text-primary">KineticOs</span>
        <div className="flex items-center gap-3">
          <span className="hidden text-sm text-muted-foreground sm:inline">{user?.email}</span>
          <Button variant="ghost" size="icon" aria-label="Cerrar sesión" onClick={logout}>
            <LogOut />
          </Button>
        </div>
      </header>
      <main className="flex-1 px-4 py-6 sm:px-6">
        <Outlet />
      </main>
    </div>
  );
}
