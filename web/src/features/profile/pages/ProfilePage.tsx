import { Button } from '@/components/ui/button';
import { extractErrorMessage } from '@/core/api/apiError';
import { GoalSection } from '../components/GoalSection';
import { InjurySection } from '../components/InjurySection';
import { MedicationSection } from '../components/MedicationSection';
import { PathologySection } from '../components/PathologySection';
import { ProfileForm } from '../components/ProfileForm';
import { useProfile } from '../application/useProfile';

export function ProfilePage() {
  const { data, isLoading, isError, error, refetch } = useProfile();

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }

  if (isError || !data) {
    return (
      <div className="mx-auto flex max-w-sm flex-col items-center gap-3 py-12 text-center">
        <p className="text-sm text-muted-foreground">No se pudo cargar el perfil. {extractErrorMessage(error)}</p>
        <Button onClick={() => refetch()}>Reintentar</Button>
      </div>
    );
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-4 pb-12">
      <h1 className="text-xl font-semibold">Mi perfil</h1>
      <ProfileForm profile={data.profile} />
      <div className="mt-2 flex flex-col gap-3">
        <GoalSection goals={data.goals} />
        <PathologySection pathologies={data.pathologies} />
        <InjurySection injuries={data.injuries} />
        <MedicationSection medications={data.medications} />
      </div>
    </div>
  );
}
