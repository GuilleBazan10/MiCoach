import { useState } from 'react';
import { ChevronRight, Plus, ShoppingCart } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { EmptyState } from '@/components/EmptyState';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
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
import { useShoppingLists } from '../application/queries';
import { useCreateShoppingList } from '../application/mutations';

export function ShoppingListListView() {
  const { data: lists, isLoading, isError } = useShoppingLists();
  const createList = useCreateShoppingList();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [name, setName] = useState('Lista de la semana');

  function handleCreate() {
    createList.mutate(
      { name: name.trim() || undefined },
      {
        onSuccess: (created) => {
          setOpen(false);
          navigate(`/nutrition/shopping-lists/${created.id}`);
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <div className="flex flex-col gap-3">
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogTrigger asChild>
          <Button size="sm" className="self-start">
            <Plus /> Nueva lista
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Nueva lista</DialogTitle>
          </DialogHeader>
          <Input placeholder="Nombre" value={name} onChange={(e) => setName(e.target.value)} />
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="ghost">Cancelar</Button>
            </DialogClose>
            <Button onClick={handleCreate} disabled={createList.isPending}>
              Crear
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {isLoading && (
        <div className="flex justify-center py-12">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
        </div>
      )}
      {isError && <p className="py-12 text-center text-sm text-muted-foreground">No se pudieron cargar las listas.</p>}
      {!isLoading && lists?.length === 0 && (
        <EmptyState icon={ShoppingCart} message="Todavía no creaste ninguna lista de compras." />
      )}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {lists?.map((list) => {
          const checkedCount = list.items.filter((i) => i.checked).length;
          return (
            <Link key={list.id} to={`/nutrition/shopping-lists/${list.id}`}>
              <Card className="flex-row items-center gap-3 px-4 py-3 transition-all hover:-translate-y-0.5 hover:shadow-md">
                <span className="flex size-10 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                  <ShoppingCart className="size-4.5" />
                </span>
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium">{list.name}</p>
                  <p className="text-sm text-muted-foreground">
                    {list.items.length} ítems · {checkedCount} comprados
                  </p>
                </div>
                <ChevronRight className="shrink-0 text-muted-foreground" />
              </Card>
            </Link>
          );
        })}
      </div>
    </div>
  );
}
