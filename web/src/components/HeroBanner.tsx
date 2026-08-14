// =====================================================================
// KineticOs — Banner de cabecera con gradiente de marca para las pantallas
// home (Rutinas/Nutrición/Progreso). Reemplaza el <h1> plano por algo con
// más presencia visual + chips de stats rápidas opcionales.
// =====================================================================
import type { ReactNode } from 'react';

export interface HeroStat {
  label: string;
  value: string | number;
}

export function HeroBanner({
  icon,
  title,
  subtitle,
  stats,
}: {
  icon: ReactNode;
  title: string;
  subtitle?: string;
  stats?: HeroStat[];
}) {
  return (
    <div
      className="relative overflow-hidden rounded-2xl px-5 py-5 text-primary-foreground shadow-sm sm:px-6 sm:py-6"
      style={{ background: 'var(--gradient-hero)' }}
    >
      <div className="pointer-events-none absolute -top-8 -right-8 size-32 rounded-full bg-white/10" />
      <div className="pointer-events-none absolute -bottom-10 -right-16 size-40 rounded-full bg-white/5" />
      <div className="relative flex items-center gap-3">
        <span className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-white/15">{icon}</span>
        <div className="min-w-0">
          <h1 className="text-xl font-bold">{title}</h1>
          {subtitle && <p className="text-sm text-primary-foreground/85">{subtitle}</p>}
        </div>
      </div>
      {stats && stats.length > 0 && (
        <div className="relative mt-4 flex flex-wrap gap-2">
          {stats.map((stat) => (
            <div key={stat.label} className="rounded-lg bg-white/15 px-3 py-1.5 backdrop-blur-sm">
              <span className="text-sm font-semibold">{stat.value}</span>{' '}
              <span className="text-xs text-primary-foreground/85">{stat.label}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
