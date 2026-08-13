// Placeholder hasta que aterricen las próximas entregas de la Fase 3.2
// (profile → workout → nutrition → progress, ver docs/00-progress.md).
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useAuth } from '@/features/auth/application/useAuth';

export function HomePage() {
  const { user } = useAuth();

  return (
    <Card className="mx-auto max-w-md">
      <CardHeader>
        <CardTitle>¡Hola, {user?.email}!</CardTitle>
        <CardDescription>
          Sesión iniciada correctamente. El resto de las secciones (perfil, rutinas, nutrición,
          progreso) llegan en las próximas entregas de la Fase 3.2.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-sm text-muted-foreground">
          Roles: {user?.roles.join(', ') || '—'}
        </p>
      </CardContent>
    </Card>
  );
}
