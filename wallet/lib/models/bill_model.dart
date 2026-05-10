import 'package:flutter/material.dart';

class BillModel {
  final String id;
  final String name;
  final String provider;
  final int? amount;           // null = custom (utас цэнэглэх)
  final IconData icon;
  final int iconColorValue;
  final String? dueDate;
  final String? accountNumber; // geriin dugaar gэх мэт

  const BillModel({
    required this.id,
    required this.name,
    required this.provider,
    this.amount,
    required this.icon,
    required this.iconColorValue,
    this.dueDate,
    this.accountNumber,
  });

  Color get iconColor => Color(iconColorValue);

  bool get isCustomAmount => amount == null;
}