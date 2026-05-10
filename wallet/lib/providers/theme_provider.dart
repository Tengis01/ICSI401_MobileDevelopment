import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../app/theme/app_colors.dart';

const String _themeKey = 'selected_palette';

class ThemeNotifier extends Notifier<AppPalette> {
  @override
  AppPalette build() {
    // build hiigdeh uyd hadgalagdsan palette-g unshih
    _loadSaved();
    return AppPalette.purple;
  }

  Future<void> _loadSaved() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString(_themeKey);
    if (saved == null) return;

    final palette = AppPalette.values.firstWhere(
          (p) => p.name == saved,
      orElse: () => AppPalette.purple,
    );

    AppColors.setPalette(palette);
    state = palette;
  }

  Future<void> setPalette(AppPalette palette) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_themeKey, palette.name);
    AppColors.setPalette(palette);
    state = palette;
  }
}

final themeProvider = NotifierProvider<ThemeNotifier, AppPalette>(
  ThemeNotifier.new,
);