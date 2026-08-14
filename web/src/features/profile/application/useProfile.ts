// =====================================================================
// KineticOs — Estado del perfil (TanStack Query). Paridad con
// mobile/lib/features/profile/application/profile_providers.dart
// (ProfileController: carga combinada + refresh tras cada mutación).
// =====================================================================
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { profileApi } from '../api/profileApi';
import type { UserProfile } from '../domain/profileTypes';

const PROFILE_QUERY_KEY = ['profile'] as const;

async function loadProfileData() {
  // El perfil se crea de forma perezosa en este GET (getOrCreateProfile en el
  // backend); goals/pathologies/injuries/medications asumen que ya existe, así
  // que van después y no en paralelo con el de arriba (mismo orden que
  // mobile/lib/features/profile/application/profile_providers.dart).
  const profile = await profileApi.getProfile();
  const [goals, pathologies, injuries, medications] = await Promise.all([
    profileApi.getGoals(),
    profileApi.getPathologies(),
    profileApi.getInjuries(),
    profileApi.getMedications(),
  ]);
  return { profile, goals, pathologies, injuries, medications };
}

export function useProfile() {
  const queryClient = useQueryClient();
  const query = useQuery({ queryKey: PROFILE_QUERY_KEY, queryFn: loadProfileData });

  function invalidate() {
    return queryClient.invalidateQueries({ queryKey: PROFILE_QUERY_KEY });
  }

  const updateProfile = useMutation({
    mutationFn: (profile: UserProfile) => profileApi.updateProfile(profile),
    onSuccess: invalidate,
  });

  const addGoal = useMutation({ mutationFn: profileApi.addGoal, onSuccess: invalidate });
  const deleteGoal = useMutation({ mutationFn: profileApi.deleteGoal, onSuccess: invalidate });

  const addPathology = useMutation({ mutationFn: profileApi.addPathology, onSuccess: invalidate });
  const deletePathology = useMutation({ mutationFn: profileApi.deletePathology, onSuccess: invalidate });

  const addInjury = useMutation({ mutationFn: profileApi.addInjury, onSuccess: invalidate });
  const deleteInjury = useMutation({ mutationFn: profileApi.deleteInjury, onSuccess: invalidate });

  const addMedication = useMutation({ mutationFn: profileApi.addMedication, onSuccess: invalidate });
  const deleteMedication = useMutation({ mutationFn: profileApi.deleteMedication, onSuccess: invalidate });

  return {
    ...query,
    updateProfile,
    addGoal,
    deleteGoal,
    addPathology,
    deletePathology,
    addInjury,
    deleteInjury,
    addMedication,
    deleteMedication,
  };
}
