import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/bill_data.dart';
import '../../core/utils/date_formatter.dart';
import '../../widgets/app_button.dart';

class BillReceiptScreen extends StatefulWidget {
  final String serviceId;
  final String txId;
  final int amount;
  final String cardLast4;
  final String bankName;

  const BillReceiptScreen({
    super.key,
    required this.serviceId,
    required this.txId,
    required this.amount,
    required this.cardLast4,
    required this.bankName,
  });

  @override
  State<BillReceiptScreen> createState() => _BillReceiptScreenState();
}

class _BillReceiptScreenState extends State<BillReceiptScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  late Animation<double> _scaleAnim;
  bool _isExpanded = false;

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _scaleAnim = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
          parent: _animController, curve: Curves.elasticOut),
    );
    _animController.forward();
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

  // receipt QR encode hiih data
  String get _qrData => jsonEncode({
    'type':      'fintrack_receipt',
    'id':        widget.txId,
    'amount':    widget.amount,
    'service':   widget.serviceId,
    'bank':      widget.bankName,
    'card':      '****${widget.cardLast4}',
    'date':      DateTime.now().toIso8601String(),
    'app':       'FinTrack',
  });

  @override
  Widget build(BuildContext context) {
    final bill = BillData.services
        .firstWhere((s) => s.id == widget.serviceId);
    final now = DateTime.now();

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        automaticallyImplyLeading: false,
        title: const Text('Баримт'),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.more_horiz_rounded),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            // success card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  // animated check circle
                  ScaleTransition(
                    scale: _scaleAnim,
                    child: Container(
                      width: 72, height: 72,
                      decoration: const BoxDecoration(
                        color: AppColors.success,
                        shape: BoxShape.circle,
                      ),
                      child: const Icon(
                        Icons.check_rounded,
                        color: Colors.white, size: 36,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text('Амжилттай шилжүүлэв',
                      style: AppTextStyles.h3),
                  const SizedBox(height: 8),
                  Text(
                    '${_fmt(widget.amount)}₮',
                    style: const TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.w700,
                      color: AppColors.textPrimary,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            // urtiin delgerengui
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  _ReceiptRow(
                      label: 'Хүлээн авагч',
                      value: bill.provider),
                  const Divider(height: 1, indent: 16),
                  _ReceiptRow(
                      label: 'Гүйлгээний дугаар',
                      value: widget.txId.substring(0, 8).toUpperCase()),
                  const Divider(height: 1, indent: 16),
                  _ReceiptRow(
                      label: 'Огноо',
                      value: DateFormatter.formatFull(now)),
                  const Divider(height: 1, indent: 16),
                  _ReceiptRow(
                      label: 'Хэтэвч',
                      value:
                      '${widget.bankName} • ${widget.cardLast4}'),
                  const Divider(height: 1, indent: 16),
                  _ReceiptRow(
                      label: 'Шимтгэл',
                      value: '0 ₮',
                      valueColor: AppColors.income),
                  const Divider(height: 1, indent: 16),
                  _ReceiptRow(
                      label: 'Нийт',
                      value: '${_fmt(widget.amount)}₮',
                      isBold: true),
                  // delgerengui expand
                  InkWell(
                    onTap: () =>
                        setState(() => _isExpanded = !_isExpanded),
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16, vertical: 12),
                      child: Row(
                        mainAxisAlignment:
                        MainAxisAlignment.center,
                        children: [
                          Text(
                            _isExpanded ? 'Хураах' : 'Дэлгэрэнгүй',
                            style: AppTextStyles.labelMedium.copyWith(
                                color: AppColors.primary),
                          ),
                          const SizedBox(width: 4),
                          AnimatedRotation(
                            turns: _isExpanded ? 0.5 : 0,
                            duration:
                            const Duration(milliseconds: 200),
                            child: Icon(
                              Icons.keyboard_arrow_down_rounded,
                              color: AppColors.primary,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  // QR code - expand hiiheer haruulna
                  AnimatedSize(
                    duration: const Duration(milliseconds: 300),
                    curve: Curves.easeInOut,
                    child: _isExpanded
                        ? Column(
                      children: [
                        const Divider(height: 1),
                        Padding(
                          padding: const EdgeInsets.all(20),
                          child: Column(
                            children: [
                              Text('FinTrack баримт',
                                  style: AppTextStyles.labelLarge),
                              Text(
                                DateFormatter.formatFull(now),
                                style: AppTextStyles.bodySmall,
                              ),
                              const SizedBox(height: 16),
                              // qr_flutter - auto generate
                              QrImageView(
                                data: _qrData,
                                version: QrVersions.auto,
                                size: 160,
                                backgroundColor: Colors.white,
                              ),
                              const SizedBox(height: 8),
                              Text(
                                widget.txId
                                    .substring(0, 8)
                                    .toUpperCase(),
                                style: AppTextStyles.bodySmall
                                    .copyWith(fontSize: 10),
                              ),
                            ],
                          ),
                        ),
                      ],
                    )
                        : const SizedBox.shrink(),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            // share boloh tatah button
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () {},
                    icon: const Icon(Icons.share_outlined, size: 18),
                    label: const Text('Хуваалцах'),
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(color: AppColors.primary),
                      foregroundColor: AppColors.primary,
                      minimumSize: const Size.fromHeight(52),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(100)),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: AppButton(
                    label: 'Нүүр хуудас',
                    onPressed: () => context.go(AppRoutes.home),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}

class _ReceiptRow extends StatelessWidget {
  final String label;
  final String value;
  final Color? valueColor;
  final bool isBold;

  const _ReceiptRow({
    required this.label,
    required this.value,
    this.valueColor,
    this.isBold = false,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(
          horizontal: 16, vertical: 13),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: AppTextStyles.bodyMedium.copyWith(
                  color: AppColors.textSecondary)),
          Text(value,
              style: AppTextStyles.labelMedium.copyWith(
                color: valueColor ?? AppColors.textPrimary,
                fontWeight: isBold
                    ? FontWeight.w700
                    : FontWeight.w500,
              )),
        ],
      ),
    );
  }
}