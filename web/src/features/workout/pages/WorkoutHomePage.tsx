import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Dumbbell, Plus } from 'lucide-react';
import { HeroBanner } from '@/components/HeroBanner';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useWorkoutList } from '../application/queries';
import { GenerateWorkoutDialog } from '../components/GenerateWorkoutDialog';
import { SessionHistoryList } from '../components/SessionHistoryList';
import { WorkoutListView } from '../components/WorkoutListView';

export function WorkoutHomePage() {
  const [tab, setTab] = useState('own');
  const { data: workouts } = useWorkoutList(false);
  const aiCount = workouts?.filter((w) => w.aiGenerated).length ?? 0;

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-4 pb-12">
      <HeroBanner
        icon={<Dumbbell className="size-5" />}
        title="Rutinas"
        subtitle="Tu plan de entrenamiento, a tu manera"
        stats={[
          { label: workouts?.length === 1 ? 'rutina' : 'rutinas', value: workouts?.length ?? 0 },
          { label: 'generadas con IA ✨', value: aiCount },
        ]}
      />
      {tab === 'own' && (
        <div className="flex justify-end gap-2">
          <GenerateWorkoutDialog />
          <Button asChild size="sm">
            <Link to="/workouts/new">
              <Plus /> Nueva rutina
            </Link>
          </Button>
        </div>
      )}
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="own">Mis rutinas</TabsTrigger>
          <TabsTrigger value="templates">Plantillas</TabsTrigger>
          <TabsTrigger value="history">Historial</TabsTrigger>
        </TabsList>
        <TabsContent value="own" className="mt-4 min-w-0">
          <WorkoutListView templates={false} />
        </TabsContent>
        <TabsContent value="templates" className="mt-4 min-w-0">
          <WorkoutListView templates={true} />
        </TabsContent>
        <TabsContent value="history" className="mt-4 min-w-0">
          <SessionHistoryList />
        </TabsContent>
      </Tabs>
    </div>
  );
}
