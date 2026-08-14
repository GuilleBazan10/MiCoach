import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
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
import { cn } from '@/lib/utils';
import { extractErrorMessage } from '@/core/api/apiError';
import { useShoppingListDetail } from '../application/queries';
import {
  useAddShoppingListItem,
  useDeleteShoppingList,
  useDeleteShoppingListItem,
  useSetItemChecked,
} from '../application/mutations';

export function ShoppingListDetailPage() {
  const params = useParams();
  const shoppingListId = Number(params.id);
  const navigate = useNavigate();
  const { data: list, isLoading, isError } = useShoppingListDetail(shoppingListId);
  const addItem = useAddShoppingListItem(shoppingListId);
  const setItemChecked = useSetItemChecked(shoppingListId);
  const deleteItem = useDeleteShoppingListItem(shoppingListId);
  const deleteList = useDeleteShoppingList();

  const [addOpen, setAddOpen] = useState(false);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [itemName, setItemName] = useState('');
  const [amount, setAmount] = useState('');
  const [unit, setUnit] = useState('');

  function handleAddItem() {
    if (!itemName.trim()) return;
    addItem.mutate(
      { itemName: itemName.trim(), amount: amount ? Number(amount.replace(',', '.')) : null, unit: unit.trim() || null },
      {
        onSuccess: () => {
          setAddOpen(false);
          setItemName('');
          setAmount('');
          setUnit('');
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  function handleDeleteList() {
    deleteList.mutate(shoppingListId, {
      onSuccess: () => navigate('/nutrition'),
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError || !list) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar la lista.</p>;
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-4 pb-12">
      <div className="flex items-start justify-between gap-3">
        <h1 className="text-xl font-semibold">{list.name}</h1>
        <Button variant="ghost" size="icon" aria-label="Borrar lista" onClick={() => setConfirmDeleteOpen(true)}>
          <Trash2 />
        </Button>
      </div>

      {list.items.length === 0 && <p className="text-sm text-muted-foreground">Todavía no agregaste ítems.</p>}
      <div className="flex flex-col gap-1">
        {list.items.map((item) => (
          <div key={item.id} className="flex items-center gap-3 rounded-lg border border-border px-3 py-2">
            <Checkbox
              checked={item.checked}
              onCheckedChange={(checked) =>
                setItemChecked.mutate(
                  { itemId: item.id, checked: checked === true },
                  { onError: (error) => toast.error(extractErrorMessage(error)) },
                )
              }
            />
            <div className="flex-1">
              <p className={cn('text-sm', item.checked && 'text-muted-foreground line-through')}>
                {item.itemName ?? `Ítem #${item.id}`}
              </p>
              {(item.amount != null || item.unit) && (
                <p className="text-xs text-muted-foreground">
                  {item.amount ?? ''} {item.unit ?? ''}
                </p>
              )}
            </div>
            <Button
              variant="ghost"
              size="icon-sm"
              aria-label="Borrar ítem"
              onClick={() => deleteItem.mutate(item.id, { onError: (error) => toast.error(extractErrorMessage(error)) })}
            >
              <Trash2 className="size-4" />
            </Button>
          </div>
        ))}
      </div>

      <Dialog open={addOpen} onOpenChange={setAddOpen}>
        <DialogTrigger asChild>
          <Button size="sm" className="self-start">
            <Plus /> Agregar ítem
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Agregar ítem</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col gap-3">
            <Input placeholder="Ítem" value={itemName} onChange={(e) => setItemName(e.target.value)} />
            <Input placeholder="Cantidad (opcional)" inputMode="decimal" value={amount} onChange={(e) => setAmount(e.target.value)} />
            <Input placeholder="Unidad (opcional)" value={unit} onChange={(e) => setUnit(e.target.value)} />
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="ghost">Cancelar</Button>
            </DialogClose>
            <Button onClick={handleAddItem} disabled={addItem.isPending}>
              Agregar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={confirmDeleteOpen} onOpenChange={setConfirmDeleteOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Borrar lista</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">¿Seguro que querés borrar "{list.name}"?</p>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="ghost">Cancelar</Button>
            </DialogClose>
            <Button variant="destructive" onClick={handleDeleteList} disabled={deleteList.isPending}>
              Borrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
