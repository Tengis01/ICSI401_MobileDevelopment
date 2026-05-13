import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import '../../core/utils/currency_formatter.dart';
import '../../models/transaction_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/app_notification_service.dart';

class RechargeBankScreen extends ConsumerStatefulWidget {
  final int amount;

  const RechargeBankScreen({
    super.key,
    required this.amount,
  });

  @override
  ConsumerState<RechargeBankScreen> createState() => _RechargeBankScreenState();
}

class _RechargeBankScreenState extends ConsumerState<RechargeBankScreen> {
  _RechargeBank? _selectedBank;
  bool _isSaving = false;

  Future<void> _finishRecharge() async {
    final bank = _selectedBank;
    if (bank == null || _isSaving) return;

    setState(() => _isSaving = true);

    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) throw Exception('User not logged in');

      final docRef = FirebaseFirestore.instance
          .collection(AppConstants.usersCollection)
          .doc(user.uid)
          .collection(AppConstants.transactionsCollection)
          .doc();
      final now = DateTime.now();
      final tx = TransactionModel(
        id: docRef.id,
        userId: user.uid,
        type: TransactionType.income,
        amount: widget.amount,
        categoryId: 'recharge',
        categoryName: 'Цэнэглэлт',
        categoryIcon: 'wallet',
        note: bank.name,
        date: now,
        createdAt: now,
      );

      await ref.read(transactionServiceProvider).addTransaction(tx);
      await AppNotificationService.instance.addNotification(
        title: 'Данс цэнэглэгдлээ',
        body: '${CurrencyFormatter.format(widget.amount)} · ${bank.name}',
        type: AppNotificationType.income,
      );

      if (!mounted) return;
      context.go(
        AppRoutes.rechargeSuccess,
        extra: {
          'amount': widget.amount,
          'bankName': bank.name,
        },
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Цэнэглэхэд алдаа гарлаа: $e'),
          backgroundColor: AppColors.error,
          behavior: SnackBarBehavior.floating,
        ),
      );
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 24),
        ),
        title: Text(
          '${CurrencyFormatter.formatNoSymbol(widget.amount)}₮ цэнэглэх',
          style: const TextStyle(
            fontSize: 26,
            fontWeight: FontWeight.w800,
            color: AppColors.textPrimary,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.fromLTRB(24, 28, 24, 0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Банкаа сонгоно уу',
              style: const TextStyle(
                fontSize: 30,
                color: AppColors.textSecondary,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 28),
            Expanded(
              child: GridView.builder(
                itemCount: _banks.length,
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 3,
                  crossAxisSpacing: 18,
                  mainAxisSpacing: 28,
                  childAspectRatio: 0.72,
                ),
                itemBuilder: (context, index) {
                  final bank = _banks[index];
                  return _BankOption(
                    bank: bank,
                    isSelected: _selectedBank?.name == bank.name,
                    onTap: () => setState(() => _selectedBank = bank),
                  );
                },
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(24, 16, 24, 24),
          child: SizedBox(
            height: 58,
            child: ElevatedButton(
              onPressed:
                  _selectedBank != null && !_isSaving ? _finishRecharge : null,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                disabledBackgroundColor: AppColors.border,
                foregroundColor: Colors.white,
                elevation: 0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(100),
                ),
              ),
              child: _isSaving
                  ? const SizedBox(
                      width: 22,
                      height: 22,
                      child: CircularProgressIndicator(
                        strokeWidth: 2.4,
                        color: Colors.white,
                      ),
                    )
                  : const Text(
                      'Үргэлжлүүлэх',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
            ),
          ),
        ),
      ),
    );
  }
}

class _BankOption extends StatelessWidget {
  final _RechargeBank bank;
  final bool isSelected;
  final VoidCallback onTap;

  const _BankOption({
    required this.bank,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(18),
      child: Column(
        children: [
          AnimatedContainer(
            duration: const Duration(milliseconds: 180),
            width: 92,
            height: 92,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(18),
              border: Border.all(
                color: isSelected ? bank.color : AppColors.border,
                width: isSelected ? 2 : 1,
              ),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.04),
                  blurRadius: 10,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: Stack(
              alignment: Alignment.center,
              children: [
                Container(
                  width: 58,
                  height: 58,
                  decoration: BoxDecoration(
                    color: bank.color,
                    borderRadius: BorderRadius.circular(16),
                  ),
                  alignment: Alignment.center,
                  child: Text(
                    bank.mark,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.w900,
                    ),
                  ),
                ),
                if (isSelected)
                  Positioned(
                    right: 8,
                    top: 8,
                    child: Icon(
                      Icons.check_circle_rounded,
                      color: bank.color,
                      size: 20,
                    ),
                  ),
              ],
            ),
          ),
          const SizedBox(height: 10),
          Text(
            bank.name,
            style: AppTextStyles.bodyMedium.copyWith(
              color: AppColors.textSecondary,
              fontSize: 15,
              height: 1.2,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}

class _RechargeBank {
  final String name;
  final String mark;
  final Color color;

  const _RechargeBank({
    required this.name,
    required this.mark,
    required this.color,
  });
}

const _banks = [
  _RechargeBank(name: 'Хаан банк', mark: 'Х', color: Color(0xFF007A3D)),
  _RechargeBank(name: 'TDB online', mark: 'T', color: Color(0xFF1397D5)),
  _RechargeBank(name: 'Төрийн банк', mark: 'Т', color: Color(0xFF0F3F8F)),
  _RechargeBank(name: 'Хас банк', mark: 'X', color: Color(0xFFF36C21)),
  _RechargeBank(name: 'Ариг банк', mark: 'А', color: Color(0xFF7E3FB2)),
  _RechargeBank(name: 'Богд банк', mark: 'Б', color: Color(0xFF19924A)),
  _RechargeBank(name: 'M банк', mark: 'M', color: Color(0xFF2DC5A1)),
  _RechargeBank(name: 'MOST мони', mark: 'M', color: Color(0xFF0B8E53)),
  _RechargeBank(name: 'Happy Pay MN', mark: 'H', color: Color(0xFF1955D6)),
];
