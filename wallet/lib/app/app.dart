import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/language_provider.dart';
import '../providers/theme_provider.dart';
import 'router.dart';
import 'theme/app_theme.dart';

class FinancialNoteApp extends ConsumerWidget {
  const FinancialNoteApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);
    // palette solohold app rebuild hiij theme solgono
    final palette = ref.watch(themeProvider);
    final language = ref.watch(languageProvider);

    return MaterialApp.router(
      title: 'Financial Note',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.fromPalette(palette),
      locale: Locale(language.name),
      routerConfig: router,
    );
  }
}
