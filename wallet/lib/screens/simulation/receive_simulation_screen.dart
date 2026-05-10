import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import '../../models/transaction_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/notification_service.dart';
import '../../widgets/app_button.dart';

class ReceiveSimulationScreen extends ConsumerStatefulWidget {
  const ReceiveSimulationScreen({super.key});

  @override
  ConsumerState<ReceiveSimulationScreen> createState() =>
      _ReceiveSimulationScreenState();
}

class _ReceiveSimulationScreenState
    extends ConsumerState<ReceiveSimulationScreen> {
  final _senderController = TextEditingController();
  final _amountController = TextEditingController();
  final _noteController   = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _senderController.dispose();
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  bool get _canReceive =>
      _senderController.text.trim().isNotEmpty &&
          _amountController.text.isNotEmpty &&
          (int.tryParse(_amountController.text) ?? 0) > 0;

  Future<void> _confirmAndReceive() async {
    if (!_canReceive) return;

    final amount = int.parse(_amountController.text);
    final sender = _senderController.text.trim();
    final note   = _noteController.text.trim();

    final confirmed = await showModalBottomSheet<bool>(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => _ReceiveConfirmSheet(
        sender: sender,
        amount: amount,
        onCancel: () => Navigator.pop(ctx, false),
        onConfirm: () => Navigator.pop(ctx, true),
      ),
    );

    if (confirmed != true) return;
    setState(() => _isLoading = true);

    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) return;

      final docRef = FirebaseFirestore.instance
          .collection(AppConstants.usersCollection)
          .doc(user.uid)
          .collection(AppConstants.transactionsCollection)
          .doc();

      final tx = TransactionModel(
        id: docRef.id,
        userId: user.uid,
        type: TransactionType.income,
        amount: amount,
        categoryId: 'transfer',
        categoryName: 'Шилжүүлэг',
        categoryIcon: 'transfer',
        note: note.isEmpty ? '$sender-аас шилжүүлэг' : note,
        date: DateTime.now(),
        createdAt: DateTime.now(),
      );

      await ref.read(transactionServiceProvider).addTransaction(tx);

      await NotificationService.instance.showTransferNotification(
        recipientName: sender,
        amount: amount,
        isSend: false,
      );

      if (!mounted) return;
      context.go(AppRoutes.home);
    } finally {
      if (mounted) setState(() => _isLoading = false);
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
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Мөнгө авах'),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFFFFF8E1),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(
                    color: const Color(0xFFFFCA28), width: 0.5),
              ),
              child: Row(
                children: [
                  const Icon(Icons.info_outline_rounded,
                      color: Color(0xFFF59E0B), size: 18),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'Туршилтын горим — зөвхөн орлогын бүртгэл хадгалагдана.',
                      style: AppTextStyles.bodySmall.copyWith(
                          color: const Color(0xFFB45309)),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 28),
            _buildLabel('ИЛГЭЭГЧ'),
            const SizedBox(height: 6),
            TextField(
              controller: _senderController,
              style: AppTextStyles.bodyLarge,
              onChanged: (_) => setState(() {}),
              decoration: const InputDecoration(
                hintText: 'Нэр эсвэл утасны дугаар',
                prefixIcon: Icon(Icons.person_outline_rounded,
                    color: AppColors.textSecondary, size: 20),
              ),
            ),
            const SizedBox(height: 20),
            _buildLabel('ДҮН'),
            const SizedBox(height: 6),
            TextField(
              controller: _amountController,
              keyboardType: TextInputType.number,
              inputFormatters: [FilteringTextInputFormatter.digitsOnly],
              style: AppTextStyles.bodyLarge,
              onChanged: (_) => setState(() {}),
              decoration: const InputDecoration(
                hintText: '0',
                suffixText: '₮',
                prefixIcon: Icon(Icons.attach_money_rounded,
                    color: AppColors.textSecondary, size: 20),
              ),
            ),
            const SizedBox(height: 20),
            _buildLabel('ТЭМДЭГЛЭЛ (заавал биш)'),
            const SizedBox(height: 6),
            TextField(
              controller: _noteController,
              style: AppTextStyles.bodyLarge,
              decoration: const InputDecoration(
                hintText: 'Тэмдэглэл нэмэх...',
                prefixIcon: Icon(Icons.notes_rounded,
                    color: AppColors.textSecondary, size: 20),
              ),
            ),
            const SizedBox(height: 36),
            ListenableBuilder(
              listenable: Listenable.merge(
                  [_senderController, _amountController]),
              builder: (context, _) => AppButton(
                label: 'Авах',
                onPressed: _canReceive ? _confirmAndReceive : null,
                isLoading: _isLoading,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLabel(String text) {
    return Text(text,
        style: AppTextStyles.labelSmall.copyWith(
          color: AppColors.textSecondary,
          fontSize: 11,
          letterSpacing: 0.8,
        ));
  }
}

class _ReceiveConfirmSheet extends StatelessWidget {
  final String sender;
  final int amount;
  final VoidCallback onCancel;
  final VoidCallback onConfirm;

  const _ReceiveConfirmSheet({
    required this.sender,
    required this.amount,
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
              color: AppColors.incomeLight,
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.arrow_downward_rounded,
                color: AppColors.income, size: 30),
          ),
          const SizedBox(height: 12),
          Text('Мөнгө авах баталгаажуулах',
              style: AppTextStyles.h3),
          const SizedBox(height: 4),
          Text(
            '${_fmt(amount)}₮ · $sender-аас',
            style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary),
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
                _InfoRow(label: 'Илгээгч', value: sender),
                const Divider(height: 16),
                _InfoRow(
                    label: 'Дүн',
                    value: '${_fmt(amount)}₮',
                    valueColor: AppColors.income),
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
                  child: const Text('Болих',
                      style: TextStyle(color: AppColors.textPrimary)),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton(
                  onPressed: onConfirm,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.income,
                    minimumSize: const Size.fromHeight(52),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(100)),
                    elevation: 0,
                  ),
                  child: const Text('Авах',
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

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;
  final Color? valueColor;

  const _InfoRow(
      {required this.label, required this.value, this.valueColor});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label,
            style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.textSecondary)),
        Text(value,
            style: AppTextStyles.labelMedium.copyWith(
                color: valueColor ?? AppColors.textPrimary)),
      ],
    );
  }
}