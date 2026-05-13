import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../core/constants/bill_data.dart';
import '../../models/wallet_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/wallet_service.dart';
import '../../widgets/app_button.dart';

class BillMethodScreen extends ConsumerStatefulWidget {
  final String serviceId;
  final int amount;

  const BillMethodScreen({
    super.key,
    required this.serviceId,
    required this.amount,
  });

  @override
  ConsumerState<BillMethodScreen> createState() => _BillMethodScreenState();
}

class _BillMethodScreenState extends ConsumerState<BillMethodScreen> {
  String? _selectedId; // 'app_wallet' or WalletCard.id
  List<WalletCard> _walletCards = [];
  bool _isLoadingCards = true;

  String _fmt(int v) => v.toString().replaceAllMapped(
        RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
      );

  @override
  void initState() {
    super.initState();
    _loadCards();
  }

  Future<void> _loadCards() async {
    final cards = await WalletService.instance.getCards();
    if (mounted) {
      setState(() {
        _walletCards = cards;
        _isLoadingCards = false;
      });
    }
  }

  Future<void> _pay() async {
    if (_selectedId == null) return;

    String cardLast4;
    String bankName;

    if (_selectedId == 'app_wallet') {
      cardLast4 = '—';
      bankName = 'App Хэтэвч';
    } else {
      final card = _walletCards.firstWhere((c) => c.id == _selectedId);
      cardLast4 = card.last4;
      bankName = card.bankName;
    }

    final confirmed = await showModalBottomSheet<bool>(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => _ConfirmSheet(
        bill: _displayFor(widget.serviceId),
        amount: widget.amount,
        cardLast4: cardLast4,
        bankName: bankName,
        onCancel: () => Navigator.pop(ctx, false),
        onConfirm: () => Navigator.pop(ctx, true),
      ),
    );

    if (confirmed != true) return;
    if (!mounted) return;

    context.push(
      AppRoutes.billProcessingPath(widget.serviceId),
      extra: {
        'amount': widget.amount,
        'cardLast4': cardLast4,
        'bankName': bankName,
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final appWalletBalance = ref.watch(totalBalanceProvider).valueOrNull ?? 0;

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
      body: _isLoadingCards
          ? const Center(child: CircularProgressIndicator())
          : Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Padding(
                  padding: EdgeInsets.only(left: 16, right: 16, top: 20, bottom: 12),
                  child: Text(
                    'ХААНААС ТӨЛӨХ',
                    style: TextStyle(
                      fontSize: 11,
                      letterSpacing: 0.8,
                      color: AppColors.textSecondary,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    child: Container(
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          // --- App Wallet option ---
                          _PaymentRow(
                            id: 'app_wallet',
                            selectedId: _selectedId,
                            icon: Icons.account_balance_wallet_rounded,
                            iconBg: const Color(0xFFEDE8FF),
                            iconColor: const Color(0xFF6C47FF),
                            title: 'App Хэтэвч',
                            subtitle: 'Үлдэгдэл ${_fmt(appWalletBalance)}₮',
                            isFirst: true,
                            isLast: _walletCards.isEmpty,
                            onTap: () => setState(() => _selectedId = 'app_wallet'),
                          ),
                          // --- Real wallet cards ---
                          ..._walletCards.asMap().entries.map((entry) {
                            final i = entry.key;
                            final card = entry.value;
                            return _PaymentRow(
                              id: card.id,
                              selectedId: _selectedId,
                              icon: Icons.credit_card_rounded,
                              iconBg: const Color(0xFFF5F3FF),
                              iconColor: const Color(0xFF6C47FF),
                              title: card.bankName,
                              subtitle: '**** ${card.last4} · Үлдэгдэл ${_fmt(card.balance)}₮',
                              isFirst: false,
                              isLast: i == _walletCards.length - 1,
                              onTap: () => setState(() => _selectedId = card.id),
                            );
                          }),
                        ],
                      ),
                    ),
                  ),
                ),
                // Add card shortcut
                Padding(
                  padding: const EdgeInsets.only(left: 16, right: 16, top: 12),
                  child: GestureDetector(
                    onTap: () async {
                      final result = await context.push(AppRoutes.addCard);
                      if (result == true) _loadCards();
                    },
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.add_circle_outline_rounded,
                            color: AppColors.primary, size: 18),
                        const SizedBox(width: 6),
                        Text(
                          'Шинэ карт нэмэх',
                          style: TextStyle(
                            fontSize: 14,
                            color: AppColors.primary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.only(left: 16, right: 16, bottom: 32, top: 16),
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(100),
              boxShadow: _selectedId != null
                  ? [
                      BoxShadow(
                        color: const Color(0xFF6C47FF).withValues(alpha: 0.3),
                        blurRadius: 20,
                        offset: const Offset(0, 8),
                      ),
                    ]
                  : [],
            ),
            child: AppButton(
              label: _selectedId == null
                  ? 'Төлбөрийн арга сонгоно уу'
                  : '${_fmt(widget.amount)}₮ төлөх',
              onPressed: _selectedId != null ? _pay : null,
            ),
          ),
        ),
      ),
    );
  }
}

_BillPaymentDisplay _displayFor(String serviceId) {
  if (serviceId == 'all') {
    return const _BillPaymentDisplay(
      name: 'Бүх төлбөр',
      provider: 'Хүлээгдэж буй бүх төлбөр',
      icon: Icons.receipt_long_rounded,
      iconColor: Color(0xFF6C47FF),
      bgIconColor: Color(0xFFEDE8FF),
    );
  }

  final bill = BillData.services.firstWhere((s) => s.id == serviceId);
  return _BillPaymentDisplay(
    name: bill.name,
    provider: bill.provider,
    icon: bill.icon,
    iconColor: bill.iconColor,
    bgIconColor: bill.bgIconColor,
  );
}

class _BillPaymentDisplay {
  final String name;
  final String provider;
  final IconData icon;
  final Color iconColor;
  final Color bgIconColor;

  const _BillPaymentDisplay({
    required this.name,
    required this.provider,
    required this.icon,
    required this.iconColor,
    required this.bgIconColor,
  });
}

// ─── Reusable payment row widget ───────────────────────────────────────────
class _PaymentRow extends StatelessWidget {
  final String id;
  final String? selectedId;
  final IconData icon;
  final Color iconBg;
  final Color iconColor;
  final String title;
  final String subtitle;
  final bool isFirst;
  final bool isLast;
  final VoidCallback onTap;

  const _PaymentRow({
    required this.id,
    required this.selectedId,
    required this.icon,
    required this.iconBg,
    required this.iconColor,
    required this.title,
    required this.subtitle,
    required this.isFirst,
    required this.isLast,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isSelected = selectedId == id;
    return Column(
      children: [
        if (!isFirst)
          const Divider(height: 0.5, color: AppColors.border, indent: 0, endIndent: 0),
        GestureDetector(
          onTap: onTap,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            height: 72,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            decoration: BoxDecoration(
              color: isSelected ? const Color(0xFFFAFAFF) : Colors.white,
              borderRadius: BorderRadius.vertical(
                top: isFirst ? const Radius.circular(16) : Radius.zero,
                bottom: isLast ? const Radius.circular(16) : Radius.zero,
              ),
              border: isSelected
                  ? Border.all(color: const Color(0xFF6C47FF), width: 1.5)
                  : null,
            ),
            child: Row(
              children: [
                Container(
                  width: 44, height: 44,
                  decoration: BoxDecoration(
                    color: iconBg,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(icon, color: iconColor, size: 22),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(title,
                          style: const TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.w600,
                              color: AppColors.textPrimary)),
                      const SizedBox(height: 2),
                      Text(subtitle,
                          style: const TextStyle(
                              fontSize: 12, color: AppColors.textSecondary)),
                    ],
                  ),
                ),
                // Radio button
                AnimatedContainer(
                  duration: const Duration(milliseconds: 200),
                  width: 22, height: 22,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: isSelected ? const Color(0xFF6C47FF) : Colors.white,
                    border: Border.all(
                      color: isSelected ? const Color(0xFF6C47FF) : AppColors.border,
                      width: 1.5,
                    ),
                  ),
                  child: isSelected
                      ? const Center(
                          child: CircleAvatar(
                            radius: 4,
                            backgroundColor: Colors.white,
                          ),
                        )
                      : null,
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

// ─── Confirm bottom sheet ───────────────────────────────────────────────────
class _ConfirmSheet extends StatelessWidget {
  final _BillPaymentDisplay bill;
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
    final fromLabel = cardLast4 == '—' ? bankName : '$bankName ****$cardLast4-аас';
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 12, 24, 24),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // drag handle
          Container(
            width: 40, height: 4,
            decoration: BoxDecoration(
              color: AppColors.border,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(height: 20),
          // service icon
          Container(
            width: 64, height: 64,
            decoration: BoxDecoration(
              color: bill.bgIconColor,
              shape: BoxShape.circle,
            ),
            child: Icon(bill.icon, color: bill.iconColor, size: 30),
          ),
          const SizedBox(height: 12),
          const Text(
            'Төлбөр баталгаажуулах',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.textPrimary),
          ),
          const SizedBox(height: 6),
          Text(
            '${bill.provider} руу $fromLabel',
            style: const TextStyle(fontSize: 14, color: AppColors.textSecondary),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 20),
          // summary box
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: const Color(0xFFF8F8FB),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              children: [
                _SummaryRow(label: 'Дүн', value: '${_fmt(amount)}₮', valueColor: AppColors.expense),
                const Divider(height: 1, thickness: 0.5, color: AppColors.border),
                const SizedBox(height: 8),
                _SummaryRow(label: 'Шимтгэл', value: 'Үнэгүй', valueColor: AppColors.income),
                const Divider(height: 1, thickness: 0.5, color: AppColors.border),
                const SizedBox(height: 8),
                _SummaryRow(label: 'Нийт', value: '${_fmt(amount)}₮', isBold: true),
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
                    foregroundColor: AppColors.textPrimary,
                  ),
                  child: const Text('Цуцлах',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Container(
                  height: 52,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(100),
                    boxShadow: [
                      BoxShadow(
                        color: const Color(0xFF6C47FF).withValues(alpha: 0.3),
                        blurRadius: 20,
                        offset: const Offset(0, 8),
                      ),
                    ],
                  ),
                  child: ElevatedButton(
                    onPressed: onConfirm,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF6C47FF),
                      minimumSize: const Size.fromHeight(52),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(100)),
                      elevation: 0,
                    ),
                    child: const Text('Төлөх',
                        style: TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.w600)),
                  ),
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

class _SummaryRow extends StatelessWidget {
  final String label;
  final String value;
  final Color? valueColor;
  final bool isBold;

  const _SummaryRow({
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
            style: TextStyle(
                fontSize: 14,
                color: isBold ? AppColors.textPrimary : AppColors.textSecondary,
                fontWeight: isBold ? FontWeight.w700 : FontWeight.w400)),
        Text(value,
            style: TextStyle(
                fontSize: 14,
                color: valueColor ?? AppColors.textPrimary,
                fontWeight: isBold ? FontWeight.w700 : FontWeight.w500)),
      ],
    );
  }
}
