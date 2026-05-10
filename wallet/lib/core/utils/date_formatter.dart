class DateFormatter {
  DateFormatter._();

  static const List<String> _months = [
    '', '1-р', '2-р', '3-р', '4-р', '5-р', '6-р', '7-р', '8-р', '9-р', '10-р', '11-р', '12-р'
  ];

  static String formatRelative(DateTime date){
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final yesterday = today.subtract(const Duration(days: 1));
    final dateOnly = DateTime(date.year, date.month, date.day);
    final time = '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';

    if (dateOnly == today) return 'Өнөөдөр $time';
    if (dateOnly == yesterday) return 'Өчигдөр $time';
    final diff = today.difference(dateOnly).inDays;
    if (diff < 7) return '$diff хоногийн өмнө';
    return '${date.year} оны ${_months[date.month]} сарын ${date.day}';
  }

  static String formatFull(DateTime date) {
    final time = '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
    return '${date.year} оны ${_months[date.month]} сарын ${date.day}, $time';
  }

  static String formatMonth(int year, int month) {
    return '${_months[month]} сарын $year';
  }
}