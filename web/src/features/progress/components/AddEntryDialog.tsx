import { useState } from 'react';
import type { ReactNode } from 'react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import { METRIC_TYPE_DEFAULT_UNIT, METRIC_TYPE_LABELS } from '../domain/progressLabels';
import { useAddEntry } from '../application/mutations';

export function AddEntryDialog({ initialMetricType, trigger }: { initialMetricType?: string; trigger: ReactNode }) {
  const addEntry = useAddEntry();
  const [open, setOpen] = useState(false);
  const [metricType, setMetricType] = useState(initialMetricType ?? Object.keys(METRIC_TYPE_LABELS)[0]);
  const [unit, setUnit] = useState(METRIC_TYPE_DEFAULT_UNIT[metricType] ?? '');
  const [value, setValue] = useState('');
  const [notes, setNotes] = useState('');

  function handleMetricChange(next: string) {
    setMetricType(next);
    setUnit(METRIC_TYPE_DEFAULT_UNIT[next] ?? unit);
  }

  function handleSubmit() {
    const numericValue = Number(value.replace(',', '.'));
    if (Number.isNaN(numericValue) || value.trim() === '') {
      toast.error('Ingresá un valor numérico válido.');
      return;
    }
    if (!unit.trim()) {
      toast.error('Ingresá la unidad de la métrica.');
      return;
    }
    addEntry.mutate(
      { metricType, value: numericValue, unit: unit.trim(), notes: notes.trim() || null },
      {
        onSuccess: () => {
          setOpen(false);
          setValue('');
          setNotes('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Registrar métrica</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-3">
          <OptionSelect label="Métrica" value={metricType} onChange={handleMetricChange} options={METRIC_TYPE_LABELS} />
          <div>
            <Label>Valor</Label>
            <Input inputMode="decimal" value={value} onChange={(e) => setValue(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Unidad</Label>
            <Input value={unit} onChange={(e) => setUnit(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Notas (opcional)</Label>
            <Input value={notes} onChange={(e) => setNotes(e.target.value)} className="mt-1.5" />
          </div>
        </div>
        <DialogFooter>
          <Button variant="ghost" onClick={() => setOpen(false)}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} disabled={addEntry.isPending}>
            Registrar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
