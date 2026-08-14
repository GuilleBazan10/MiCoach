import { useState } from 'react';
import type { ReactNode } from 'react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import { PHOTO_ANGLE_LABELS } from '../domain/progressLabels';
import { useAddPhoto } from '../application/mutations';

export function AddPhotoDialog({ trigger }: { trigger: ReactNode }) {
  const addPhoto = useAddPhoto();
  const [open, setOpen] = useState(false);
  const [photoUrl, setPhotoUrl] = useState('');
  const [angle, setAngle] = useState<string | undefined>(undefined);

  function handleSubmit() {
    if (!photoUrl.trim()) return;
    addPhoto.mutate(
      { photoUrl: photoUrl.trim(), angle: angle ?? null },
      {
        onSuccess: () => {
          setOpen(false);
          setPhotoUrl('');
          setAngle(undefined);
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
          <DialogTitle>Agregar foto</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-3">
          <div>
            <Label>URL de la foto</Label>
            <Input value={photoUrl} onChange={(e) => setPhotoUrl(e.target.value)} className="mt-1.5" />
          </div>
          <OptionSelect label="Ángulo (opcional)" value={angle} onChange={setAngle} options={PHOTO_ANGLE_LABELS} />
        </div>
        <DialogFooter>
          <Button variant="ghost" onClick={() => setOpen(false)}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} disabled={addPhoto.isPending}>
            Agregar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
