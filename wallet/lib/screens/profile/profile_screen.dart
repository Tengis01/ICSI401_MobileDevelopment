import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../providers/transaction_provider.dart';
import '../../providers/theme_provider.dart';
import '../../widgets/app_bottom_nav.dart';

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

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        automaticallyImplyLeading: false,
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
                        bottom: 0, right: 0,
                        child: Container(
                          width: 26, height: 26,
                          decoration: BoxDecoration(
                            color: AppColors.primary,
                            shape: BoxShape.circle,
                            border: Border.all(
                                color: Colors.white, width: 2),
                          ),
                          child: const Icon(
                            Icons.camera_alt_rounded,
                            color: Colors.white, size: 12,
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
                    style: AppTextStyles.bodyMedium.copyWith(
                        color: AppColors.textSecondary),
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
                            value: '$txCount',
                            label: 'Энэ сарын\nгүйлгээ'),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            // theme songoh section
            Container(
              color: Colors.white,
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Өнгөний загвар',
                      style: AppTextStyles.labelLarge),
                  const SizedBox(height: 4),
                  Text(
                    'Өөрт таалагдах өнгөөр өөрчилнэ үү',
                    style: AppTextStyles.bodySmall,
                  ),
                  const SizedBox(height: 16),
                  const _ThemeSelector(),
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
                    icon: Icons.notifications_outlined,
                    label: 'Мэдэгдэл',
                    trailing: const Text('Идэвхтэй'),
                    onTap: () =>
                        context.push('/profile/notifications'),
                  ),
                  const Divider(height: 1, indent: 56),
                  _SettingsTile(
                    icon: Icons.language_outlined,
                    label: 'Хэл',
                    trailing: const Text('Монгол'),
                    onTap: () =>
                        context.push('/profile/language'),
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
                      content: const Text(
                          'Акаунтаасаа гарахдаа итгэлтэй байна уу?'),
                      actions: [
                        TextButton(
                          onPressed: () =>
                              Navigator.pop(ctx, false),
                          child: const Text('Болих'),
                        ),
                        TextButton(
                          onPressed: () =>
                              Navigator.pop(ctx, true),
                          child: Text('Гарах',
                              style: TextStyle(
                                  color: AppColors.error)),
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
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push(AppRoutes.entry),
        backgroundColor: AppColors.primary,
        shape: const CircleBorder(),
        child: const Icon(Icons.add_rounded,
            color: Colors.white, size: 28),
      ),
      floatingActionButtonLocation:
      FloatingActionButtonLocation.centerDocked,
      bottomNavigationBar: const AppBottomNav(currentIndex: 3),
    );
  }
}

// theme selector widget
class _ThemeSelector extends ConsumerWidget {
  const _ThemeSelector();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final current = ref.watch(themeProvider);

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: AppPalette.values.map((palette) {
        final info = AppColors.paletteInfo[palette]!;
        final isSelected = current == palette;

        return GestureDetector(
          onTap: () =>
              ref.read(themeProvider.notifier).setPalette(palette),
          child: Column(
            children: [
              AnimatedContainer(
                duration: const Duration(milliseconds: 250),
                curve: Curves.easeOut,
                width: isSelected ? 52 : 44,
                height: isSelected ? 52 : 44,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                    colors: [
                      info.gradientStart,
                      info.gradientEnd,
                    ],
                  ),
                  shape: BoxShape.circle,
                  border: isSelected
                      ? Border.all(
                    color: AppColors.textPrimary,
                    width: 2.5,
                  )
                      : null,
                  boxShadow: isSelected
                      ? [
                    BoxShadow(
                      color: info.color.withOpacity(0.4),
                      blurRadius: 12,
                      offset: const Offset(0, 4),
                    )
                  ]
                      : null,
                ),
                child: isSelected
                    ? const Icon(Icons.check_rounded,
                    color: Colors.white, size: 22)
                    : null,
              ),
              const SizedBox(height: 6),
              Text(
                info.name,
                style: AppTextStyles.bodySmall.copyWith(
                  fontSize: 10,
                  color: isSelected
                      ? info.color
                      : AppColors.textSecondary,
                  fontWeight: isSelected
                      ? FontWeight.w600
                      : FontWeight.w400,
                ),
              ),
            ],
          ),
        );
      }).toList(),
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
        Text(value,
            style: AppTextStyles.h2.copyWith(
                color: AppColors.primary)),
        Text(label,
            style: AppTextStyles.bodySmall,
            textAlign: TextAlign.center),
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
        width: 36, height: 36,
        decoration: BoxDecoration(
          color: (iconColor ?? AppColors.primary).withOpacity(0.1),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Icon(icon,
            color: iconColor ?? AppColors.primary, size: 20),
      ),
      title: Text(label,
          style: AppTextStyles.labelMedium.copyWith(
              color: labelColor ?? AppColors.textPrimary)),
      trailing: trailing != null
          ? Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          DefaultTextStyle(
            style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary),
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