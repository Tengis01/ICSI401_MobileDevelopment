import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../app/router.dart';
import '../app/theme/app_colors.dart';

class AppBottomNav extends StatelessWidget {
  final int currentIndex;

  const AppBottomNav({super.key, required this.currentIndex});

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      color: Colors.white,
      elevation: 8,
      notchMargin: 8,
      shape: const CircularNotchedRectangle(),
      child: SizedBox(
        height: 56,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _NavItem(
              icon: Icons.home_rounded,
              label: 'Нүүр',
              isActive: currentIndex == 0,
              onTap: () => context.go(AppRoutes.home),
            ),
            _NavItem(
              icon: Icons.bar_chart_rounded,
              label: 'Статистик',
              isActive: currentIndex == 1,
              onTap: () => context.go(AppRoutes.stats),
            ),
            const SizedBox(width: 48),
            _NavItem(
              icon: Icons.search_rounded,
              label: 'Хайх',
              isActive: currentIndex == 2,
              onTap: () => context.go(AppRoutes.search),
            ),
            _NavItem(
              icon: Icons.account_balance_wallet_outlined,
              label: 'Хэтэвч',
              isActive: currentIndex == 3,
              onTap: () => context.go(AppRoutes.wallet),
            ),
          ],
        ),
      ),
    );
  }
}

class _NavItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final bool isActive;
  final VoidCallback onTap;

  const _NavItem({
    required this.icon,
    required this.label,
    required this.isActive,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final color = isActive ? AppColors.primary : AppColors.textSecondary;
    return GestureDetector(
      onTap: onTap,
      behavior: HitTestBehavior.opaque,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(height: 2),
          Text(
            label,
            style: TextStyle(
              fontSize: 10,
              color: color,
              fontWeight:
              isActive ? FontWeight.w600 : FontWeight.w400,
            ),
          ),
        ],
      ),
    );
  }
}