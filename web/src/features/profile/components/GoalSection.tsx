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
import { GOAL_TYPE_OPTIONS } from '../domain/profileLabels';
import type { UserGoal } from '../domain/profileTypes';
import { useProfile } from '../application/useProfile';

export function GoalSection({ goals }: { goals: UserGoal[] }) {
  const { addGoal, deleteGoal } = useProfile();
  const [open, setOpen] = useState(false);
  const [goalType, setGoalType] = useState(Object.keys(GOAL_TYPE_OPTIONS)[0]);
  const [targetValue, setTargetValue] = useState('');
  const [targetUnit, setTargetUnit] = useState('');
  const [pendingDelete, setPendingDelete] = useState<UserGoal | null>(null);

  function handleAdd() {
    addGoal.mutate(
      {
        goalType,
        targetValue: targetValue.trim() ? Number(targetValue.replace(',', '.')) : null,
        targetUnit: targetUnit.trim() || null,
      },
      {
        onSuccess: () => {
          setOpen(false);
          setTargetValue('');
          setTargetUnit('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Accordion type="single" collapsible className="rounded-xl border border-border bg-card px-4">
      <AccordionItem value="goals" className="border-none">
        <AccordionTrigger>Objetivos ({goals.length})</AccordionTrigger>
        <AccordionContent className="flex flex-col gap-2">
          {goals.map((goal) => (
            <div key={goal.id} className="flex items-center justify-between rounded-lg border border-border px-3 py-2">
              <div>
                <p className="text-sm font-medium">{GOAL_TYPE_OPTIONS[goal.goalType] ?? goal.goalType}</p>
                {goal.targetValue != null && (
                  <p className="text-xs text-muted-foreground">
                    Meta: {goal.targetValue} {goal.targetUnit ?? ''}
                  </p>
                )}
              </div>
              <Button variant="ghost" size="icon-sm" aria-label="Borrar objetivo" onClick={() => setPendingDelete(goal)}>
                <Trash2 />
              </Button>
            </div>
          ))}
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button variant="outline" className="self-start">
                Agregar objetivo
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Nuevo objetivo</DialogTitle>
              </DialogHeader>
              <div className="flex flex-col gap-3">
                <OptionSelect label="Tipo" value={goalType} onChange={setGoalType} options={GOAL_TYPE_OPTIONS} />
                <div className="flex flex-col gap-1.5">
                  <Input
                    placeholder="Valor objetivo (opcional)"
                    inputMode="decimal"
                    value={targetValue}
                    onChange={(e) => setTargetValue(e.target.value)}
                  />
                </div>
                <Input
                  placeholder="Unidad (kg, cm, min...)"
                  value={targetUnit}
                  onChange={(e) => setTargetUnit(e.target.value)}
                />
              </div>
              <DialogFooter>
                <DialogClose asChild>
                  <Button variant="ghost">Cancelar</Button>
                </DialogClose>
                <Button onClick={handleAdd} disabled={addGoal.isPending}>
                  Agregar
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
          <ConfirmDeleteDialog
            open={pendingDelete != null}
            onOpenChange={(next) => !next && setPendingDelete(null)}
            title="Borrar objetivo"
            message={`¿Seguro que querés borrar "${pendingDelete ? (GOAL_TYPE_OPTIONS[pendingDelete.goalType] ?? pendingDelete.goalType) : ''}"? Esta acción no se puede deshacer.`}
            pending={deleteGoal.isPending}
            onConfirm={() => {
              if (!pendingDelete) return;
              deleteGoal.mutate(pendingDelete.id, {
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
