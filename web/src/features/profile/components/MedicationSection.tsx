import { useState } from 'react';
import { Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
import { ConfirmDeleteDialog } from '@/components/ConfirmDeleteDialog';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { extractErrorMessage } from '@/core/api/apiError';
import type { UserMedication } from '../domain/profileTypes';
import { useProfile } from '../application/useProfile';

export function MedicationSection({ medications }: { medications: UserMedication[] }) {
  const { addMedication, deleteMedication } = useProfile();
  const [open, setOpen] = useState(false);
  const [name, setName] = useState('');
  const [dosage, setDosage] = useState('');
  const [schedule, setSchedule] = useState('');
  const [pendingDelete, setPendingDelete] = useState<UserMedication | null>(null);

  function handleAdd() {
    if (!name.trim()) return;
    addMedication.mutate(
      { medicationName: name.trim(), dosage: dosage.trim() || null, schedule: schedule.trim() || null },
      {
        onSuccess: () => {
          setOpen(false);
          setName('');
          setDosage('');
          setSchedule('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Accordion type="single" collapsible className="rounded-xl border border-border bg-card px-4">
      <AccordionItem value="medications" className="border-none">
        <AccordionTrigger>Medicación ({medications.length})</AccordionTrigger>
        <AccordionContent className="flex flex-col gap-2">
          {medications.map((item) => (
            <div key={item.id} className="flex items-center justify-between rounded-lg border border-border px-3 py-2">
              <div>
                <p className="text-sm font-medium">{item.medicationName}</p>
                {item.schedule && <p className="text-xs text-muted-foreground">{item.schedule}</p>}
              </div>
              <Button variant="ghost" size="icon-sm" aria-label="Borrar medicación" onClick={() => setPendingDelete(item)}>
                <Trash2 />
              </Button>
            </div>
          ))}
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button variant="outline" className="self-start">
                Agregar medicación
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nueva medicación</DialogTitle>
              </DialogHeader>
              <div className="flex flex-col gap-3">
                <Input placeholder="Nombre" value={name} onChange={(e) => setName(e.target.value)} />
                <Input placeholder="Dosis (opcional)" value={dosage} onChange={(e) => setDosage(e.target.value)} />
                <Input
                  placeholder="Frecuencia (opcional)"
                  value={schedule}
                  onChange={(e) => setSchedule(e.target.value)}
                />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="ghost">Cancelar</Button>
                </DialogClose>
                <Button onClick={handleAdd} disabled={addMedication.isPending}>
                  Agregar
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
          <ConfirmDeleteDialog
            open={pendingDelete != null}
            onOpenChange={(next) => !next && setPendingDelete(null)}
            title="Borrar medicación"
            message={`¿Seguro que querés borrar "${pendingDelete?.medicationName ?? ''}"? Esta acción no se puede deshacer.`}
            pending={deleteMedication.isPending}
            onConfirm={() => {
              if (!pendingDelete) return;
              deleteMedication.mutate(pendingDelete.id, {
                onSuccess: () => setPendingDelete(null),
                onError: (error) => toast.error(extractErrorMessage(error)),
              });
            }}
          />
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
}
