import 'dart:convert';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import '../../core/constants/bill_data.dart';
import '../../models/transaction_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/notification_service.dart';
import '../../widgets/app_button.dart';

// kart model - wallet_service duussan ued solgono
class _LocalCard {
  final String id;
  final String bankName;
  final String last4;
  final int balance;

  const _LocalCard({
    required this.id,
    required this.bankName,
    required this.last4,
    required this.balance,
  });
}

class BillMethodScreen extends ConsumerStatefulWidget {
  final String serviceId;
  final int amount;

  const BillMethodScreen({
    super.key,
    required this.serviceId,
    required this.amount,
  });

  @override
  ConsumerState<BillMethodScreen> createState() =>
      _BillMethodScreenState();
}

class _BillMethodScreenState extends ConsumerState<BillMethodScreen> {
  String? _selectedCardId;
  bool _isLoading = false;

  // demo kart - wallet_service-ees avah
  final List<_LocalCard> _cards = const [
    _LocalCard(
        id: 'khan',
        bankName: 'Khan Bank',
        last4: '4521',
        balance: 2847500),
    _LocalCard(
        id: 'tdb',
        bankName: 'TDB',
        last4: '8932',
        balance: 1250000),
  ];

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

  Future<void> _pay() async {
    if (_selectedCardId == null) return;
    final card =
    _cards.firstWhere((c) => c.id == _selectedCardId);

    // confirm bottom sheet
    final confirmed = await showModalBottomSheet<bool>(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => _ConfirmSheet(
        bill: BillData.services
            .firstWhere((s) => s.id == widget.serviceId),
        amount: widget.amount,
        cardLast4: card.last4,
        bankName: card.bankName,
        onCancel: () => Navigator.pop(ctx, false),
        onConfirm: () => Navigator.pop(ctx, true),
      ),
    );

    if (confirmed != true) return;

    setState(() => _isLoading = true);

    // processing simulation - 1.5 second loading
    await Future.delayed(const Duration(milliseconds: 1500));

    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) return;

      final bill = BillData.services
          .firstWhere((s) => s.id == widget.serviceId);

      final docRef = FirebaseFirestore.instance
          .collection(AppConstants.usersCollection)
          .doc(user.uid)
          .collection(AppConstants.transactionsCollection)
          .doc();

      final tx = TransactionModel(
        id: docRef.id,
        userId: user.uid,
        type: TransactionType.expense,
        amount: widget.amount,
        categoryId: 'bill_${widget.serviceId}',
        categoryName: bill.name,
        categoryIcon: 'tax',
        note: '${bill.provider} · ****${card.last4}-аас',
        date: DateTime.now(),
        createdAt: DateTime.now(),
      );

      await ref.read(transactionServiceProvider).addTransaction(tx);

      // notification
      await NotificationService.instance.showBillPaymentNotification(
        serviceName: bill.name,
        amount: widget.amount,
        cardLast4: card.last4,
      );

      if (!mounted) return;
      // receipt screen ruu navigate, tx ID damjuulah
      context.pushReplacement(
        AppRoutes.billReceipt(widget.serviceId),
        extra: {
          'txId':     tx.id,
          'amount':   widget.amount,
          'cardLast4': card.last4,
          'bankName':  card.bankName,
        },
      );
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
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
        title: const Text('Төлбөрийн арга'),
        centerTitle: true,
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 12),
            child: Text('ХААНААС ТӨЛӨХ',
                style: AppTextStyles.labelSmall.copyWith(
                  color: AppColors.textSecondary,
                  letterSpacing: 0.8,
                  fontSize: 11,
                )),
          ),
          // kart list
          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Column(
                children: [
                  Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Column(
                      children: _cards.asMap().entries.map((entry) {
                        final index = entry.key;
                        final card = entry.value;
                        final isSelected = _selectedCardId == card.id;

                        return Column(
                          children: [
                            if (index > 0)
                              const Divider(height: 1, indent: 64),
                            InkWell(
                              onTap: () => setState(
                                      () => _selectedCardId = card.id),
                              borderRadius: BorderRadius.circular(16),
                              child: Padding(
                                padding: const EdgeInsets.all(16),
                                child: Row(
                                  children: [
                                    Container(
                                      width: 44, height: 44,
                                      decoration: BoxDecoration(
                                        color: AppColors.primarySurface,
                                        borderRadius:
                                        BorderRadius.circular(12),
                                      ),
                                      child: Icon(
                                        Icons.credit_card_rounded,
                                        color: AppColors.primary,
                                        size: 22,
                                      ),
                                    ),
                                    const SizedBox(width: 12),
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                        children: [
                                          Text(card.bankName,
                                              style:
                                              AppTextStyles.labelMedium),
                                          Text(
                                            '**** ${card.last4} · Үлдэгдэл ${_fmt(card.balance)}₮',
                                            style: AppTextStyles.bodySmall,
                                          ),
                                        ],
                                      ),
                                    ),
                                    AnimatedContainer(
                                      duration: const Duration(
                                          milliseconds: 200),
                                      width: 22, height: 22,
                                      decoration: BoxDecoration(
                                        shape: BoxShape.circle,
                                        border: Border.all(
                                          color: isSelected
                                              ? AppColors.primary
                                              : AppColors.border,
                                          width: isSelected ? 6 : 1.5,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ],
                        );
                      }).toList(),
                    ),
                  ),
                ],
              ),
            ),
          ),
          // pay button
          Padding(
            padding: const EdgeInsets.all(20),
            child: AppButton(
              label: _selectedCardId == null
                  ? 'Карт сонгоно уу'
                  : '${_fmt(widget.amount)}₮ төлөх',
              onPressed: _selectedCardId != null ? _pay : null,
              isLoading: _isLoading,
            ),
          ),
        ],
      ),
    );
  }
}

class _ConfirmSheet extends StatelessWidget {
  final dynamic bill;
  final int amount;
  final String cardLast4;
  final String bankName;
  final VoidCallback onCancel;
  final VoidCallback onConfirm;

  const _ConfirmSheet({
    required this.bill,
    required this.amount,
    required this.cardLast4,
    required this.bankName,
    required this.onCancel,
    required this.onConfirm,
  });

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 40, height: 4,
            decoration: BoxDecoration(
              color: AppColors.border,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(height: 20),
          Container(
            width: 64, height: 64,
            decoration: BoxDecoration(
              color: bill.iconColor.withOpacity(0.12),
              shape: BoxShape.circle,
            ),
            child: Icon(bill.icon, color: bill.iconColor, size: 30),
          ),
          const SizedBox(height: 12),
          Text('Төлбөр баталгаажуулах',
              style: AppTextStyles.h3),
          const SizedBox(height: 4),
          Text(
            '${bill.name} руу $bankName ****$cardLast4-аас',
            style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 20),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.surfaceVariant,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              children: [
                _Row(label: 'Дүн',
                    value: '${_fmt(amount)}₮',
                    valueColor: AppColors.expense),
                const Divider(height: 16),
                _Row(label: 'Шимтгэл',
                    value: 'Үнэгүй',
                    valueColor: AppColors.income),
                const Divider(height: 16),
                _Row(label: 'Нийт',
                    value: '${_fmt(amount)}₮',
                    isBold: true),
              ],
            ),
          ),
          const SizedBox(height: 24),
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: onCancel,
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppColors.border),
                    minimumSize: const Size.fromHeight(52),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(100)),
                  ),
                  child: const Text('Цуцлах',
                      style: TextStyle(color: AppColors.textPrimary)),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton(
                  onPressed: onConfirm,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    minimumSize: const Size.fromHeight(52),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(100)),
                    elevation: 0,
                  ),
                  child: const Text('Төлөх',
                      style: TextStyle(color: Colors.white)),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
        ],
      ),
    );
  }
}

class _Row extends StatelessWidget {
  final String label;
  final String value;
  final Color? valueColor;
  final bool isBold;

  const _Row({
    required this.label,
    required this.value,
    this.valueColor,
    this.isBold = false,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label,
            style: isBold
                ? AppTextStyles.labelMedium
                : AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary)),
        Text(value,
            style: AppTextStyles.labelMedium.copyWith(
                color: valueColor ?? AppColors.textPrimary,
                fontWeight: isBold
                    ? FontWeight.w700
                    : FontWeight.w500)),
      ],
    );
  }
}