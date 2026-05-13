import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../models/wallet_model.dart';
import '../../services/wallet_service.dart';
import '../../widgets/app_button.dart';

class AddCardScreen extends StatefulWidget {
  const AddCardScreen({super.key});

  @override
  State<AddCardScreen> createState() => _AddCardScreenState();
}

class _AddCardScreenState extends State<AddCardScreen> {
  final _formKey = GlobalKey<FormState>();
  final _numberController  = TextEditingController();
  final _expiryController  = TextEditingController();
  final _cvvController     = TextEditingController();
  final _holderController  = TextEditingController();
  final _bankController    = TextEditingController();
  final _balanceController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _numberController.dispose();
    _expiryController.dispose();
    _cvvController.dispose();
    _holderController.dispose();
    _bankController.dispose();
    _balanceController.dispose();
    super.dispose();
  }

  // kart dugaar preview
  String get _previewNumber {
    final raw = _numberController.text.replaceAll(' ', '');
    if (raw.isEmpty) return '•••• •••• •••• ••••';
    final padded = raw.padRight(16, '•');
    return '${padded.substring(0, 4)} ${padded.substring(4, 8)} '
        '${padded.substring(8, 12)} ${padded.substring(12, 16)}';
  }

  String get _previewExpiry =>
      _expiryController.text.isEmpty ? 'MM/YY' : _expiryController.text;

  String get _previewHolder =>
      _holderController.text.isEmpty
          ? 'HOLDER NAME'
          : _holderController.text.toUpperCase();

  String get _previewBank =>
      _bankController.text.isEmpty ? 'БАНК' : _bankController.text;

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);

    try {
      final card = WalletCard(
        id: WalletService.instance.generateId(),
        bankName: _bankController.text.trim(),
        cardNumber: _numberController.text.replaceAll(' ', ''),
        expiryDate: _expiryController.text.trim(),
        holderName: _holderController.text.trim(),
        balance: int.tryParse(
            _balanceController.text.replaceAll(',', '')) ??
            0,
      );

      await WalletService.instance.addCard(card);

      if (!mounted) return;
      context.pop(true); // true = amjilttai nemegdsen
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
        title: const Text('Карт нэмэх'),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // kart preview
              _CardPreview(
                number: _previewNumber,
                expiry: _previewExpiry,
                holder: _previewHolder,
                bank: _previewBank,
              ),
              const SizedBox(height: 28),
              // bank ner
              _buildLabel('БАНКНЫ НЭР'),
              const SizedBox(height: 6),
              TextFormField(
                controller: _bankController,
                style: AppTextStyles.bodyLarge,
                onChanged: (_) => setState(() {}),
                decoration: const InputDecoration(
                  hintText: 'Khan Bank, TDB...',
                  prefixIcon: Icon(Icons.account_balance_outlined,
                      color: AppColors.textSecondary, size: 20),
                ),
                validator: (v) => (v == null || v.trim().isEmpty)
                    ? 'Банкны нэр оруулна уу'
                    : null,
              ),
              const SizedBox(height: 20),
              // kart dugaar
              _buildLabel('КАРТЫН ДУГААР'),
              const SizedBox(height: 6),
              TextFormField(
                controller: _numberController,
                keyboardType: TextInputType.number,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  _CardNumberFormatter(),
                ],
                style: AppTextStyles.bodyLarge,
                onChanged: (_) => setState(() {}),
                decoration: const InputDecoration(
                  hintText: '0000 0000 0000 0000',
                  prefixIcon: Icon(Icons.credit_card_rounded,
                      color: AppColors.textSecondary, size: 20),
                ),
                validator: (v) {
                  final digits = v?.replaceAll(' ', '') ?? '';
                  if (digits.length < 16) return 'Картын дугаар 16 оронтой байх ёстой';
                  return null;
                },
              ),
              const SizedBox(height: 20),
              // duusah ognoo + cvv
              Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildLabel('ДУУСАХ ОГНОО'),
                        const SizedBox(height: 6),
                        TextFormField(
                          controller: _expiryController,
                          keyboardType: TextInputType.number,
                          inputFormatters: [
                            FilteringTextInputFormatter.digitsOnly,
                            _ExpiryFormatter(),
                          ],
                          style: AppTextStyles.bodyLarge,
                          onChanged: (_) => setState(() {}),
                          decoration: const InputDecoration(
                            hintText: 'MM/YY',
                          ),
                          validator: (v) {
                            if (v == null || v.length < 5) {
                              return 'Буруу огноо';
                            }
                            return null;
                          },
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildLabel('CVV'),
                        const SizedBox(height: 6),
                        TextFormField(
                          controller: _cvvController,
                          keyboardType: TextInputType.number,
                          obscureText: true,
                          inputFormatters: [
                            FilteringTextInputFormatter.digitsOnly,
                            LengthLimitingTextInputFormatter(4),
                          ],
                          style: AppTextStyles.bodyLarge,
                          decoration: const InputDecoration(
                            hintText: '•••',
                          ),
                          validator: (v) {
                            if (v == null || v.length < 3) {
                              return 'CVV буруу';
                            }
                            return null;
                          },
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              // ezem ner
              _buildLabel('КАРТЫН ЭЗЭМШИГЧ'),
              const SizedBox(height: 6),
              TextFormField(
                controller: _holderController,
                textCapitalization: TextCapitalization.words,
                style: AppTextStyles.bodyLarge,
                onChanged: (_) => setState(() {}),
                decoration: const InputDecoration(
                  hintText: 'BAT-ERDENE',
                  prefixIcon: Icon(Icons.person_outline_rounded,
                      color: AppColors.textSecondary, size: 20),
                ),
                validator: (v) => (v == null || v.trim().isEmpty)
                    ? 'Эзэмшигчийн нэр оруулна уу'
                    : null,
              ),
              const SizedBox(height: 20),
              // simulation balance
              _buildLabel('ҮЛДЭГДЭЛ (ТУРШИЛТ)'),
              const SizedBox(height: 6),
              TextFormField(
                controller: _balanceController,
                keyboardType: TextInputType.number,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                ],
                style: AppTextStyles.bodyLarge,
                decoration: const InputDecoration(
                  hintText: '0',
                  suffixText: '₮',
                  prefixIcon: Icon(Icons.account_balance_wallet_outlined,
                      color: AppColors.textSecondary, size: 20),
                ),
              ),
              const SizedBox(height: 32),
              AppButton(
                label: 'Карт нэмэх',
                onPressed: _save,
                isLoading: _isLoading,
              ),
              const SizedBox(height: 20),
            ],
          ),
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

// kart preview widget
class _CardPreview extends StatelessWidget {
  final String number;
  final String expiry;
  final String holder;
  final String bank;

  const _CardPreview({
    required this.number,
    required this.expiry,
    required this.holder,
    required this.bank,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      height: 190,
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.splashStart, AppColors.splashEnd],
        ),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: AppColors.primary.withValues(alpha:0.3),
            blurRadius: 20,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                bank,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
              const Icon(Icons.credit_card_rounded,
                  color: Colors.white, size: 28),
            ],
          ),
          const Spacer(),
          Text(
            number,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w500,
              color: Colors.white,
              letterSpacing: 2,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'CARD HOLDER',
                    style: TextStyle(
                      fontSize: 9,
                      color: Colors.white.withValues(alpha:0.65),
                    ),
                  ),
                  Text(
                    holder,
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    'EXPIRES',
                    style: TextStyle(
                      fontSize: 9,
                      color: Colors.white.withValues(alpha:0.65),
                    ),
                  ),
                  Text(
                    expiry,
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}

// 16 digit -> "0000 0000 0000 0000" formatter
class _CardNumberFormatter extends TextInputFormatter {
  @override
  TextEditingValue formatEditUpdate(
      TextEditingValue old, TextEditingValue newVal) {
    final digits = newVal.text.replaceAll(' ', '');
    if (digits.length > 16) return old;

    final buffer = StringBuffer();
    for (int i = 0; i < digits.length; i++) {
      if (i > 0 && i % 4 == 0) buffer.write(' ');
      buffer.write(digits[i]);
    }

    final str = buffer.toString();
    return newVal.copyWith(
      text: str,
      selection: TextSelection.collapsed(offset: str.length),
    );
  }
}

// MM/YY formatter
class _ExpiryFormatter extends TextInputFormatter {
  @override
  TextEditingValue formatEditUpdate(
      TextEditingValue old, TextEditingValue newVal) {
    final digits = newVal.text.replaceAll('/', '');
    if (digits.length > 4) return old;

    String formatted = digits;
    if (digits.length >= 3) {
      formatted = '${digits.substring(0, 2)}/${digits.substring(2)}';
    }

    return newVal.copyWith(
      text: formatted,
      selection:
      TextSelection.collapsed(offset: formatted.length),
    );
  }
}