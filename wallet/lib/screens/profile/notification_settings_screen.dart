import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';

class NotificationSettingsScreen extends StatefulWidget {
  const NotificationSettingsScreen({super.key});

  @override
  State<NotificationSettingsScreen> createState() =>
      _NotificationSettingsScreenState();
}

class _NotificationSettingsScreenState
    extends State<NotificationSettingsScreen> {
  bool _txNotif = true;
  bool _monthlyReport = true;
  bool _billReminder = false;

  static const _txKey       = 'notif_transaction';
  static const _monthlyKey  = 'notif_monthly';
  static const _billKey     = 'notif_bill';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _txNotif       = prefs.getBool(_txKey) ?? true;
      _monthlyReport = prefs.getBool(_monthlyKey) ?? true;
      _billReminder  = prefs.getBool(_billKey) ?? false;
    });
  }

  Future<void> _save(String key, bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(key, value);
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
        title: const Text('Мэдэгдэл'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          const SizedBox(height: 12),
          Container(
            color: Colors.white,
            child: Column(
              children: [
                _NotifTile(
                  icon: Icons.swap_horiz_rounded,
                  title: 'Гүйлгээний мэдэгдэл',
                  subtitle: 'Гүйлгээ нэмэгдэх бүрд мэдэгдэл ирнэ',
                  value: _txNotif,
                  onChanged: (v) {
                    setState(() => _txNotif = v);
                    _save(_txKey, v);
                  },
                ),
                const Divider(height: 1, indent: 56),
                _NotifTile(
                  icon: Icons.bar_chart_rounded,
                  title: 'Сарын тайлан',
                  subtitle: 'Сар бүрийн эхэнд тойм мэдэгдэл ирнэ',
                  value: _monthlyReport,
                  onChanged: (v) {
                    setState(() => _monthlyReport = v);
                    _save(_monthlyKey, v);
                  },
                ),
                const Divider(height: 1, indent: 56),
                _NotifTile(
                  icon: Icons.receipt_long_rounded,
                  title: 'Төлбөрийн сануулга',
                  subtitle: 'Хугацаа дуусахаас өмнө сануулна',
                  value: _billReminder,
                  onChanged: (v) {
                    setState(() => _billReminder = v);
                    _save(_billKey, v);
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _NotifTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final bool value;
  final ValueChanged<bool> onChanged;

  const _NotifTile({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
          horizontal: 16, vertical: 12),
      child: Row(
        children: [
          Container(
            width: 36, height: 36,
            decoration: BoxDecoration(
              color: AppColors.primarySurface,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(icon, color: AppColors.primary, size: 20),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: AppTextStyles.labelMedium),
                Text(subtitle,
                    style: AppTextStyles.bodySmall),
              ],
            ),
          ),
          Switch(
            value: value,
            onChanged: onChanged,
            activeThumbColor: AppColors.primary,
          ),
        ],
      ),
    );
  }
}