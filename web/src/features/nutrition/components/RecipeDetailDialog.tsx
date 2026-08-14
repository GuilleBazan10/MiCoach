// =====================================================================
// KineticOs — Detalle de una receta: ingredientes (con opción de sustituir
// cada uno con IA), instrucciones y macros. Se abre al tocar el nombre de
// una receta. Equivalente a ExerciseDetailDialog para el módulo workout.
// =====================================================================
import { useState } from 'react';
import { ImageOff, Repeat } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { RECIPE_DIFFICULTY_LABELS, labelFor } from '../domain/nutritionLabels';
import type { Recipe } from '../domain/nutritionTypes';
import { SubstituteIngredientDialog } from './SubstituteIngredientDialog';

export function RecipeDetailDialog({
  recipe,
  open,
  onOpenChange,
}: {
  recipe: Recipe | undefined;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const [substituting, setSubstituting] = useState<{ id: number; name: string } | null>(null);

  if (!recipe) return null;

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>{recipe.name}</DialogTitle>
          </DialogHeader>

          <div className="flex flex-col gap-3">
            <div className="flex flex-wrap gap-2">
              {recipe.difficulty && <Badge variant="secondary">{labelFor(RECIPE_DIFFICULTY_LABELS, recipe.difficulty)}</Badge>}
              {recipe.caloriesPerServing != null && <Badge variant="secondary">{recipe.caloriesPerServing} kcal/porción</Badge>}
              {recipe.prepTimeMin != null && <Badge variant="outline">{recipe.prepTimeMin + (recipe.cookTimeMin ?? 0)} min</Badge>}
            </div>

            {recipe.imageUrl ? (
              <img src={recipe.imageUrl} alt={recipe.name} className="w-full rounded-lg object-cover" />
            ) : (
              <div className="flex h-28 flex-col items-center justify-center gap-1 rounded-lg bg-muted text-muted-foreground">
                <ImageOff className="size-6" />
                <span className="text-xs">Todavía no hay imagen de referencia</span>
              </div>
            )}

            <div>
              <p className="mb-1.5 text-sm font-medium">Ingredientes</p>
              <div className="flex flex-col gap-1">
                {recipe.ingredients.map((ing) => (
                  <div key={ing.ingredientId} className="flex items-center justify-between gap-2 text-sm">
                    <span className="min-w-0 truncate">
                      {ing.ingredientName} <span className="text-muted-foreground">· {ing.amount}{ing.unit}</span>
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-7 shrink-0 px-2 text-xs"
                      onClick={() => setSubstituting({ id: ing.ingredientId, name: ing.ingredientName })}
                    >
                      <Repeat className="size-3" /> Sustituir
                    </Button>
                  </div>
                ))}
              </div>
            </div>

            {recipe.instructions && (
              <div>
                <p className="mb-1 text-sm font-medium">Preparación</p>
                <p className="text-sm text-muted-foreground">{recipe.instructions}</p>
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {substituting && (
        <SubstituteIngredientDialog
          ingredientId={substituting.id}
          ingredientName={substituting.name}
          open={!!substituting}
          onOpenChange={(open) => !open && setSubstituting(null)}
        />
      )}
    </>
  );
}
