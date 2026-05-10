import 'package:flutter/material.dart';

enum AppPalette { purple, orange, cyan, red, green }

class AppColors {
  AppColors._();

  static AppPalette _current = AppPalette.purple;
  static void setPalette(AppPalette p) => _current = p;
  static AppPalette get current => _current;

  static Color get primary {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFE8702A);
      case AppPalette.cyan:   return const Color(0xFF0891B2);
      case AppPalette.red:    return const Color(0xFFDC2626);
      case AppPalette.green:  return const Color(0xFF16A34A);
      default:                return const Color(0xFF6C47FF);
    }
  }

  static Color get primaryDark {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFC45D1F);
      case AppPalette.cyan:   return const Color(0xFF0E7490);
      case AppPalette.red:    return const Color(0xFFB91C1C);
      case AppPalette.green:  return const Color(0xFF15803D);
      default:                return const Color(0xFF5235CC);
    }
  }

  static Color get primaryLight {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFFEF0E7);
      case AppPalette.cyan:   return const Color(0xFFE0F7FA);
      case AppPalette.red:    return const Color(0xFFFFEEEE);
      case AppPalette.green:  return const Color(0xFFE8F5E9);
      default:                return const Color(0xFFEDE8FF);
    }
  }

  static Color get primarySurface {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFFFF7F0);
      case AppPalette.cyan:   return const Color(0xFFF0FBFF);
      case AppPalette.red:    return const Color(0xFFFFF5F5);
      case AppPalette.green:  return const Color(0xFFF0FDF4);
      default:                return const Color(0xFFF5F3FF);
    }
  }

  static Color get splashStart {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFFF9A5C);
      case AppPalette.cyan:   return const Color(0xFF22D3EE);
      case AppPalette.red:    return const Color(0xFFFF6B6B);
      case AppPalette.green:  return const Color(0xFF4ADE80);
      default:                return const Color(0xFF8B67FF);
    }
  }

  static Color get splashEnd {
    switch (_current) {
      case AppPalette.orange: return const Color(0xFFC45D1F);
      case AppPalette.cyan:   return const Color(0xFF0E7490);
      case AppPalette.red:    return const Color(0xFFB91C1C);
      case AppPalette.green:  return const Color(0xFF15803D);
      default:                return const Color(0xFF5C3DCC);
    }
  }

  // palette metadata - profile selector-d
  static const Map<AppPalette, PaletteInfo> paletteInfo = {
    AppPalette.purple: PaletteInfo(
      name: 'Нил ягаан',
      color: Color(0xFF6C47FF),
      gradientStart: Color(0xFF8B67FF),
      gradientEnd: Color(0xFF5C3DCC),
    ),
    AppPalette.orange: PaletteInfo(
      name: 'Улбар шар',
      color: Color(0xFFE8702A),
      gradientStart: Color(0xFFFF9A5C),
      gradientEnd: Color(0xFFC45D1F),
    ),
    AppPalette.cyan: PaletteInfo(
      name: 'Хөх ногоон',
      color: Color(0xFF0891B2),
      gradientStart: Color(0xFF22D3EE),
      gradientEnd: Color(0xFF0E7490),
    ),
    AppPalette.red: PaletteInfo(
      name: 'Улаан',
      color: Color(0xFFDC2626),
      gradientStart: Color(0xFFFF6B6B),
      gradientEnd: Color(0xFFB91C1C),
    ),
    AppPalette.green: PaletteInfo(
      name: 'Ногоон',
      color: Color(0xFF16A34A),
      gradientStart: Color(0xFF4ADE80),
      gradientEnd: Color(0xFF15803D),
    ),
  };

  // palette-аас үл хамаарах тогтмол өнгөнүүд
  static const Color background     = Color(0xFFF8F8FB);
  static const Color surface        = Color(0xFFFFFFFF);
  static const Color surfaceVariant = Color(0xFFF2F2F7);
  static const Color textPrimary    = Color(0xFF1A1A2E);
  static const Color textSecondary  = Color(0xFF8E8EA9);
  static const Color textHint       = Color(0xFFBBBBCC);
  static const Color border         = Color(0xFFE8E8F0);
  static const Color income         = Color(0xFF00C896);
  static const Color incomeLight    = Color(0xFFE6FAF5);
  static const Color expense        = Color(0xFFFF5252);
  static const Color expenseLight   = Color(0xFFFFEEEE);
  static const Color error          = Color(0xFFFF5252);
  static const Color errorLight     = Color(0xFFFFEEEE);
  static const Color success        = Color(0xFF00C896);
}

class PaletteInfo {
  final String name;
  final Color color;
  final Color gradientStart;
  final Color gradientEnd;

  const PaletteInfo({
    required this.name,
    required this.color,
    required this.gradientStart,
    required this.gradientEnd,
  });
}