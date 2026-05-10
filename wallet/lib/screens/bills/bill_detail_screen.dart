import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/bill_data.dart';
import '../../models/bill_model.dart';
import '../../widgets/app_button.dart';

class BillDetailScreen extends StatefulWidget {
  final String serviceId;

  const BillDetailScreen({super.key, required this.serviceId});

  @override
  State<BillDetailScreen> createState() => _BillDetailScreenState();
}

class _BillDetailScreenState extends State<BillDetailScreen> {
  final _customAmountController = TextEditingController();

  @override
  void dispose() {
    _customAmountController.dispose();
    super.dispose();
  }

  BillModel get bill =>
      BillData.services.firstWhere((s) => s.id == widget.serviceId);

  bool get _canProceed =>
      !bill.isCustomAmount ||
          (_customAmountController.text.isNotEmpty &&
              (int.tryParse(_customAmountController.text) ?? 0) > 0);

  int get _finalAmount =>
      bill.isCustomAmount
          ? int.tryParse(_customAmountController.text) ?? 0
          : bill.amount!;

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

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
        title: Text(bill.name),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // amount card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  Container(
                    width: 56, height: 56,
                    decoration: BoxDecoration(
                      color: bill.iconColor.withOpacity(0.12),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Icon(bill.icon,
                        color: bill.iconColor, size: 28),
                  ),
                  const SizedBox(height: 12),
                  Text('Нийт төлбөл зохих',
                      style: AppTextStyles.bodySmall),
                  const SizedBox(height: 4),
                  // custom amount baiwal input haruulah
                  if (bill.isCustomAmount)
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        IntrinsicWidth(
                          child: TextField(
                            controller: _customAmountController,
                            keyboardType: TextInputType.number,
                            inputFormatters: [
                              FilteringTextInputFormatter.digitsOnly
                            ],
                            textAlign: TextAlign.center,
                            style: const TextStyle(
                              fontSize: 28,
                              fontWeight: FontWeight.w700,
                              color: AppColors.textPrimary,
                            ),
                            onChanged: (_) => setState(() {}),
                            decoration: const InputDecoration(
                              hintText: '0',
                              border: InputBorder.none,
                              enabledBorder: InputBorder.none,
                              focusedBorder: InputBorder.none,
                              filled: false,
                              contentPadding: EdgeInsets.zero,
                              isDense: true,
                            ),
                          ),
                        ),
                        const Text(' ₮',
                            style: TextStyle(
                                fontSize: 20,
                                color: AppColors.textSecondary)),
                      ],
                    )
                  else
                    Text(
                      '${_fmt(bill.amount!)}₮',
                      style: const TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.w700,
                        color: AppColors.textPrimary,
                      ),
                    ),
                  if (bill.dueDate != null) ...[
                    const SizedBox(height: 4),
                    Text(
                      'Сүүлчийн хугацаа: ${bill.dueDate}',
                      style: AppTextStyles.bodySmall.copyWith(
                          color: AppColors.expense),
                    ),
                  ],
                ],
              ),
            ),
            const SizedBox(height: 16),
            // delgerengui
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  _InfoRow(
                      label: 'Үйлчилгээ', value: bill.provider),
                  if (bill.accountNumber != null) ...[
                    const Divider(height: 1, indent: 16),
                    _InfoRow(
                        label: 'Гэрээний дугаар',
                        value: bill.accountNumber!),
                  ],
                  if (!bill.isCustomAmount) ...[
                    const Divider(height: 1, indent: 16),
                    _InfoRow(
                        label: 'Дүн',
                        value: '${_fmt(bill.amount!)}₮'),
                  ],
                  const Divider(height: 1, indent: 16),
                  _InfoRow(
                      label: 'Тооцооны сар',
                      value: '2026 оны 4-р сар'),
                ],
              ),
            ),
            const SizedBox(height: 32),
            StatefulBuilder(
              builder: (context, _) => AppButton(
                label: 'Үргэлжлүүлэх',
                onPressed: _canProceed
                    ? () => context.push(
                  AppRoutes.billMethod(widget.serviceId),
                  extra: _finalAmount,
                )
                    : null,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final String label;
  final String value;

  const _InfoRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: AppTextStyles.bodyMedium.copyWith(
                  color: AppColors.textSecondary)),
          Text(value, style: AppTextStyles.labelMedium),
        ],
      ),
    );
  }
}