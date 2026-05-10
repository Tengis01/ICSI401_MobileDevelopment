import 'package:flutter/material.dart';
import '../../models/category_model.dart';
import '../../models/transaction_model.dart';

class CategoryData {
  CategoryData._();

  static const List<CategoryModel> expenseCategories = [
    CategoryModel(
      id: 'food', name: 'Хоол', icon: 'food',
      colorValue: 0xFFFF8A65, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'shopping', name: 'Дэлгүүр', icon: 'shopping',
      colorValue: 0xFFEC407A, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'transport', name: 'Тээвэр', icon: 'transport',
      colorValue: 0xFF42A5F5, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'home', name: 'Гэр', icon: 'home',
      colorValue: 0xFF66BB6A, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'entertainment', name: 'Зугаа', icon: 'entertainment',
      colorValue: 0xFFAB47BC, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'health', name: 'Эрүүл мэнд', icon: 'health',
      colorValue: 0xFF26C6DA, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'education', name: 'Боловсрол', icon: 'education',
      colorValue: 0xFFFFCA28, type: TransactionType.expense,
    ),
    CategoryModel(
      id: 'other_expense', name: 'Бусад', icon: 'other',
      colorValue: 0xFF90A4AE, type: TransactionType.expense,
    ),
  ];

  static const List<CategoryModel> incomeCategories = [
    CategoryModel(
      id: 'salary', name: 'Цалин', icon: 'salary',
      colorValue: 0xFF00C896, type: TransactionType.income,
    ),
    CategoryModel(
      id: 'business', name: 'Бизнес', icon: 'business',
      colorValue: 0xFF6C47FF, type: TransactionType.income,
    ),
    CategoryModel(
      id: 'gift', name: 'Бэлэг', icon: 'gift',
      colorValue: 0xFFEC407A, type: TransactionType.income,
    ),
    CategoryModel(
      id: 'other_income', name: 'Бусад', icon: 'other',
      colorValue: 0xFF90A4AE, type: TransactionType.income,
    ),
  ];

  static IconData getIconData(String icon) {
    switch (icon) {
      case 'food': return Icons.restaurant_rounded;
      case 'shopping': return Icons.shopping_bag_rounded;
      case 'transport': return Icons.directions_bus_rounded;
      case 'home': return Icons.home_rounded;
      case 'entertainment': return Icons.movie_rounded;
      case 'health': return Icons.favorite_rounded;
      case 'education': return Icons.school_rounded;
      case 'salary': return Icons.work_rounded;
      case 'business': return Icons.business_center_rounded;
      case 'gift': return Icons.card_giftcard_rounded;
      default: return Icons.more_horiz_rounded;
    }
  }

  static Color getColor(String icon, TransactionType type) {
    final list = type == TransactionType.expense
        ? expenseCategories
        : incomeCategories;
    final cat = list.where((c) => c.icon == icon).firstOrNull;
    return Color(cat?.colorValue ?? 0xFF90A4AE);
  }
}