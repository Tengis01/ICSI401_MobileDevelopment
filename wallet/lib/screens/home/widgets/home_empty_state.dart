import 'package:flutter/material.dart';
import '../../../app/theme/app_colors.dart';
import '../../../app/theme/app_text_styles.dart';
import '../../../widgets/app_button.dart';

class HomeEmptyState extends StatelessWidget {
  final VoidCallback onAdd;

  const HomeEmptyState({super.key, required this.onAdd});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 40),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 80, height: 80,
              decoration: BoxDecoration(
                color: AppColors.primaryLight,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.account_balance_wallet_rounded,
                color: AppColors.primary, size: 36,
              ),
            ),
            const SizedBox(height: 20),
            Text('Гүйлгээ алга байна',
                style: AppTextStyles.h3, textAlign: TextAlign.center),
            const SizedBox(height: 8),
            Text(
              'Эхний гүйлгээгээ нэмэхэд таны санхүүгийн зураг энд харагдах болно.',
              style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary, height: 1.6,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 28),
            AppButton(label: '+ Гүйлгээ нэмэх', onPressed: onAdd),
          ],
        ),
      ),
    );
  }
}