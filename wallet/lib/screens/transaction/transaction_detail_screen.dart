import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/category_data.dart';
import '../../core/utils/currency_formatter.dart';
import '../../core/utils/date_formatter.dart';
import '../../models/transaction_model.dart';
import '../../providers/auth_provider.dart';
import '../../providers/transaction_provider.dart';

class TransactionDetailScreen extends ConsumerWidget {
  final String transactionId;

  const TransactionDetailScreen({
    super.key,
    required this.transactionId,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // recentTransactions stream-ees id-aar haih
    final transactionsAsync = ref.watch(recentTransactionsProvider);

    return transactionsAsync.when(
      data: (transactions) {
        final tx = transactions
            .where((t) => t.id == transactionId)
            .firstOrNull;

        if (tx == null) {
          return Scaffold(
            appBar: AppBar(
              leading: IconButton(
                onPressed: () => context.pop(),
                icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
              ),
            ),
            body: const Center(
              child: Text('Гүйлгээ олдсонгүй'),
            ),
          );
        }

        return _TransactionDetailView(transaction: tx);
      },
      loading: () => const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      ),
      error: (e, _) => Scaffold(
        body: Center(child: Text('Алдаа: $e')),
      ),
    );
  }
}

class _TransactionDetailView extends ConsumerWidget {
  final TransactionModel transaction;

  const _TransactionDetailView({required this.transaction});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isIncome = transaction.type == TransactionType.income;
    final amountColor = isIncome ? AppColors.income : AppColors.expense;
    final iconData = CategoryData.getIconData(transaction.categoryIcon);
    final catColor = CategoryData.getColor(
        transaction.categoryIcon, transaction.type);

    Future<void> deleteTransaction() async {
      final confirmed = await showDialog<bool>(
        context: context,
        builder: (ctx) => AlertDialog(
          shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16)),
          title: const Text('Устгах уу?'),
          content: const Text(
              'Энэ гүйлгээг устгах уу? Буцаах боломжгүй.'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Болих'),
            ),
            TextButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: Text('Устгах',
                  style: TextStyle(color: AppColors.error)),
            ),
          ],
        ),
      );

      if (confirmed != true) return;

      final user = ref.read(currentUserProvider);
      if (user == null) return;

      await ref
          .read(transactionServiceProvider)
          .deleteTransaction(user.uid, transaction.id);

      if (!context.mounted) return;
      context.go(AppRoutes.home);
    }

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Гүйлгээний дэлгэрэнгүй'),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // amount card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  Container(
                    width: 64, height: 64,
                    decoration: BoxDecoration(
                      color: catColor.withValues(alpha:0.15),
                      borderRadius: BorderRadius.circular(18),
                    ),
                    child: Icon(iconData, color: catColor, size: 30),
                  ),
                  const SizedBox(height: 12),
                  Text(
                    isIncome ? 'Орлого' : 'Зарлага',
                    style: AppTextStyles.bodySmall,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    CurrencyFormatter.formatWithSign(
                        transaction.amount, isIncome: isIncome),
                    style: TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.w700,
                      color: amountColor,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            // details card
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  _DetailRow(
                    icon: Icons.category_outlined,
                    label: 'Ангилал',
                    value: transaction.categoryName,
                  ),
                  const Divider(height: 1, indent: 52),
                  _DetailRow(
                    icon: Icons.calendar_today_outlined,
                    label: 'Огноо',
                    value: DateFormatter.formatFull(transaction.date),
                  ),
                  if (transaction.note != null) ...[
                    const Divider(height: 1, indent: 52),
                    _DetailRow(
                      icon: Icons.notes_rounded,
                      label: 'Тэмдэглэл',
                      value: transaction.note!,
                    ),
                  ],
                ],
              ),
            ),
            const SizedBox(height: 32),
            // ustgah button
            SizedBox(
              width: double.infinity,
              height: 52,
              child: OutlinedButton.icon(
                onPressed: deleteTransaction,
                icon: const Icon(Icons.delete_outline_rounded,
                    color: AppColors.error),
                label: const Text('Устгах',
                    style: TextStyle(color: AppColors.error)),
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: AppColors.error),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(100)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _DetailRow({
    required this.icon,
    required this.label,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      child: Row(
        children: [
          Container(
            width: 36, height: 36,
            decoration: BoxDecoration(
              color: AppColors.primarySurface,
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(icon, color: AppColors.primary, size: 18),
          ),
          const SizedBox(width: 12),
          Text(label,
              style: AppTextStyles.bodyMedium.copyWith(
                  color: AppColors.textSecondary)),
          const Spacer(),
          Flexible(
            child: Text(
              value,
              style: AppTextStyles.labelMedium,
              textAlign: TextAlign.end,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }
}