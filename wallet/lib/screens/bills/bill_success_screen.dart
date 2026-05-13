import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/bill_data.dart';
import '../../widgets/app_button.dart';

class BillSuccessScreen extends StatelessWidget {
  final String serviceId;
  final String txId;
  final int amount;
  final String cardLast4;
  final String bankName;

  const BillSuccessScreen({
    super.key,
    required this.serviceId,
    required this.txId,
    required this.amount,
    required this.cardLast4,
    required this.bankName,
  });

  String _fmt(int v) => v.toString().replaceAllMapped(
        RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
      );

  @override
  Widget build(BuildContext context) {
    final provider = serviceId == 'all'
        ? 'Бүх үйлчилгээ'
        : BillData.services.firstWhere((s) => s.id == serviceId).provider;
    
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        automaticallyImplyLeading: false,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 80,
              height: 80,
              decoration: const BoxDecoration(
                color: AppColors.success,
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.check_rounded, color: Colors.white, size: 40),
            ),
            const SizedBox(height: 24),
            Text('Төлбөр амжилттай!', style: AppTextStyles.h2),
            const SizedBox(height: 8),
            Text('$provider-д ${_fmt(amount)}₮ амжилттай төлөгдлөө',
                style: AppTextStyles.bodyMedium.copyWith(color: AppColors.textSecondary)),
            const SizedBox(height: 32),
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 40),
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  _Row(label: 'Гүйлгээний №', value: 'TX${txId.substring(0, 6).toUpperCase()}'),
                  const SizedBox(height: 12),
                  _Row(label: 'Огноо', value: _formatDate()),
                ],
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              AppButton(
                label: 'Баримт харах',
                onPressed: () => context.pushReplacement(
                  AppRoutes.billReceiptPath(serviceId),
                  extra: {
                    'txId': txId,
                    'amount': amount,
                    'cardLast4': cardLast4,
                    'bankName': bankName,
                  },
                ),
              ),
              const SizedBox(height: 16),
              TextButton(
                onPressed: () => context.go(AppRoutes.home),
                child: Text('Нүүр буцах', style: AppTextStyles.labelMedium.copyWith(color: AppColors.textSecondary)),
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _formatDate() {
    final now = DateTime.now();
    return '${now.year}/${now.month.toString().padLeft(2, '0')}/${now.day.toString().padLeft(2, '0')}';
  }
}

class _Row extends StatelessWidget {
  final String label;
  final String value;
  const _Row({required this.label, required this.value});
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTextStyles.bodyMedium.copyWith(color: AppColors.textSecondary)),
        Text(value, style: AppTextStyles.labelMedium),
      ],
    );
  }
}
