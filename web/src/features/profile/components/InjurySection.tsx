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
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import { INJURY_STATUS_OPTIONS } from '../domain/profileLabels';
import type { UserInjury } from '../domain/profileTypes';
import { useProfile } from '../application/useProfile';

export function InjurySection({ injuries }: { injuries: UserInjury[] }) {
  const { addInjury, deleteInjury } = useProfile();
  const [open, setOpen] = useState(false);
  const [bodyPart, setBodyPart] = useState('');
  const [injuryType, setInjuryType] = useState('');
  const [status, setStatus] = useState('active');
  const [pendingDelete, setPendingDelete] = useState<UserInjury | null>(null);

  function handleAdd() {
    if (!bodyPart.trim() || !injuryType.trim()) return;
    addInjury.mutate(
      { bodyPart: bodyPart.trim(), injuryType: injuryType.trim(), status },
      {
        onSuccess: () => {
          setOpen(false);
          setBodyPart('');
          setInjuryType('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Accordion type="single" collapsible className="rounded-xl border border-border bg-card px-4">
      <AccordionItem value="injuries" className="border-none">
        <AccordionTrigger>Lesiones ({injuries.length})</AccordionTrigger>
        <AccordionContent className="flex flex-col gap-2">
          {injuries.map((item) => (
            <div key={item.id} className="flex items-center justify-between rounded-lg border border-border px-3 py-2">
              <div>
                <p className="text-sm font-medium">
                  {item.bodyPart} — {item.injuryType}
                </p>
                <p className="text-xs text-muted-foreground">
                  {(item.status && INJURY_STATUS_OPTIONS[item.status]) ?? item.status ?? ''}
                </p>
              </div>
              <Button variant="ghost" size="icon-sm" aria-label="Borrar lesión" onClick={() => setPendingDelete(item)}>
                <Trash2 />
              </Button>
            </div>
          ))}
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button variant="outline" className="self-start">
                Agregar lesión
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nueva lesión</DialogTitle>
              </DialogHeader>
              <div className="flex flex-col gap-3">
                <Input placeholder="Zona (ej: rodilla)" value={bodyPart} onChange={(e) => setBodyPart(e.target.value)} />
                <Input placeholder="Tipo de lesión" value={injuryType} onChange={(e) => setInjuryType(e.target.value)} />
                <OptionSelect label="Estado" value={status} onChange={setStatus} options={INJURY_STATUS_OPTIONS} />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="ghost">Cancelar</Button>
                </DialogClose>
                <Button onClick={handleAdd} disabled={addInjury.isPending}>
                  Agregar
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
          <ConfirmDeleteDialog
            open={pendingDelete != null}
            onOpenChange={(next) => !next && setPendingDelete(null)}
            title="Borrar lesión"
            message={`¿Seguro que querés borrar "${pendingDelete ? `${pendingDelete.bodyPart} — ${pendingDelete.injuryType}` : ''}"? Esta acción no se puede deshacer.`}
            pending={deleteInjury.isPending}
            onConfirm={() => {
              if (!pendingDelete) return;
              deleteInjury.mutate(pendingDelete.id, {
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
