import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:shared_preferences/shared_preferences.dart';

class NotificationService {
  NotificationService._();
  static final NotificationService instance = NotificationService._();

  final _plugin = FlutterLocalNotificationsPlugin();
  bool _initialized = false;

  static const _txKey = 'notif_transaction';
  static const _monthlyKey = 'notif_monthly';
  static const _billKey = 'notif_bill';

  Future<void> initialize() async {
    if (_initialized) return;

    const androidSettings =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const darwinSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    const settings = InitializationSettings(
      android: androidSettings,
      iOS: darwinSettings,
    );

    await _plugin.initialize(settings);
    _initialized = true;
  }

  // notif idewhtei eseh shalgah
  Future<bool> _isEnabled(String key) async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(key) ?? true;
  }

  // guriljaa nemegdeh ued duudagdana
  Future<void> showTransactionNotification({
    required String categoryName,
    required int amount,
    required bool isIncome,
  }) async {
    if (!await _isEnabled(_txKey)) return;

    final sign = isIncome ? '+' : '-';
    final formattedAmount = _formatAmount(amount);

    await _show(
      id: 1,
      title: isIncome ? '↓ Орлого бүртгэгдлээ' : '↑ Зарлага бүртгэгдлээ',
      body: '$categoryName · $sign$formattedAmount₮',
    );
  }

  // simulation tolbor duusah ued
  Future<void> showBillPaymentNotification({
    required String serviceName,
    required int amount,
    required String cardLast4,
  }) async {
    if (!await _isEnabled(_billKey)) return;

    await _show(
      id: 2,
      title: '✓ Төлбөр амжилттай',
      body: '$serviceName · ${_formatAmount(amount)}₮ · ****$cardLast4-аас',
    );
  }

  // simulation shiljuuleg duusah ued
  Future<void> showTransferNotification({
    required String recipientName,
    required int amount,
    required bool isSend,
  }) async {
    if (!await _isEnabled(_txKey)) return;

    await _show(
      id: 3,
      title: isSend ? '↗ Шилжүүлэг илгээгдлээ' : '↙ Мөнгө хүлээн авлаа',
      body: '${_formatAmount(amount)}₮ · $recipientName',
    );
  }

  // sariin taiilan - sar ehelhed duudna
  Future<void> showMonthlySummaryNotification({
    required int income,
    required int expense,
  }) async {
    if (!await _isEnabled(_monthlyKey)) return;

    final savings = income - expense;
    await _show(
      id: 4,
      title: '📊 Сарын тайлан',
      body:
          'Орлого ${_formatAmount(income)}₮ · Зарлага ${_formatAmount(expense)}₮ · Хэмнэлт ${_formatAmount(savings)}₮',
    );
  }

  Future<void> _show({
    required int id,
    required String title,
    required String body,
  }) async {
    const androidDetails = AndroidNotificationDetails(
      'financial_note_channel',
      'Financial Note мэдэгдэл',
      channelDescription: 'Гүйлгээ болон санхүүгийн мэдэгдэл',
      importance: Importance.high,
      priority: Priority.high,
      showWhen: true,
    );

    const darwinDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const details = NotificationDetails(
      android: androidDetails,
      iOS: darwinDetails,
    );

    await _plugin.show(id, title, body, details);
  }

  // 45000 -> "45,000"
  String _formatAmount(int amount) {
    return amount.toString().replaceAllMapped(
          RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
          (m) => '${m[1]},',
        );
  }
}
