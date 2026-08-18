// =====================================================================
// MiCoach — Gráfico de línea simple de la evolución de una métrica.
// docs/10-recomendaciones-coach-nutricion.md § F.1: el valor real de
// registrar progreso es la tendencia, no el dato puntual — sin esto la
// sección "Progreso" guarda datos pero no ayuda a decidir nada con ellos.
// SVG a mano (sin librería nueva) — alcanza para una sola serie.
// =====================================================================
import type { ProgressEntry } from '../domain/progressTypes';

const dateFormatter = new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: '2-digit' });

export function MetricChart({ entries, unit }: { entries: ProgressEntry[]; unit: string }) {
  const sorted = [...entries].sort((a, b) => new Date(a.measuredAt).getTime() - new Date(b.measuredAt).getTime());
  if (sorted.length < 2) return null;

  const width = 100;
  const height = 36;
  const pad = 4;

  const values = sorted.map((e) => e.value);
  const min = Math.min(...values);
  const max = Math.max(...values);
  const range = max - min || 1;

  const points = sorted.map((e, i) => ({
    x: pad + (i / (sorted.length - 1)) * (width - pad * 2),
    y: height - pad - ((e.value - min) / range) * (height - pad * 2),
  }));

  const first = sorted[0];
  const last = sorted[sorted.length - 1];
  const delta = last.value - first.value;

  return (
    <div className="rounded-lg border border-border p-3">
      <svg viewBox={`0 0 ${width} ${height}`} className="h-28 w-full" preserveAspectRatio="none">
        <polyline
          points={points.map((p) => `${p.x},${p.y}`).join(' ')}
          fill="none"
          stroke="var(--primary)"
          strokeWidth="1.5"
          strokeLinejoin="round"
          strokeLinecap="round"
          vectorEffect="non-scaling-stroke"
        />
        {points.map((p, i) => (
          <circle key={i} cx={p.x} cy={p.y} r="1.5" fill="var(--primary)" vectorEffect="non-scaling-stroke" />
        ))}
      </svg>
      <div className="mt-1 flex items-center justify-between text-xs text-muted-foreground">
        <span>
          {dateFormatter.format(new Date(first.measuredAt))} · {first.value} {unit}
        </span>
        <span className={delta === 0 ? '' : delta > 0 ? 'font-medium text-highlight' : 'font-medium text-primary'}>
          {delta > 0 ? '+' : ''}
          {delta.toFixed(1)} {unit}
        </span>
        <span>
          {dateFormatter.format(new Date(last.measuredAt))} · {last.value} {unit}
        </span>
      </div>
    </div>
  );
}
