// Diálogo para buscar y elegir un ejercicio del catálogo.
// Paridad con exercise_picker_dialog.dart.
import { useState } from 'react';
import { Search } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useExerciseCatalog } from '../application/queries';
import { CATEGORY_LABELS, DIFFICULTY_LABELS, labelFor } from '../domain/workoutLabels';
import type { Exercise } from '../domain/workoutTypes';
import { ExerciseThumb } from './ExerciseThumb';

interface ExercisePickerDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSelect: (exercise: Exercise) => void;
}

export function ExercisePickerDialog({ open, onOpenChange, onSelect }: ExercisePickerDialogProps) {
  const [search, setSearch] = useState('');
  const { data: exercises, isLoading } = useExerciseCatalog({ search: search || undefined });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="flex max-h-[560px] flex-col sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Elegir ejercicio</DialogTitle>
        </DialogHeader>
        <div className="relative">
          <Search className="absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Buscar"
            className="pl-8"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            autoFocus
          />
        </div>
        <ScrollArea className="h-80">
          {isLoading && <p className="p-4 text-sm text-muted-foreground">Cargando…</p>}
          {!isLoading && exercises?.length === 0 && (
            <p className="p-4 text-sm text-muted-foreground">Sin resultados</p>
          )}
          <div className="flex flex-col">
            {exercises?.map((exercise) => (
              <button
                key={exercise.id}
                type="button"
                className="flex items-center gap-3 rounded-lg px-3 py-2 text-left hover:bg-muted"
                onClick={() => {
                  onSelect(exercise);
                  onOpenChange(false);
                  setSearch('');
                }}
              >
                <ExerciseThumb imageUrl={exercise.imageUrl} category={exercise.category} size="sm" />
                <span className="flex flex-col items-start gap-0.5">
                  <span className="text-sm font-medium">{exercise.name}</span>
                  <span className="text-xs text-muted-foreground">
                    {labelFor(CATEGORY_LABELS, exercise.category)} · {labelFor(DIFFICULTY_LABELS, exercise.difficulty)}
                  </span>
                </span>
              </button>
            ))}
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
