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

          {exercise.imageUrl ? (
            <img src={exercise.imageUrl} alt={exercise.name} className="w-full rounded-lg object-cover" />
          ) : (
            <div className="flex h-32 flex-col items-center justify-center gap-1 rounded-lg bg-muted text-muted-foreground">
              <ImageOff className="size-6" />
              <span className="text-xs">Todavía no hay imagen de referencia</span>
            </div>
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
