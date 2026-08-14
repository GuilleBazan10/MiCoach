// Diálogo para buscar y elegir una receta del catálogo. Paridad con
// recipe_picker_dialog.dart.
import { useState } from 'react';
import { Search } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useRecipeCatalog } from '../application/queries';
import { MEAL_CATEGORY_LABELS, labelFor } from '../domain/nutritionLabels';
import type { Recipe } from '../domain/nutritionTypes';

interface RecipePickerDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSelect: (recipe: Recipe) => void;
  mealCategory?: string;
}

export function RecipePickerDialog({ open, onOpenChange, onSelect, mealCategory }: RecipePickerDialogProps) {
  const [search, setSearch] = useState('');
  const { data: recipes, isLoading } = useRecipeCatalog({ mealCategory, search: search || undefined });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="flex max-h-[560px] flex-col sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Elegir receta</DialogTitle>
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
          {!isLoading && recipes?.length === 0 && <p className="p-4 text-sm text-muted-foreground">Sin resultados</p>}
          <div className="flex flex-col">
            {recipes?.map((recipe) => (
              <button
                key={recipe.id}
                type="button"
                className="flex flex-col items-start gap-0.5 rounded-lg px-3 py-2 text-left hover:bg-muted"
                onClick={() => {
                  onSelect(recipe);
                  onOpenChange(false);
                  setSearch('');
                }}
              >
                <span className="text-sm font-medium">{recipe.name}</span>
                <span className="text-xs text-muted-foreground">
                  {labelFor(MEAL_CATEGORY_LABELS, recipe.mealCategory)}
                  {recipe.caloriesPerServing != null ? ` · ${Math.round(recipe.caloriesPerServing)} kcal/porción` : ''}
                </span>
              </button>
            ))}
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
