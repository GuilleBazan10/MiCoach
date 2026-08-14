import { useState } from 'react';
import { Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Button } from '@/components/ui/button';
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
import type { UserPathology } from '../domain/profileTypes';
import { useProfile } from '../application/useProfile';

export function PathologySection({ pathologies }: { pathologies: UserPathology[] }) {
  const { addPathology, deletePathology } = useProfile();
  const [open, setOpen] = useState(false);
  const [pathology, setPathology] = useState('');
  const [notes, setNotes] = useState('');

  function handleAdd() {
    if (!pathology.trim()) return;
    addPathology.mutate(
      { pathology: pathology.trim(), notes: notes.trim() || null },
      {
        onSuccess: () => {
          setOpen(false);
          setPathology('');
          setNotes('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Accordion type="single" collapsible className="rounded-xl border border-border bg-card px-4">
      <AccordionItem value="pathologies" className="border-none">
        <AccordionTrigger>Patologías ({pathologies.length})</AccordionTrigger>
        <AccordionContent className="flex flex-col gap-2">
          {pathologies.map((item) => (
            <div key={item.id} className="flex items-center justify-between rounded-lg border border-border px-3 py-2">
              <div>
                <p className="text-sm font-medium">{item.pathology}</p>
                {item.notes && <p className="text-xs text-muted-foreground">{item.notes}</p>}
              </div>
              <Button
                variant="ghost"
                size="icon-sm"
                aria-label="Borrar patología"
                onClick={() =>
                  deletePathology.mutate(item.id, { onError: (error) => toast.error(extractErrorMessage(error)) })
                }
              >
                <Trash2 />
              </Button>
            </div>
          ))}
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button variant="outline" className="self-start">
                Agregar patología
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nueva patología</DialogTitle>
              </DialogHeader>
              <div className="flex flex-col gap-3">
                <Input placeholder="Patología" value={pathology} onChange={(e) => setPathology(e.target.value)} />
                <Input placeholder="Notas (opcional)" value={notes} onChange={(e) => setNotes(e.target.value)} />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="ghost">Cancelar</Button>
                </DialogClose>
                <Button onClick={handleAdd} disabled={addPathology.isPending}>
                  Agregar
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  );
}
