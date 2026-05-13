import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import '../../core/constants/bill_data.dart';
import '../../models/transaction_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/app_notification_service.dart';
import '../../services/notification_service.dart';

class BillProcessingScreen extends ConsumerStatefulWidget {
  final String serviceId;
  final int amount;
  final String cardLast4;
  final String bankName;

  const BillProcessingScreen({
    super.key,
    required this.serviceId,
    required this.amount,
    required this.cardLast4,
    required this.bankName,
  });

  @override
  ConsumerState<BillProcessingScreen> createState() => _BillProcessingScreenState();
}

class _BillProcessingScreenState extends ConsumerState<BillProcessingScreen> {
  @override
  void initState() {
    super.initState();
    _processPayment();
  }

  Future<void> _processPayment() async {
    // delay for animation
    await Future.delayed(const Duration(seconds: 2));

    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) throw Exception('User not logged in');

      final transactions = widget.serviceId == 'all'
          ? await _writeAllPendingBills(user.uid)
          : [await _writeSingleBill(user.uid, widget.serviceId, widget.amount)];
      final txId = transactions.first.id;
      final notificationTitle =
          widget.serviceId == 'all' ? 'Бүх төлбөр төлөгдлөө' : 'Төлбөр амжилттай';
      final notificationBody = widget.serviceId == 'all'
          ? '${BillData.pendingCount} төлбөр · ${_fmt(widget.amount)}₮'
          : '${transactions.first.categoryName} · ${_fmt(widget.amount)}₮';

      await NotificationService.instance.showBillPaymentNotification(
        serviceName:
            widget.serviceId == 'all' ? 'Бүх төлбөр' : transactions.first.categoryName,
        amount: widget.amount,
        cardLast4: widget.cardLast4,
      );

      await AppNotificationService.instance.addNotification(
        title: notificationTitle,
        body: '$notificationBody · ${widget.bankName}',
        type: AppNotificationType.bill,
      );

      if (!mounted) return;
      context.pushReplacement(
        AppRoutes.billSuccessPath(widget.serviceId),
        extra: {
          'txId': txId,
          'amount': widget.amount,
          'cardLast4': widget.cardLast4,
          'bankName': widget.bankName,
        },
      );
    } catch (e) {
      if (!mounted) return;
      context.pushReplacement(
        AppRoutes.billFailurePath(widget.serviceId),
        extra: e.toString(),
      );
    }
  }

  Future<TransactionModel> _writeSingleBill(
    String userId,
    String serviceId,
    int amount,
  ) async {
    final bill = BillData.services.firstWhere((s) => s.id == serviceId);
    final tx = _buildBillTransaction(
      userId: userId,
      serviceId: serviceId,
      amount: amount,
      categoryName: bill.name,
      provider: bill.provider,
    );
    await ref.read(transactionServiceProvider).addTransaction(tx);
    return tx;
  }

  Future<List<TransactionModel>> _writeAllPendingBills(String userId) async {
    final transactions = <TransactionModel>[];
    for (final bill in BillData.pendingBills) {
      final tx = _buildBillTransaction(
        userId: userId,
        serviceId: bill.id,
        amount: bill.amount!,
        categoryName: bill.name,
        provider: bill.provider,
      );
      await ref.read(transactionServiceProvider).addTransaction(tx);
      transactions.add(tx);
    }
    return transactions;
  }

  TransactionModel _buildBillTransaction({
    required String userId,
    required String serviceId,
    required int amount,
    required String categoryName,
    required String provider,
  }) {
    final docRef = FirebaseFirestore.instance
        .collection(AppConstants.usersCollection)
        .doc(userId)
        .collection(AppConstants.transactionsCollection)
        .doc();
    final now = DateTime.now();

    return TransactionModel(
      id: docRef.id,
      userId: userId,
      type: TransactionType.expense,
      amount: amount,
      categoryId: 'bill_$serviceId',
      categoryName: categoryName,
      categoryIcon: 'tax',
      note: '$provider · ****${widget.cardLast4}-аас',
      date: now,
      createdAt: now,
    );
  }

  String _fmt(int v) => v.toString().replaceAllMapped(
        RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        automaticallyImplyLeading: false,
        title: const Text('Төлбөр'),
        centerTitle: true,
        actions: [
          IconButton(onPressed: () {}, icon: const Icon(Icons.more_horiz_rounded)),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                color: AppColors.primary,
                shape: BoxShape.circle,
              ),
              child: const Center(
                child: CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                  strokeWidth: 3,
                ),
              ),
            ),
            const SizedBox(height: 32),
            Text('Төлбөр хийгдэж байна', style: AppTextStyles.h3),
            const SizedBox(height: 8),
            Text('Түр хүлээнэ үү...',
                style: AppTextStyles.bodyMedium.copyWith(color: AppColors.textSecondary)),
          ],
        ),
      ),
    );
  }
}
