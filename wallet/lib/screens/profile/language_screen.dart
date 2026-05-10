import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';

class LanguageScreen extends StatefulWidget {
  const LanguageScreen({super.key});

  @override
  State<LanguageScreen> createState() => _LanguageScreenState();
}

class _LanguageScreenState extends State<LanguageScreen> {
  String _selected = 'mn';
  static const _key = 'app_language';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() => _selected = prefs.getString(_key) ?? 'mn');
  }

  Future<void> _select(String lang) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_key, lang);
    setState(() => _selected = lang);
  }

  @override
  Widget build(BuildContext context) {
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
                  code: 'mn',
                  isSelected: _selected == 'mn',
                  onTap: () => _select('mn'),
                ),
                const Divider(height: 1, indent: 56),
                _LangTile(
                  flag: '🇺🇸',
                  label: 'English',
                  code: 'en',
                  isSelected: _selected == 'en',
                  onTap: () => _select('en'),
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
  final String code;
  final bool isSelected;
  final VoidCallback onTap;

  const _LangTile({
    required this.flag,
    required this.label,
    required this.code,
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
          ? Icon(Icons.check_circle_rounded,
          color: AppColors.primary)
          : const Icon(Icons.radio_button_unchecked_rounded,
          color: AppColors.border),
    );
  }
}