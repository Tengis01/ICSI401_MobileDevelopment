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
      bgIconColorValue: 0xFFFFF8E1,
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
      bgIconColorValue: 0xFFE3F2FD,
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
      bgIconColorValue: 0xFFE0F7FA,
      dueDate: '2026/05/01',
      accountNumber: 'INT-887766',
      providerOptions: [
        ProviderOption(id: 'univision', name: 'Univision', colorValue: 0xFF8E24AA),
        ProviderOption(id: 'skytel', name: 'Skytel', colorValue: 0xFF1E88E5),
      ],
    ),
    BillModel(
      id: 'phone',
      name: 'Утас цэнэглэх',
      provider: 'Mobicom · Unitel · Skytel · G-Mobile',
      amount: null,
      icon: Icons.smartphone_rounded,
      iconColorValue: 0xFF66BB6A,
      bgIconColorValue: 0xFFE8F5E9,
      providerOptions: [
        ProviderOption(id: 'mobicom', name: 'Mobicom', colorValue: 0xFF43A047),
        ProviderOption(id: 'unitel', name: 'Unitel', colorValue: 0xFFE53935),
        ProviderOption(id: 'skytel', name: 'Skytel', colorValue: 0xFF1E88E5),
        ProviderOption(id: 'gmobile', name: 'G-Mobile', colorValue: 0xFFFB8C00),
      ],
    ),
    BillModel(
      id: 'tv',
      name: 'Кабелийн ТВ',
      provider: 'DDish · SkyMedia',
      amount: 25000,
      icon: Icons.tv_rounded,
      iconColorValue: 0xFFAB47BC,
      bgIconColorValue: 0xFFF3E5F5,
      dueDate: '2026/04/30',
      accountNumber: 'TV-445566',
      providerOptions: [
        ProviderOption(id: 'ddish', name: 'DDish', colorValue: 0xFFF4511E),
        ProviderOption(id: 'skymedia', name: 'SkyMedia', colorValue: 0xFF1E88E5),
      ],
    ),
  ];

  // niit dun
  static int get totalDue => services
      .where((s) => s.amount != null)
      .fold(0, (sum, s) => sum + s.amount!);

  static List<BillModel> get pendingBills =>
      services.where((s) => s.amount != null).toList();

  // hugjagdah toots too
  static int get pendingCount =>
      services.where((s) => s.amount != null).length;
}
