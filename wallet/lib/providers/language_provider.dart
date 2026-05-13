import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

enum AppLanguage { mn, en }

const String languagePreferenceKey = 'app_language';

class LanguageNotifier extends Notifier<AppLanguage> {
  @override
  AppLanguage build() {
    _loadSaved();
    return AppLanguage.mn;
  }

  Future<void> _loadSaved() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString(languagePreferenceKey);
    state = _fromCode(saved);
  }

  Future<void> setLanguage(AppLanguage language) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(languagePreferenceKey, language.name);
    state = language;
  }

  AppLanguage _fromCode(String? code) {
    return AppLanguage.values.firstWhere(
      (language) => language.name == code,
      orElse: () => AppLanguage.mn,
    );
  }
}

final languageProvider = NotifierProvider<LanguageNotifier, AppLanguage>(
  LanguageNotifier.new,
);

extension AppLanguageLabel on AppLanguage {
  String get label {
    switch (this) {
      case AppLanguage.mn:
        return 'Монгол';
      case AppLanguage.en:
        return 'English';
    }
  }
}
