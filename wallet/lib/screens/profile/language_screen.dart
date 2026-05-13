import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../providers/language_provider.dart';

class LanguageScreen extends ConsumerWidget {
  const LanguageScreen({super.key});

  Future<void> _select(
    BuildContext context,
    WidgetRef ref,
    AppLanguage language,
  ) async {
    await ref.read(languageProvider.notifier).setLanguage(language);
    if (!context.mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${language.label} сонгогдлоо'),
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 1),
      ),
    );
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selected = ref.watch(languageProvider);

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Хэл'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          const SizedBox(height: 12),
          Container(
            color: Colors.white,
            child: Column(
              children: [
                _LangTile(
                  flag: '🇲🇳',
                  label: 'Монгол',
                  isSelected: selected == AppLanguage.mn,
                  onTap: () => _select(context, ref, AppLanguage.mn),
                ),
                const Divider(height: 1, indent: 56),
                _LangTile(
                  flag: '🇺🇸',
                  label: 'English',
                  isSelected: selected == AppLanguage.en,
                  onTap: () => _select(context, ref, AppLanguage.en),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Text(
              'Хэл солих нь зарим хэсэгт нөлөөлөх бөгөөд бүрэн орчуулга дараагийн хувилбарт нэмэгдэх болно.',
              style: AppTextStyles.bodySmall,
              textAlign: TextAlign.center,
            ),
          ),
        ],
      ),
    );
  }
}

class _LangTile extends StatelessWidget {
  final String flag;
  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  const _LangTile({
    required this.flag,
    required this.label,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: onTap,
      leading: Text(flag, style: const TextStyle(fontSize: 28)),
      title: Text(label, style: AppTextStyles.labelMedium),
      trailing: isSelected
          ? Icon(Icons.check_circle_rounded, color: AppColors.primary)
          : const Icon(Icons.radio_button_unchecked_rounded,
              color: AppColors.border),
    );
  }
}
