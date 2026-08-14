import { useState } from 'react';
import { TrendingUp } from 'lucide-react';
import { HeroBanner } from '@/components/HeroBanner';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useProgressEntries, useProgressPhotos } from '../application/queries';
import { MetricEntriesView } from '../components/MetricEntriesView';
import { ProgressPhotosView } from '../components/ProgressPhotosView';

export function ProgressHomePage() {
  const [tab, setTab] = useState('metrics');
  const { data: entries } = useProgressEntries();
  const { data: photos } = useProgressPhotos();

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-4 pb-12">
      <HeroBanner
        icon={<TrendingUp className="size-5" />}
        title="Progreso"
        subtitle="Medí tu avance con el tiempo"
        stats={[
          { label: entries?.length === 1 ? 'registro' : 'registros', value: entries?.length ?? 0 },
          { label: photos?.length === 1 ? 'foto' : 'fotos', value: photos?.length ?? 0 },
        ]}
      />
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="metrics">Métricas</TabsTrigger>
          <TabsTrigger value="photos">Fotos</TabsTrigger>
        </TabsList>
        <TabsContent value="metrics" className="mt-4 min-w-0">
          <MetricEntriesView />
        </TabsContent>
        <TabsContent value="photos" className="mt-4 min-w-0">
          <ProgressPhotosView />
        </TabsContent>
      </Tabs>
    </div>
  );
}
