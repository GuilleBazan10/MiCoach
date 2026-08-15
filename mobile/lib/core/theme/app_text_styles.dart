// =====================================================================
// MiCoach — Tipografías centralizadas
//
// >>> CAMBIA AQUÍ LA TIPOGRAFÍA DEL DISEÑO <<<
// Si usas google_fonts, sustituye las constancias TextStyle por
// GoogleFonts.roboto(....) o la fuente que elijas.
// =====================================================================
import 'package:flutter/material.dart';

@immutable
class AppTextStyles {
  final TextStyle title;
  final TextStyle heading;
  final TextStyle body;
  final TextStyle label;
  final TextStyle caption;

  const AppTextStyles({
    required this.title,
    required this.heading,
    required this.body,
    required this.label,
    required this.caption,
  });

  static const AppTextStyles light = AppTextStyles(
    title: TextStyle(fontSize: 28, fontWeight: FontWeight.w700, height: 1.2),
    heading: TextStyle(fontSize: 20, fontWeight: FontWeight.w600, height: 1.3),
    body: TextStyle(fontSize: 16, fontWeight: FontWeight.w400, height: 1.5),
    label: TextStyle(fontSize: 14, fontWeight: FontWeight.w500, letterSpacing: 0.3),
    caption: TextStyle(fontSize: 12, fontWeight: FontWeight.w400, height: 1.4),
  );

  // El texto base es el mismo; el color lo aporta el theme.
  static const AppTextStyles dark = light;

  /// Combina estos estilos con los del ThemeData (conserva colores).
  TextTheme textTheme(TextTheme base) {
    return base.copyWith(
      headlineMedium: title,
      titleLarge: heading,
      bodyLarge: body,
      labelLarge: label,
      bodySmall: caption,
    );
  }
}
