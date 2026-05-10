import 'package:flutter/material.dart';
import '../../../app/theme/app_colors.dart';
import '../../../app/theme/app_text_styles.dart';

class QuickActionsRow extends StatelessWidget {
  final VoidCallback onSend;
  final VoidCallback onReceive;
  final VoidCallback onTopUp;
  final VoidCallback onMore;

  const QuickActionsRow({
    super.key,
    required this.onSend,
    required this.onReceive,
    required this.onTopUp,
    required this.onMore,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        _ActionItem(icon: Icons.arrow_upward_rounded,
            label: 'Илгээх', onTap: onSend),
        _ActionItem(icon: Icons.arrow_downward_rounded,
            label: 'Авах', onTap: onReceive),
        _ActionItem(icon: Icons.phone_android_rounded,
            label: 'Цэнэглэх', onTap: onTopUp),
        _ActionItem(icon: Icons.more_horiz_rounded,
            label: 'Илүү', onTap: onMore),
      ],
    );
  }
}

class _ActionItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _ActionItem({
    required this.icon,
    required this.label,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        children: [
          Container(
            width: 52, height: 52,
            decoration: BoxDecoration(
              color: AppColors.primarySurface,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(icon, color: AppColors.primary, size: 24),
          ),
          const SizedBox(height: 6),
          Text(label,
              style: AppTextStyles.bodySmall.copyWith(fontSize: 11)),
        ],
      ),
    );
  }
}