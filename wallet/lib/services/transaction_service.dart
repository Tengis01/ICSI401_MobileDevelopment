import 'package:cloud_firestore/cloud_firestore.dart';
import '../core/constants/app_constants.dart';
import '../models/transaction_model.dart';

class TransactionService {
  final FirebaseFirestore _db = FirebaseFirestore.instance;

  CollectionReference<Map<String, dynamic>> _txCollection(String userId) {
    return _db
        .collection(AppConstants.usersCollection)
        .doc(userId)
        .collection(AppConstants.transactionsCollection);
  }

  Future<void> addTransaction(TransactionModel tx) async {
    await _txCollection(tx.userId).doc(tx.id).set(tx.toFirestore());
  }

  Future<void> updateTransaction(TransactionModel tx) async {
    await _txCollection(tx.userId).doc(tx.id).update(tx.toFirestore());
  }

  Future<void> deleteTransaction(String userId, String txId) async {
    await _txCollection(userId).doc(txId).delete();
  }

  // suuliin 20 guriljaa real-time stream-eer avah
  Stream<List<TransactionModel>> getRecentTransactions(
      String userId, {
        int limit = 20,
      }) {
    return _txCollection(userId)
        .orderBy('date', descending: true)
        .limit(limit)
        .snapshots()
        .map((snap) =>
        snap.docs.map((doc) => TransactionModel.fromFirestore(doc)).toList());
  }

  Stream<List<TransactionModel>> getAllTransactions(String userId) {
    return _txCollection(userId)
        .orderBy('date', descending: true)
        .snapshots()
        .map((snap) =>
        snap.docs.map((doc) => TransactionModel.fromFirestore(doc)).toList());
  }

  Future<List<TransactionModel>> searchTransactions(
      String userId, String query) async {
    final q = query.toLowerCase();
    final snap = await _txCollection(userId)
        .orderBy('date', descending: true)
        .get();
    final all =
        snap.docs.map((doc) => TransactionModel.fromFirestore(doc)).toList();
    return all.where((tx) {
      final matchCategory = tx.categoryName.toLowerCase().contains(q);
      final matchNote = tx.note?.toLowerCase().contains(q) ?? false;
      return matchCategory || matchNote;
    }).toList();
  }

  Future<List<TransactionModel>> getTransactionsByMonth(
      String userId,
      int year,
      int month,
      ) async {
    final startDate = DateTime(year, month, 1);
    final endDate = DateTime(year, month + 1, 1);
    final snap = await _txCollection(userId)
        .where('date', isGreaterThanOrEqualTo: Timestamp.fromDate(startDate))
        .where('date', isLessThan: Timestamp.fromDate(endDate))
        .orderBy('date', descending: true)
        .get();
    return snap.docs.map((doc) => TransactionModel.fromFirestore(doc)).toList();
  }
}
