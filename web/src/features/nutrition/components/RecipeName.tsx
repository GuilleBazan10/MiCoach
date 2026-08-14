// Resuelve y muestra el nombre de una receta por id. Paridad con recipe_name_text.dart.
// Es clickeable: abre el detalle (ingredientes/instrucciones/sustituciones) de la receta.
import { useState } from 'react';
import { useRecipe } from '../application/queries';
import { RecipeDetailDialog } from './RecipeDetailDialog';

export function RecipeName({ recipeId, className }: { recipeId?: number | null; className?: string }) {
  const { data, isLoading } = useRecipe(recipeId);
  const [open, setOpen] = useState(false);

  if (recipeId == null) return <span className={className}>Comida libre</span>;

  return (
    <>
      <button
        type="button"
        className={`text-left underline-offset-2 hover:underline ${className ?? ''}`}
        onClick={() => setOpen(true)}
        disabled={!data}
      >
        {isLoading ? 'Cargando…' : (data?.name ?? `Receta #${recipeId}`)}
      </button>
      <RecipeDetailDialog recipe={data} open={open} onOpenChange={setOpen} />
    </>
  );
}
