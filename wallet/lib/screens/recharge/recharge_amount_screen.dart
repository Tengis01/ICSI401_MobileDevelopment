import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../core/utils/currency_formatter.dart';

class RechargeAmountScreen extends StatefulWidget {
  const RechargeAmountScreen({super.key});

  @override
  State<RechargeAmountScreen> createState() => _RechargeAmountScreenState();
}

class _RechargeAmountScreenState extends State<RechargeAmountScreen> {
  int _amount = 0;

  void _append(String value) {
    final next = int.tryParse('$_amount$value') ?? _amount;
    setState(() => _amount = next.clamp(0, 999999999));
  }

  void _backspace() {
    final text = _amount.toString();
    setState(() {
      _amount =
          text.length <= 1 ? 0 : int.parse(text.substring(0, text.length - 1));
    });
  }

  void _continue() {
    if (_amount <= 0) return;
    context.push(AppRoutes.rechargeBank, extra: _amount);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.splashStart, AppColors.splashEnd],
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
                child: Row(
                  children: [
                    const SizedBox(width: 48),
                    const Expanded(
                      child: Text(
                        'Данс цэнэглэх',
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    IconButton(
                      onPressed: () => context.pop(),
                      icon: const Icon(
                        Icons.close_rounded,
                        color: Colors.white,
                        size: 28,
                      ),
                    ),
                  ],
                ),
              ),
              const Spacer(flex: 2),
              Text(
                '${CurrencyFormatter.formatNoSymbol(_amount)}₮',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 48,
                  fontWeight: FontWeight.w700,
                  letterSpacing: 0,
                ),
              ),
              const Spacer(flex: 2),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 36),
                child: _Numpad(
                  onDigit: _append,
                  onBackspace: _backspace,
                ),
              ),
              const SizedBox(height: 28),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 64),
                child: SizedBox(
                  width: double.infinity,
                  height: 58,
                  child: ElevatedButton(
                    onPressed: _amount > 0 ? _continue : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white.withValues(alpha: 0.24),
                      disabledBackgroundColor:
                          Colors.white.withValues(alpha: 0.14),
                      foregroundColor: Colors.white,
                      disabledForegroundColor:
                          Colors.white.withValues(alpha: 0.55),
                      elevation: 0,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(100),
                      ),
                    ),
                    child: const Text(
                      'Үргэлжлүүлэх',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 26),
            ],
          ),
        ),
      ),
    );
  }
}

class _Numpad extends StatelessWidget {
  final ValueChanged<String> onDigit;
  final VoidCallback onBackspace;

  const _Numpad({
    required this.onDigit,
    required this.onBackspace,
  });

  @override
  Widget build(BuildContext context) {
    const rows = [
      ['1', '2', '3'],
      ['4', '5', '6'],
      ['7', '8', '9'],
      ['000', '0', 'backspace'],
    ];

    return Column(
      children: rows.map((row) {
        return Padding(
          padding: const EdgeInsets.only(bottom: 18),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: row.map((value) {
              if (value == 'backspace') {
                return _NumpadButton(
                  onTap: onBackspace,
                  child: const Icon(
                    Icons.backspace_rounded,
                    color: Colors.white,
                    size: 26,
                  ),
                );
              }

              return _NumpadButton(
                onTap: () => onDigit(value),
                child: Text(
                  value,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 28,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              );
            }).toList(),
          ),
        );
      }).toList(),
    );
  }
}

class _NumpadButton extends StatelessWidget {
  final Widget child;
  final VoidCallback onTap;

  const _NumpadButton({
    required this.child,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkResponse(
      onTap: onTap,
      radius: 38,
      child: SizedBox(
        width: 72,
        height: 48,
        child: Center(child: child),
      ),
    );
  }
}
