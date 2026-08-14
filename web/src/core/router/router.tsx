import { createBrowserRouter } from 'react-router-dom';
import { AiProvidersPage } from '@/features/admin/pages/AiProvidersPage';
import { LoginPage } from '@/features/auth/pages/LoginPage';
import { RegisterPage } from '@/features/auth/pages/RegisterPage';
import { MealPlanDetailPage } from '@/features/nutrition/pages/MealPlanDetailPage';
import { MealPlanFormPage } from '@/features/nutrition/pages/MealPlanFormPage';
import { NutritionHomePage } from '@/features/nutrition/pages/NutritionHomePage';
import { ShoppingListDetailPage } from '@/features/nutrition/pages/ShoppingListDetailPage';
import { ProfilePage } from '@/features/profile/pages/ProfilePage';
import { ProgressHomePage } from '@/features/progress/pages/ProgressHomePage';
import { SessionPage } from '@/features/workout/pages/SessionPage';
import { WorkoutDetailPage } from '@/features/workout/pages/WorkoutDetailPage';
import { WorkoutFormPage } from '@/features/workout/pages/WorkoutFormPage';
import { WorkoutHomePage } from '@/features/workout/pages/WorkoutHomePage';
import { AppShell } from './AppShell';
import { RequireAdmin } from './RequireAdmin';
import { RedirectIfAuthenticated, RequireAuth } from './RequireAuth';

export const router = createBrowserRouter([
  {
    element: <RedirectIfAuthenticated />,
    children: [
      { path: '/login', element: <LoginPage /> },
      { path: '/register', element: <RegisterPage /> },
    ],
  },
  {
    element: <RequireAuth />,
    children: [
      {
        element: <AppShell />,
        children: [
          { path: '/', element: <WorkoutHomePage /> },
          { path: '/workouts', element: <WorkoutHomePage /> },
          { path: '/workouts/new', element: <WorkoutFormPage /> },
          { path: '/workouts/:id', element: <WorkoutDetailPage /> },
          { path: '/workouts/:id/edit', element: <WorkoutFormPage /> },
          { path: '/sessions/:id', element: <SessionPage /> },
          { path: '/nutrition', element: <NutritionHomePage /> },
          { path: '/nutrition/plans/new', element: <MealPlanFormPage /> },
          { path: '/nutrition/plans/:id', element: <MealPlanDetailPage /> },
          { path: '/nutrition/plans/:id/edit', element: <MealPlanFormPage /> },
          { path: '/nutrition/shopping-lists/:id', element: <ShoppingListDetailPage /> },
          { path: '/progress', element: <ProgressHomePage /> },
          { path: '/profile', element: <ProfilePage /> },
          {
            element: <RequireAdmin />,
            children: [{ path: '/admin/ai', element: <AiProvidersPage /> }],
          },
        ],
      },
    ],
  },
]);
