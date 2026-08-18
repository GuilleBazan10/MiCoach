import { useEffect } from 'react';
import { useBlocker } from 'react-router-dom';

/**
 * Advierte antes de perder cambios sin guardar: beforeunload (cerrar/recargar
 * la pestaña) + bloqueo de navegación interna de React Router (back, links del
 * nav). docs/06-ux-ui-audit.md §15 — WorkoutForm/MealPlanForm no tenían nada
 * de esto y podían perder varios minutos de carga manual con un click.
 */
export function useUnsavedChangesGuard(isDirty: boolean) {
  useEffect(() => {
    if (!isDirty) return;
    function handler(e: BeforeUnloadEvent) {
      e.preventDefault();
    }
    window.addEventListener('beforeunload', handler);
    return () => window.removeEventListener('beforeunload', handler);
  }, [isDirty]);

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) => isDirty && currentLocation.pathname !== nextLocation.pathname,
  );

  useEffect(() => {
    if (blocker.state !== 'blocked') return;
    if (window.confirm('Tenés cambios sin guardar. ¿Seguro que querés salir?')) {
      blocker.proceed();
    } else {
      blocker.reset();
    }
  }, [blocker]);
}
