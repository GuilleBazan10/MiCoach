import { useState } from 'react';
import { Camera, ImageOff, Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { ConfirmDeleteDialog } from '@/components/ConfirmDeleteDialog';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { extractErrorMessage } from '@/core/api/apiError';
import { useProgressPhotos } from '../application/queries';
import { useDeletePhoto } from '../application/mutations';
import { PHOTO_ANGLE_LABELS, labelFor } from '../domain/progressLabels';
import { AddPhotoDialog } from './AddPhotoDialog';

const dateFormatter = new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric' });

export function ProgressPhotosView() {
  const { data: photos, isLoading, isError, refetch } = useProgressPhotos();
  const deletePhoto = useDeletePhoto();
  const [pendingDeleteId, setPendingDeleteId] = useState<number | null>(null);

  return (
    <div className="flex flex-col gap-3">
      <AddPhotoDialog
        trigger={
          <Button size="sm" className="self-start">
            <Plus /> Agregar foto
          </Button>
        }
      />

      {isLoading && (
        <div className="flex justify-center py-12">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
        </div>
      )}
      {isError && <ErrorState onRetry={() => refetch()} />}
      {!isLoading && photos?.length === 0 && (
        <EmptyState icon={Camera} message="Todavía no subiste ninguna foto de progreso." />
      )}
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
        {photos?.map((photo) => (
          <PhotoCard
            key={photo.id}
            photoUrl={photo.photoUrl}
            caption={`${labelFor(PHOTO_ANGLE_LABELS, photo.angle)} · ${dateFormatter.format(new Date(photo.takenAt))}`}
            onDelete={() => setPendingDeleteId(photo.id)}
          />
        ))}
      </div>
      <ConfirmDeleteDialog
        open={pendingDeleteId != null}
        onOpenChange={(next) => !next && setPendingDeleteId(null)}
        title="Borrar foto"
        message="¿Seguro que querés borrar esta foto de progreso? Esta acción no se puede deshacer."
        pending={deletePhoto.isPending}
        onConfirm={() => {
          if (pendingDeleteId == null) return;
          deletePhoto.mutate(pendingDeleteId, {
            onSuccess: () => setPendingDeleteId(null),
            onError: (error) => toast.error(extractErrorMessage(error)),
          });
        }}
      />
    </div>
  );
}

function PhotoCard({ photoUrl, caption, onDelete }: { photoUrl: string; caption: string; onDelete: () => void }) {
  const [broken, setBroken] = useState(false);

  return (
    <Card className="overflow-hidden py-0">
      <div className="flex aspect-[4/5] items-center justify-center bg-muted">
        {broken ? (
          <ImageOff className="text-muted-foreground" />
        ) : (
          <img src={photoUrl} alt="" className="h-full w-full object-cover" onError={() => setBroken(true)} />
        )}
      </div>
      <div className="flex items-center justify-between gap-1 px-2 py-1.5">
        <p className="truncate text-xs text-muted-foreground">{caption}</p>
        <Button variant="ghost" size="icon-sm" aria-label="Borrar foto" onClick={onDelete}>
          <Trash2 className="size-3.5" />
        </Button>
      </div>
    </Card>
  );
}
