// Smoke test: la app arranca y, sin sesión guardada, redirige a /login.
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:micoach_mobile/app/app.dart';

void main() {
  // flutter_secure_storage usa un MethodChannel que no existe en el entorno
  // de test; se mockea para que devuelva "sin sesión" en vez de lanzar
  // MissingPluginException.
  const secureStorageChannel = MethodChannel('plugins.it_nomads.com/flutter_secure_storage');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(secureStorageChannel, (call) async => null);
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(secureStorageChannel, null);
  });

  testWidgets('arranca y muestra la pantalla de login sin sesión guardada', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: MiCoachApp()));
    // La restauración de sesión resuelve en un microtask; unos pumps alcanzan
    // (pumpAndSettle no sirve acá: el splash tiene un spinner indeterminado).
    for (var i = 0; i < 10; i++) {
      await tester.pump(const Duration(milliseconds: 50));
    }

    expect(find.text('MiCoach'), findsOneWidget);
    expect(find.widgetWithText(FilledButton, 'Entrar'), findsOneWidget);
  });
}
