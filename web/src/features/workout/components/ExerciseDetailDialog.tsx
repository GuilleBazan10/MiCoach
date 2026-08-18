// =====================================================================
// MiCoach — Detalle de un ejercicio: cómo se hace (imagen/video/instrucciones).
// Se abre al tocar el nombre de un ejercicio en una rutina o sesión.
// =====================================================================
import { ImageOff, Video } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { CATEGORY_LABELS, DIFFICULTY_LABELS, labelFor } from '../domain/workoutLabels';
import type { Exercise } from '../domain/workoutTypes';

export function ExerciseDetailDialog({
  exercise,
  open,
  onOpenChange,
}: {
  exercise: Exercise | undefined;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  if (!exercise) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>{exercise.name}</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-3">
          <div className="flex flex-wrap gap-2">
            <Badge variant="secondary">{labelFor(CATEGORY_LABELS, exercise.category)}</Badge>
            <Badge variant="secondary">{labelFor(DIFFICULTY_LABELS, exercise.difficulty)}</Badge>
            {exercise.equipment.map((eq) => (
              <Badge key={eq} variant="outline">
                {eq}
              </Badge>
            ))}
          </div>

          {exercise.imageUrl || exercise.imageUrlEnd ? (
            <div className="grid grid-cols-2 gap-2">
              <ExercisePositionImage url={exercise.imageUrl} label="Posición inicial" name={exercise.name} />
              <ExercisePositionImage url={exercise.imageUrlEnd} label="Posición final" name={exercise.name} />
            </div>
          ) : (
            <div className="flex h-32 flex-col items-center justify-center gap-1 rounded-lg bg-muted text-muted-foreground">
              <ImageOff className="size-6" />
              <span className="text-xs">Todavía no hay imagen de referencia</span>
            </div>
          )}

          {exercise.muscles.length > 0 && (
            <p className="text-sm text-muted-foreground">
              <span className="font-medium text-foreground">Músculos: </span>
              {exercise.muscles
                .filter((m) => m.role === 'primary')
                .map((m) => m.muscleName)
                .join(', ')}
              {exercise.muscles.some((m) => m.role !== 'primary') && (
                <>
                  {exercise.muscles.some((m) => m.role === 'primary') && ' · '}
                  <span className="italic">
                    {exercise.muscles
                      .filter((m) => m.role !== 'primary')
                      .map((m) => m.muscleName)
                      .join(', ')}{' '}
                    (secundarios)
                  </span>
                </>
              )}
            </p>
          )}

          {exercise.videoUrl ? (
            <a
              href={exercise.videoUrl}
              target="_blank"
              rel="noreferrer"
              className="flex items-center gap-2 text-sm text-primary hover:underline"
            >
              <Video className="size-4" /> Ver video de demostración
            </a>
          ) : (
            <span className="flex items-center gap-2 text-sm text-muted-foreground">
              <Video className="size-4" /> Todavía no hay video de demostración
            </span>
          )}

          {exercise.instructions ? (
            <div>
              <p className="mb-1 text-sm font-medium">Cómo hacerlo</p>
              <p className="text-sm text-muted-foreground">{exercise.instructions}</p>
            </div>
          ) : (
            <p className="text-sm text-muted-foreground">Sin instrucciones cargadas todavía.</p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

function ExercisePositionImage({ url, label, name }: { url?: string | null; label: string; name: string }) {
  return (
    <div className="flex flex-col gap-1">
      {url ? (
        <img src={url} alt={`${name} — ${label}`} className="aspect-square w-full rounded-lg object-cover" />
      ) : (
        <div className="flex aspect-square w-full flex-col items-center justify-center gap-1 rounded-lg bg-muted text-muted-foreground">
          <ImageOff className="size-5" />
        </div>
      )}
      <p className="text-center text-xs text-muted-foreground">{label}</p>
    </div>
  );
}
