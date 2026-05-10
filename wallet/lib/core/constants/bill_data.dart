import 'package:flutter/material.dart';
import '../../models/bill_model.dart';

class BillData {
  BillData._();

  static const List<BillModel> services = [
    BillModel(
      id: 'tsahilgaan',
      name: 'Цахилгаан',
      provider: 'УБЦТС',
      amount: 58000,
      icon: Icons.bolt_rounded,
      iconColorValue: 0xFFFFCA28,
      dueDate: '2026/04/30',
      accountNumber: 'MN-2024-583920',
    ),
    BillModel(
      id: 'us',
      name: 'Ус',
      provider: 'УСУГ',
      amount: 12400,
      icon: Icons.water_drop_rounded,
      iconColorValue: 0xFF42A5F5,
      dueDate: '2026/04/28',
      accountNumber: 'US-2024-112233',
    ),
    BillModel(
      id: 'internet',
      name: 'Интернэт',
      provider: 'Univision · Skytel',
      amount: 35000,
      icon: Icons.wifi_rounded,
      iconColorValue: 0xFF26C6DA,
      dueDate: '2026/05/01',
      accountNumber: 'INT-887766',
    ),
    BillModel(
      id: 'phone',
      name: 'Утас цэнэглэх',
      provider: 'Mobicom · Unitel',
      amount: null,
      icon: Icons.phone_android_rounded,
      iconColorValue: 0xFF66BB6A,
      dueDate: null,
      accountNumber: null,
    ),
    BillModel(
      id: 'tv',
      name: 'Кабелийн ТВ',
      provider: 'DDish · SkyMedia',
      amount: 25000,
      icon: Icons.tv_rounded,
      iconColorValue: 0xFFAB47BC,
      dueDate: '2026/04/30',
      accountNumber: 'TV-445566',
    ),
  ];

  // niit dun
  static int get totalDue => services
      .where((s) => s.amount != null)
      .fold(0, (sum, s) => sum + s.amount!);

  // hugjagdah toots too
  static int get pendingCount =>
      services.where((s) => s.amount != null).length;
}