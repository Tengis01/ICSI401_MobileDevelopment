import 'package:flutter/material.dart';

class BillModel {
  final String id;
  final String name;
  final String provider;
  final int? amount;
  final IconData icon;
  final int iconColorValue;
  final int bgIconColorValue;
  final String? dueDate;
  final String? accountNumber;
  final List<ProviderOption>? providerOptions;

  const BillModel({
    required this.id,
    required this.name,
    required this.provider,
    this.amount,
    required this.icon,
    required this.iconColorValue,
    required this.bgIconColorValue,
    this.dueDate,
    this.accountNumber,
    this.providerOptions,
  });

  Color get iconColor => Color(iconColorValue);
  Color get bgIconColor => Color(bgIconColorValue);

  bool get isCustomAmount => amount == null;
}

class ProviderOption {
  final String id;
  final String name;
  final int colorValue;

  const ProviderOption({
    required this.id,
    required this.name,
    required this.colorValue,
  });

  Color get color => Color(colorValue);
}