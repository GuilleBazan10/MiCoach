import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Utensils } from 'lucide-react';
import { HeroBanner } from '@/components/HeroBanner';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useMealPlanList } from '../application/queries';
import { DailyIntakeView } from '../components/DailyIntakeView';
import { GenerateMealPlanDialog } from '../components/GenerateMealPlanDialog';
import { MealPlanListView } from '../components/MealPlanListView';
import { ShoppingListListView } from '../components/ShoppingListListView';

export function NutritionHomePage() {
  const [tab, setTab] = useState('plans');
  const { data: plans } = useMealPlanList();
  const aiCount = plans?.filter((p) => p.aiGenerated).length ?? 0;

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-4 pb-12">
      <HeroBanner
        icon={<Utensils className="size-5" />}
        title="Nutrición"
        subtitle="Planificá qué comer sin pensarlo dos veces"
        stats={[
          { label: plans?.length === 1 ? 'plan' : 'planes', value: plans?.length ?? 0 },
          { label: 'generados con IA ✨', value: aiCount },
        ]}
      />
      {tab === 'plans' && (
        <div className="flex justify-end gap-2">
          <GenerateMealPlanDialog />
          <Button asChild size="sm">
            <Link to="/nutrition/plans/new">
              <Plus /> Nuevo plan
            </Link>
          </Button>
        </div>
      )}
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="plans">Planes</TabsTrigger>
          <TabsTrigger value="intake">Diario</TabsTrigger>
          <TabsTrigger value="shopping">Compras</TabsTrigger>
        </TabsList>
        <TabsContent value="plans" className="mt-4 min-w-0">
          <MealPlanListView />
        </TabsContent>
        <TabsContent value="intake" className="mt-4 min-w-0">
          <DailyIntakeView />
        </TabsContent>
        <TabsContent value="shopping" className="mt-4 min-w-0">
          <ShoppingListListView />
        </TabsContent>
      </Tabs>
    </div>
  );
}
