// =====================================================================
// KineticOs — PUNTO ÚNICO DEL DISEÑO
//
// Si quieres cambiar el diseño de la app, edita estos tres archivos:
//   - app_colors.dart     → paleta de colores (claro/oscuro)
//   - app_text_styles.dart → tipografías
//   - app_spacing.dart     → medidas/espaciados
//
// Toda la app se re-tematiza automáticamente. NO pongas colores "a mano"
// dentro de las features: usa siempre estos tokens.
// =====================================================================
import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_spacing.dart';
import 'app_text_styles.dart';

class AppTheme {
  const AppTheme._();

  static ThemeData light() => _build(AppColors.light, AppTextStyles.light);
  static ThemeData dark() => _build(AppColors.dark, AppTextStyles.dark);

  static ThemeData _build(AppColors colors, AppTextStyles text) {
    final base = ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: colors.seed,
        brightness: colors.brightness,
      ),
    );

    return base.copyWith(
      scaffoldBackgroundColor: colors.background,
      canvasColor: colors.background,
      colorScheme: base.colorScheme.copyWith(
        primary: colors.primary,
        onPrimary: colors.onPrimary,
        secondary: colors.accent,
        error: colors.error,
        surface: colors.surface,
        onSurface: colors.onSurface,
      ),
      appBarTheme: AppBarTheme(
        backgroundColor: colors.surface,
        foregroundColor: colors.onSurface,
        elevation: 0,
        centerTitle: true,
      ),
      textTheme: text.textTheme(base.textTheme),
      cardTheme: CardThemeData(
        color: colors.surface,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppSpacing.radiusMd),
        ),
      ),
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: colors.surface,
        indicatorColor: colors.primary.withValues(alpha: 0.2),
      ),
    );
  }
}
