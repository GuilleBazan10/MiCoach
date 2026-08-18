// =====================================================================
// MiCoach — Shell de la app autenticada, con navegación entre las 4
// secciones de usuario final (equivalente al bottom nav de mobile:
// mobile/lib/core/router/app_shell.dart). Mobile-first: barra fija abajo
// en pantallas chicas, nav inline en el header a partir de `sm`.
// =====================================================================
import { NavLink, Outlet } from 'react-router-dom';
import { Dumbbell, LogOut, Settings, TrendingUp, User, Utensils } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/Logo';
import { APP_NAME } from '@/core/config';
import { useAuth } from '@/features/auth/application/useAuth';

const NAV_ITEMS = [
  { to: '/workouts', label: 'Rutinas', icon: Dumbbell },
  { to: '/nutrition', label: 'Nutrición', icon: Utensils },
  { to: '/progress', label: 'Progreso', icon: TrendingUp },
  { to: '/profile', label: 'Perfil', icon: User },
];

export function AppShell() {
  const { user, logout } = useAuth();
  const isAdmin = user?.roles.includes('ROLE_ADMIN') ?? false;
  const navItems = isAdmin ? [...NAV_ITEMS, { to: '/admin/ai', label: 'Admin IA', icon: Settings }] : NAV_ITEMS;

  return (
    <div className="flex min-h-svh flex-col bg-background">
      <header className="sticky top-0 z-10 border-b border-border bg-card">
        <div
          className="h-[3px] w-full"
          style={{ background: 'var(--gradient-hero)' }}
          aria-hidden="true"
        />
        <div className="flex items-center justify-between px-4 py-2.5 sm:px-6">
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2">
              <Logo className="size-8 shrink-0" />
              <span className="text-lg font-bold tracking-tight text-foreground">{APP_NAME}</span>
            </div>
            <nav className="hidden items-center gap-1 sm:flex">
              {navItems.map((item) => (
                <NavItem key={item.to} {...item} />
              ))}
            </nav>
          </div>
          <div className="flex items-center gap-3">
            <span className="hidden text-sm text-muted-foreground md:inline">{user?.email}</span>
            <Button variant="ghost" size="icon" aria-label="Cerrar sesión" onClick={logout}>
              <LogOut />
            </Button>
          </div>
        </div>
      </header>

      <main className="flex-1 px-4 py-6 pb-20 sm:px-6 sm:pb-6">
        <Outlet />
      </main>

      <nav className="fixed inset-x-0 bottom-0 z-10 flex items-center justify-around border-t border-border bg-card py-1.5 sm:hidden">
        {navItems.map((item) => (
          <NavItem key={item.to} {...item} compact />
        ))}
      </nav>
    </div>
  );
}

function NavItem({ to, label, icon: Icon, compact }: { to: string; label: string; icon: typeof Dumbbell; compact?: boolean }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        cn(
          'flex items-center gap-1.5 rounded-full text-sm font-medium transition-colors',
          compact ? 'flex-col gap-0.5 rounded-lg px-3 py-1 text-xs' : 'px-3.5 py-1.5',
          isActive
            ? compact
              ? 'text-primary'
              : 'bg-primary/10 text-primary'
            : 'text-muted-foreground hover:bg-muted hover:text-foreground',
        )
      }
    >
      <Icon className={compact ? 'size-5' : 'size-4'} />
      {label}
    </NavLink>
  );
}
