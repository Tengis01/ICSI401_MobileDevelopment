import 'package:cloud_firestore/cloud_firestore.dart';

enum TransactionType { expense, income }

class TransactionModel {
  final String id;
  final String userId;
  final TransactionType type;
  final int amount;
  final String categoryId;
  final String categoryName;
  final String categoryIcon;
  final String? note;
  final DateTime date;
  final DateTime createdAt;

  const TransactionModel({
    required this.id,
    required this.userId,
    required this.type,
    required this.amount,
    required this.categoryId,
    required this.categoryName,
    required this.categoryIcon,
    this.note,
    required this.date,
    required this.createdAt,
  });

  factory TransactionModel.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return TransactionModel(
      id: doc.id,
      userId: data['userId'] as String,
      type: data['type'] == 'income'
          ? TransactionType.income
          : TransactionType.expense,
      amount: data['amount'] as int,
      categoryId: data['categoryId'] as String,
      categoryName: data['categoryName'] as String,
      categoryIcon: data['categoryIcon'] as String,
      note: data['note'] as String?,
      date: (data['date'] as Timestamp).toDate(),
      createdAt: (data['createdAt'] as Timestamp).toDate(),
    );
  }

  Map<String, dynamic> toFirestore() {
    return {
      'userId': userId,
      'type': type == TransactionType.income ? 'income' : 'expense',
      'amount': amount,
      'categoryId': categoryId,
      'categoryName': categoryName,
      'categoryIcon': categoryIcon,
      'note': note,
      'date': Timestamp.fromDate(date),
      'createdAt': Timestamp.fromDate(createdAt),
    };
  }

  TransactionModel copyWith({
    TransactionType? type,
    int? amount,
    String? categoryId,
    String? categoryName,
    String? categoryIcon,
    String? note,
    DateTime? date,
  }) {
    return TransactionModel(
      id: id,
      userId: userId,
      type: type ?? this.type,
      amount: amount ?? this.amount,
      categoryId: categoryId ?? this.categoryId,
      categoryName: categoryName ?? this.categoryName,
      categoryIcon: categoryIcon ?? this.categoryIcon,
      note: note ?? this.note,
      date: date ?? this.date,
      createdAt: createdAt,
    );
  }
}