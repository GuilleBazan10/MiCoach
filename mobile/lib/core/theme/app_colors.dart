// =====================================================================
// MiCoach — Paleta de colores centralizada
//
// >>> CAMBIA AQUÍ EL DISEÑO <<<
// Ej: el verde salud por el color de tu marca. Claro y oscuro por separado.
// =====================================================================
import 'package:flutter/material.dart';

@immutable
class AppColors {
  final Brightness brightness;
  final Color seed;
  final Color background;
  final Color surface;
  final Color onSurface;
  final Color primary;
  final Color onPrimary;
  final Color accent;
  final Color error;

  const AppColors({
    required this.brightness,
    required this.seed,
    required this.background,
    required this.surface,
    required this.onSurface,
    required this.primary,
    required this.onPrimary,
    required this.accent,
    required this.error,
  });

  // ---- Tema claro ----------------------------------------------------
  static const AppColors light = AppColors(
    brightness: Brightness.light,
    seed: Color(0xFF4CAF50), // Verde salud — cambiar aquí
    background: Color(0xFFFAFAFA),
    surface: Colors.white,
    onSurface: Color(0xFF1C1B1F),
    primary: Color(0xFF4CAF50),
    onPrimary: Colors.white,
    accent: Color(0xFF00BFA5), // Turquesa (energía/hidratación)
    error: Color(0xFFB3261E),
  );

  // ---- Tema oscuro ---------------------------------------------------
  static const AppColors dark = AppColors(
    brightness: Brightness.dark,
    seed: Color(0xFF81C784),
    background: Color(0xFF121212),
    surface: Color(0xFF1E1E1E),
    onSurface: Color(0xFFE6E1E5),
    primary: Color(0xFF81C784),
    onPrimary: Color(0xFF00391F),
    accent: Color(0xFF00BFA5),
    error: Color(0xFFF2B8B5),
  );
}
