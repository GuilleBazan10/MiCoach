// =====================================================================
// MiCoach — Isotipo de marca (docs/11-identidad-marca.md). `mark` es el
// ícono solo (escudo + figura + hoja, sin wordmark) para usos chicos como
// el header; `full` es el lockup completo con el nombre, para pantallas
// con más espacio (ej. login, splash).
// =====================================================================
import logoFullUrl from '@/assets/brand/logo-full.svg';
import logoMarkUrl from '@/assets/brand/logo-mark.svg';

export function Logo({ variant = 'mark', className }: { variant?: 'mark' | 'full'; className?: string }) {
  const src = variant === 'full' ? logoFullUrl : logoMarkUrl;
  const alt = variant === 'full' ? 'Mi Coach Saludable' : 'Mi Coach';
  return <img src={src} alt={alt} className={className} />;
}
