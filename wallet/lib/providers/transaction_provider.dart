import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/transaction_model.dart';
import '../services/transaction_service.dart';
import 'auth_provider.dart';

final transactionServiceProvider = Provider<TransactionService>((ref) {
  return TransactionService();
});

// home screen-d real-time stream
final recentTransactionsProvider = StreamProvider<List<TransactionModel>>((ref) {
  final user = ref.watch(currentUserProvider);
  if (user == null) return const Stream.empty();
  return ref.watch(transactionServiceProvider).getRecentTransactions(user.uid);
});

// sariin guriljaa - statistik screen-d
final monthlyTransactionsProvider =
FutureProvider.family<List<TransactionModel>, ({int year, int month})>(
      (ref, params) async {
    final user = ref.watch(currentUserProvider);
    if (user == null) return [];
    return ref
        .watch(transactionServiceProvider)
        .getTransactionsByMonth(user.uid, params.year, params.month);
  },
);

// sariin niilber - balance card-d
final monthSummaryProvider =
FutureProvider.family<({int income, int expense}), ({int year, int month})>(
      (ref, params) async {
    final transactions =
    await ref.watch(monthlyTransactionsProvider(params).future);
    int income = 0;
    int expense = 0;
    for (final tx in transactions) {
      if (tx.type == TransactionType.income) {
        income += tx.amount;
      } else {
        expense += tx.amount;
      }
    }
    return (income: income, expense: expense);
  },
);