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

final allTransactionsProvider = StreamProvider<List<TransactionModel>>((ref) {
  final user = ref.watch(currentUserProvider);
  if (user == null) return const Stream.empty();
  return ref.watch(transactionServiceProvider).getAllTransactions(user.uid);
});

final totalBalanceStreamProvider =
StreamProvider<({int income, int expense, int balance})>((ref) {
  final user = ref.watch(currentUserProvider);
  if (user == null) return const Stream.empty();
  return ref
      .watch(transactionServiceProvider)
      .getAllTransactions(user.uid)
      .map(_summaryFromTransactions);
});

final totalBalanceProvider = StreamProvider<int>((ref) {
  final user = ref.watch(currentUserProvider);
  if (user == null) return const Stream.empty();
  return ref
      .watch(transactionServiceProvider)
      .getAllTransactions(user.uid)
      .map((transactions) => _summaryFromTransactions(transactions).balance);
});

({int income, int expense, int balance}) _summaryFromTransactions(
  List<TransactionModel> transactions,
) {
  int income = 0;
  int expense = 0;
  for (final tx in transactions) {
    if (tx.type == TransactionType.income) {
      income += tx.amount;
    } else {
      expense += tx.amount;
    }
  }
  return (income: income, expense: expense, balance: income - expense);
}

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

// pre-computed stats model - stats screen-d shuurhai ashiglana
class MonthlyStats {
  final int totalIncome;
  final int totalExpense;
  final int savings;
  final List<MapEntry<String, int>> sortedCategories;

  const MonthlyStats({
    required this.totalIncome,
    required this.totalExpense,
    required this.savings,
    required this.sortedCategories,
  });
}

// stats screen-d shaardlagataig buh too-g neg provider-ees avna
// keepAlive = true -> sar solihod cache-ees avna
final monthlyStatsProvider =
    FutureProvider.family<MonthlyStats, ({int year, int month})>(
  (ref, params) async {
    final transactions =
        await ref.watch(monthlyTransactionsProvider(params).future);

    int totalIncome = 0;
    int totalExpense = 0;
    final Map<String, int> categoryTotals = {};

    for (final tx in transactions) {
      if (tx.type == TransactionType.income) {
        totalIncome += tx.amount;
      } else {
        totalExpense += tx.amount;
        categoryTotals[tx.categoryName] =
            (categoryTotals[tx.categoryName] ?? 0) + tx.amount;
      }
    }

    final sortedCategories = categoryTotals.entries.toList()
      ..sort((a, b) => b.value.compareTo(a.value));

    return MonthlyStats(
      totalIncome: totalIncome,
      totalExpense: totalExpense,
      savings: totalIncome - totalExpense,
      sortedCategories: sortedCategories,
    );
  },
);
