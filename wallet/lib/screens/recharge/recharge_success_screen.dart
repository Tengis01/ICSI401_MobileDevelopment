import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/utils/currency_formatter.dart';
import '../../widgets/app_button.dart';

class RechargeSuccessScreen extends StatelessWidget {
  final int amount;
  final String bankName;

  const RechargeSuccessScreen({
    super.key,
    required this.amount,
    required this.bankName,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              const Spacer(),
              Container(
                width: 88,
                height: 88,
                decoration: const BoxDecoration(
                  color: AppColors.success,
                  shape: BoxShape.circle,
                ),
                child: const Icon(
                  Icons.check_rounded,
                  color: Colors.white,
                  size: 48,
                ),
              ),
              const SizedBox(height: 24),
              Text('Данс цэнэглэгдлээ', style: AppTextStyles.h2),
              const SizedBox(height: 10),
              Text(
                '${CurrencyFormatter.format(amount)} амжилттай нэмэгдлээ',
                style: AppTextStyles.bodyMedium.copyWith(
                  color: AppColors.textSecondary,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 28),
              Container(
                padding: const EdgeInsets.all(18),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  children: [
                    _Row(label: 'Дүн', value: CurrencyFormatter.format(amount)),
                    const SizedBox(height: 12),
                    _Row(label: 'Банк', value: bankName),
                  ],
                ),
              ),
              const Spacer(),
              AppButton(
                label: 'Нүүр хуудас',
                onPressed: () => context.go(AppRoutes.home),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _Row extends StatelessWidget {
  final String label;
  final String value;

  const _Row({
    required this.label,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: AppTextStyles.bodyMedium.copyWith(
            color: AppColors.textSecondary,
          ),
        ),
        Text(value, style: AppTextStyles.labelMedium),
      ],
    );
  }
}
