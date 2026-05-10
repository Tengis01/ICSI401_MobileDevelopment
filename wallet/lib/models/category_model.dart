import 'transaction_model.dart';

class CategoryModel {
  final String id;
  final String name;
  final String icon;
  final int colorValue;
  final TransactionType type;

  const CategoryModel({
    required this.id,
    required this.name,
    required this.icon,
    required this.colorValue,
    required this.type,
});
}