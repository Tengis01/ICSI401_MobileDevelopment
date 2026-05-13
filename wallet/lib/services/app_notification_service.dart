import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:uuid/uuid.dart';

enum AppNotificationType { expense, income, bill, note }

class AppNotification {
  final String id;
  final String title;
  final String body;
  final AppNotificationType type;
  final DateTime createdAt;
  final bool isRead;

  const AppNotification({
    required this.id,
    required this.title,
    required this.body,
    required this.type,
    required this.createdAt,
    required this.isRead,
  });

  AppNotification copyWith({bool? isRead}) {
    return AppNotification(
      id: id,
      title: title,
      body: body,
      type: type,
      createdAt: createdAt,
      isRead: isRead ?? this.isRead,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'title': title,
        'body': body,
        'type': type.name,
        'createdAt': createdAt.toIso8601String(),
        'isRead': isRead,
      };

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      id: json['id'] as String,
      title: json['title'] as String,
      body: json['body'] as String,
      type: AppNotificationType.values.firstWhere(
        (type) => type.name == json['type'],
        orElse: () => AppNotificationType.note,
      ),
      createdAt: DateTime.parse(json['createdAt'] as String),
      isRead: json['isRead'] as bool? ?? false,
    );
  }
}

class AppNotificationService {
  AppNotificationService._();
  static final AppNotificationService instance = AppNotificationService._();

  static const _key = 'app_notifications';
  static const _uuid = Uuid();

  Future<void> addNotification({
    required String title,
    required String body,
    required AppNotificationType type,
  }) async {
    final notifications = await getNotifications();
    notifications.insert(
      0,
      AppNotification(
        id: _uuid.v4(),
        title: title,
        body: body,
        type: type,
        createdAt: DateTime.now(),
        isRead: false,
      ),
    );
    await _save(notifications);
  }

  Future<List<AppNotification>> getNotifications() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStr = prefs.getString(_key);
    if (jsonStr == null) return [];

    final list = jsonDecode(jsonStr) as List;
    return list
        .map((item) => AppNotification.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<void> markAllRead() async {
    final notifications = await getNotifications();
    await _save(
      notifications.map((item) => item.copyWith(isRead: true)).toList(),
    );
  }

  Future<int> get unreadCount async {
    final notifications = await getNotifications();
    return notifications.where((item) => !item.isRead).length;
  }

  Future<void> _save(List<AppNotification> notifications) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(
      _key,
      jsonEncode(notifications.map((item) => item.toJson()).toList()),
    );
  }
}
