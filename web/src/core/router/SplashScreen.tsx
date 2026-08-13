// Se muestra mientras se restaura la sesión guardada (equivalente a
// mobile/lib/core/router/splash_screen.dart).
export function SplashScreen() {
  return (
    <div className="flex min-h-svh items-center justify-center bg-background">
      <div
        className="size-8 animate-spin rounded-full border-2 border-muted border-t-primary"
        role="status"
        aria-label="Cargando"
      />
    </div>
  );
}
