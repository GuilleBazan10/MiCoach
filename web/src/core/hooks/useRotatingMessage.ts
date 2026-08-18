import { useEffect, useState } from 'react';

/**
 * Rota entre `messages` cada `intervalMs` mientras `active` es true (pensado para
 * generación con IA de hasta 3 min — docs/06-ux-ui-audit.md §3.1: reduce la
 * percepción de tiempo muerto sin necesitar progreso real del backend).
 * `messages` debe ser una referencia estable (constante de módulo).
 */
export function useRotatingMessage(messages: readonly string[], active: boolean, intervalMs = 15000): string {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (!active) {
      setIndex(0);
      return;
    }
    const id = setInterval(() => setIndex((i) => (i + 1) % messages.length), intervalMs);
    return () => clearInterval(id);
  }, [active, messages, intervalMs]);

  return messages[index];
}
