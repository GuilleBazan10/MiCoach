// =====================================================================
// KineticOs — Diálogo para pedirle a la IA un sustituto de un ingrediente
// (alergia/intolerancia/no disponible/preferencia). Se abre desde el detalle
// de una receta, por ingrediente.
// =====================================================================
import { useState } from 'react';
import { Loader2, Sparkles } from 'lucide-react';
import { toast } from 'sonner';
import { OptionSelect } from '@/components/option-select';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { extractErrorMessage } from '@/core/api/apiError';
import { useGenerateSubstitution } from '../application/mutations';
import { SUBSTITUTION_REASON_LABELS } from '../domain/nutritionLabels';
import type { Substitution } from '../domain/nutritionTypes';

export function SubstituteIngredientDialog({
  ingredientId,
  ingredientName,
  open,
  onOpenChange,
}: {
  ingredientId: number;
  ingredientName: string;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const [reason, setReason] = useState('allergy');
  const [notes, setNotes] = useState('');
  const [result, setResult] = useState<Substitution | null>(null);
  const generate = useGenerateSubstitution(ingredientId);

  function handleGenerate() {
    setResult(null);
    generate.mutate(
      { reason, notes: notes || undefined },
      {
        onSuccess: (substitution) => setResult(substitution),
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  function handleClose(next: boolean) {
    if (!next) {
      setResult(null);
      setNotes('');
    }
    onOpenChange(next);
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Sustituir "{ingredientName}"</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-3">
          <OptionSelect
            label="Motivo"
            value={reason}
            onChange={setReason}
            options={SUBSTITUTION_REASON_LABELS}
            placeholder="Elegí un motivo"
          />
          <div className="flex flex-col gap-1.5">
            <Textarea
              placeholder="Detalle opcional (ej: alergia al maní, no como lácteos...)"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={2}
            />
          </div>

          {result && (
            <div className="rounded-lg bg-primary/10 p-3">
              <p className="flex items-center gap-1.5 text-sm font-medium text-primary">
                <Sparkles className="size-3.5" /> {result.substituteIngredientName}
              </p>
              {result.notes && <p className="mt-1 text-sm text-muted-foreground">{result.notes}</p>}
            </div>
          )}
        </div>

        <DialogFooter>
          <Button onClick={handleGenerate} disabled={generate.isPending}>
            {generate.isPending && <Loader2 className="animate-spin" />}
            {result ? 'Probar otro' : 'Generar sustituto'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
