import 'package:intl/intl.dart';

class CurrencyFormatter {
  CurrencyFormatter._();

  static final _formatter = NumberFormat('#,###', 'en_US');

  // 45000 -> "45,000 ₮"
  static String format(int amount) {
    return '${_formatter.format(amount)} ₮';
  }

  // income=true -> "+45,000 ₮", false -> "-45,000 ₮"
  static String formatWithSign(int amount, {bool isIncome = false}) {
    final sign = isIncome ? '+' : '-';
    return '$sign${_formatter.format(amount)} ₮';
  }

  static String formatNoSymbol(int amount) {
    return _formatter.format(amount);
  }
}