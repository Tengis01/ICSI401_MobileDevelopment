import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../providers/language_provider.dart';
import '../../providers/transaction_provider.dart';
import '../../providers/theme_provider.dart';

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final user = FirebaseAuth.instance.currentUser;
    final now = DateTime.now();
    final transactionsAsync = ref.watch(
      monthlyTransactionsProvider((year: now.year, month: now.month)),
    );
    final txCount = transactionsAsync.valueOrNull?.length ?? 0;
    final language = ref.watch(languageProvider);
    final currentPalette = ref.watch(themeProvider);
    final currentPaletteName = AppColors.paletteInfo[currentPalette]!.name;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Профайл'),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // user info
            Container(
              color: Colors.white,
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  Stack(
                    children: [
                      CircleAvatar(
                        radius: 40,
                        backgroundColor: AppColors.primaryLight,
                        child: Text(
                          user?.displayName?.isNotEmpty == true
                              ? user!.displayName![0].toUpperCase()
                              : 'Б',
                          style: TextStyle(
                            fontSize: 32,
                            fontWeight: FontWeight.w700,
                            color: AppColors.primary,
                          ),
                        ),
                      ),
                      Positioned(
                        bottom: 0,
                        right: 0,
                        child: Container(
                          width: 26,
                          height: 26,
                          decoration: BoxDecoration(
                            color: AppColors.primary,
                            shape: BoxShape.circle,
                            border: Border.all(color: Colors.white, width: 2),
                          ),
                          child: const Icon(
                            Icons.camera_alt_rounded,
                            color: Colors.white,
                            size: 12,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Text(
                    user?.displayName ?? 'Хэрэглэгч',
                    style: AppTextStyles.h3,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    user?.email ?? '',
                    style: AppTextStyles.bodyMedium
                        .copyWith(color: AppColors.textSecondary),
                  ),
                  const SizedBox(height: 16),
                  // profile zasah button
                  OutlinedButton.icon(
                    onPressed: () => context.push('/profile/edit'),
                    icon: const Icon(Icons.edit_outlined, size: 16),
                    label: const Text('Профайл засах'),
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(color: AppColors.primary),
                      foregroundColor: AppColors.primary,
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20)),
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16, vertical: 8),
                    ),
                  ),
                  const SizedBox(height: 20),
                  // stats row
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 16, vertical: 12),
                    decoration: BoxDecoration(
                      color: AppColors.primarySurface,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        _StatItem(
                            value: '$txCount', label: 'Энэ сарын\nгүйлгээ'),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            // settings list
            Container(
              color: Colors.white,
              child: Column(
                children: [
                  _SettingsTile(
                    icon: Icons.palette_outlined,
                    label: 'Өнгөний загвар',
                    trailing: Text(currentPaletteName),
                    onTap: () => _showThemePicker(context),
                  ),
                  const Divider(height: 1, indent: 56),
                  _SettingsTile(
                    icon: Icons.notifications_outlined,
                    label: 'Мэдэгдэл',
                    trailing: const Text('Идэвхтэй'),
                    onTap: () => context.push('/profile/notifications'),
                  ),
                  const Divider(height: 1, indent: 56),
                  _SettingsTile(
                    icon: Icons.language_outlined,
                    label: 'Хэл',
                    trailing: Text(language.label),
                    onTap: () => context.push('/profile/language'),
                  ),
                  const Divider(height: 1, indent: 56),
                  _SettingsTile(
                    icon: Icons.security_outlined,
                    label: 'Аюулгүй байдал',
                    onTap: () {},
                  ),
                  const Divider(height: 1, indent: 56),
                  _SettingsTile(
                    icon: Icons.help_outline_rounded,
                    label: 'Тусламж',
                    onTap: () {},
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            // garch button
            Container(
              color: Colors.white,
              child: _SettingsTile(
                icon: Icons.logout_rounded,
                label: 'Гарах',
                iconColor: AppColors.error,
                labelColor: AppColors.error,
                onTap: () async {
                  final confirmed = await showDialog<bool>(
                    context: context,
                    builder: (ctx) => AlertDialog(
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16)),
                      title: const Text('Гарах уу?'),
                      content:
                          const Text('Акаунтаасаа гарахдаа итгэлтэй байна уу?'),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.pop(ctx, false),
                          child: const Text('Болих'),
                        ),
                        TextButton(
                          onPressed: () => Navigator.pop(ctx, true),
                          child: Text('Гарах',
                              style: TextStyle(color: AppColors.error)),
                        ),
                      ],
                    ),
                  );
                  if (confirmed != true) return;
                  await FirebaseAuth.instance.signOut();
                  if (!context.mounted) return;
                  context.go(AppRoutes.login);
                },
              ),
            ),
            const SizedBox(height: 100),
          ],
        ),
      ),
    );
  }

  void _showThemePicker(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.white,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (_) => const _ThemePickerSheet(),
    );
  }
}

class _ThemePickerSheet extends ConsumerWidget {
  const _ThemePickerSheet();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final current = ref.watch(themeProvider);
    final palettes = AppPalette.values;

    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(24, 12, 24, 28),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: AppColors.border,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 20),
            Text('Өнгөний загвар', style: AppTextStyles.h3),
            const SizedBox(height: 6),
            Text(
              'Аппын үндсэн өнгөө сонгоно уу',
              style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary,
              ),
            ),
            const SizedBox(height: 20),
            _ThemePickerRow(
              palettes: palettes.take(2).toList(),
              current: current,
            ),
            const SizedBox(height: 16),
            _ThemePickerRow(
              palettes: palettes.skip(2).take(2).toList(),
              current: current,
            ),
            const SizedBox(height: 16),
            Center(
              child: SizedBox(
                width: (MediaQuery.of(context).size.width - 64) / 2,
                child: _ThemeOption(
                  palette: palettes.last,
                  isSelected: current == palettes.last,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ThemePickerRow extends StatelessWidget {
  final List<AppPalette> palettes;
  final AppPalette current;

  const _ThemePickerRow({
    required this.palettes,
    required this.current,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        for (var i = 0; i < palettes.length; i++) ...[
          Expanded(
            child: _ThemeOption(
              palette: palettes[i],
              isSelected: current == palettes[i],
            ),
          ),
          if (i != palettes.length - 1) const SizedBox(width: 16),
        ],
      ],
    );
  }
}

class _ThemeOption extends ConsumerWidget {
  final AppPalette palette;
  final bool isSelected;

  const _ThemeOption({
    required this.palette,
    required this.isSelected,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final info = AppColors.paletteInfo[palette]!;

    return InkWell(
      onTap: () => ref.read(themeProvider.notifier).setPalette(palette),
      borderRadius: BorderRadius.circular(18),
      child: Column(
        children: [
          AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            height: 92,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [info.gradientStart, info.gradientEnd],
              ),
              borderRadius: BorderRadius.circular(18),
              border: Border.all(
                color: isSelected ? AppColors.textPrimary : AppColors.border,
                width: isSelected ? 2 : 1,
              ),
              boxShadow: isSelected
                  ? [
                      BoxShadow(
                        color: info.color.withValues(alpha: 0.28),
                        blurRadius: 14,
                        offset: const Offset(0, 6),
                      ),
                    ]
                  : null,
            ),
            child: isSelected
                ? const Center(
                    child: Icon(
                      Icons.check_circle_rounded,
                      color: Colors.white,
                      size: 30,
                    ),
                  )
                : null,
          ),
          const SizedBox(height: 8),
          Text(
            info.name,
            style: AppTextStyles.labelMedium.copyWith(
              color: isSelected ? info.color : AppColors.textSecondary,
              fontWeight: isSelected ? FontWeight.w700 : FontWeight.w500,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}

class _StatItem extends StatelessWidget {
  final String value;
  final String label;

  const _StatItem({required this.value, required this.label});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(value, style: AppTextStyles.h2.copyWith(color: AppColors.primary)),
        Text(label,
            style: AppTextStyles.bodySmall, textAlign: TextAlign.center),
      ],
    );
  }
}

class _SettingsTile extends StatelessWidget {
  final IconData icon;
  final String label;
  final Widget? trailing;
  final VoidCallback onTap;
  final Color? iconColor;
  final Color? labelColor;

  const _SettingsTile({
    required this.icon,
    required this.label,
    required this.onTap,
    this.trailing,
    this.iconColor,
    this.labelColor,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: onTap,
      leading: Container(
        width: 36,
        height: 36,
        decoration: BoxDecoration(
          color: (iconColor ?? AppColors.primary).withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Icon(icon, color: iconColor ?? AppColors.primary, size: 20),
      ),
      title: Text(label,
          style: AppTextStyles.labelMedium
              .copyWith(color: labelColor ?? AppColors.textPrimary)),
      trailing: trailing != null
          ? Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                DefaultTextStyle(
                  style: AppTextStyles.bodyMedium
                      .copyWith(color: AppColors.textSecondary),
                  child: trailing!,
                ),
                const SizedBox(width: 4),
                const Icon(Icons.chevron_right_rounded,
                    color: AppColors.textSecondary, size: 20),
              ],
            )
          : const Icon(Icons.chevron_right_rounded,
              color: AppColors.textSecondary, size: 20),
    );
  }
}
