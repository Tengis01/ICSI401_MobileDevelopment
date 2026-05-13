import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/utils/currency_formatter.dart';
import '../../core/utils/date_formatter.dart';
import '../../providers/transaction_provider.dart';
import '../../widgets/app_bottom_nav.dart';

class StatsScreen extends ConsumerStatefulWidget {
  const StatsScreen({super.key});

  @override
  ConsumerState<StatsScreen> createState() => _StatsScreenState();
}

class _StatsScreenState extends ConsumerState<StatsScreen> {
  late int _year;
  late int _month;

  @override
  void initState() {
    super.initState();
    _year = DateTime.now().year;
    _month = DateTime.now().month;
  }

  void _prevMonth() {
    setState(() {
      if (_month == 1) { _month = 12; _year--; }
      else { _month--; }
    });
  }

  void _nextMonth() {
    final now = DateTime.now();
    // ireedui sar ruu solgohgui
    if (_year == now.year && _month == now.month) return;
    setState(() {
      if (_month == 12) { _month = 1; _year++; }
      else { _month++; }
    });
  }

  @override
  Widget build(BuildContext context) {
    final statsAsync = ref.watch(
      monthlyStatsProvider((year: _year, month: _month)),
    );
    final now = DateTime.now();
    final isCurrentMonth = _year == now.year && _month == now.month;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        automaticallyImplyLeading: false,
        title: const Text('Статистик'),
        centerTitle: false,
      ),
      body: statsAsync.when(
        data: (stats) => _StatsBody(
          stats: stats,
          year: _year,
          month: _month,
          isCurrentMonth: isCurrentMonth,
          onPrev: _prevMonth,
          onNext: _nextMonth,
        ),
        loading: () => const Center(
            child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Алдаа: $e')),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        backgroundColor: AppColors.primary,
        shape: const CircleBorder(),
        child: const Icon(Icons.add_rounded,
            color: Colors.white, size: 28),
      ),
      floatingActionButtonLocation:
      FloatingActionButtonLocation.centerDocked,
      bottomNavigationBar: const AppBottomNav(currentIndex: 1),
    );
  }
}

class _StatsBody extends StatelessWidget {
  final MonthlyStats stats;
  final int year;
  final int month;
  final bool isCurrentMonth;
  final VoidCallback onPrev;
  final VoidCallback onNext;

  const _StatsBody({
    required this.stats,
    required this.year,
    required this.month,
    required this.isCurrentMonth,
    required this.onPrev,
    required this.onNext,
  });

  @override
  Widget build(BuildContext context) {
    final totalIncome = stats.totalIncome;
    final totalExpense = stats.totalExpense;
    final savings = stats.savings;
    final sortedCategories = stats.sortedCategories;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // sar songoh
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Row(
              children: [
                IconButton(
                  onPressed: onPrev,
                  icon: const Icon(Icons.chevron_left_rounded),
                ),
                Expanded(
                  child: Text(
                    DateFormatter.formatMonth(year, month),
                    style: AppTextStyles.labelLarge,
                    textAlign: TextAlign.center,
                  ),
                ),
                IconButton(
                  onPressed: isCurrentMonth ? null : onNext,
                  icon: Icon(
                    Icons.chevron_right_rounded,
                    color: isCurrentMonth
                        ? AppColors.textHint
                        : AppColors.textPrimary,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          // summary card
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              children: [
                Row(
                  children: [
                    Expanded(
                      child: _SummaryItem(
                        label: 'Орлого',
                        amount: totalIncome,
                        color: AppColors.income,
                        icon: Icons.arrow_downward_rounded,
                      ),
                    ),
                    Container(
                        width: 1, height: 48,
                        color: AppColors.border),
                    Expanded(
                      child: _SummaryItem(
                        label: 'Зарлага',
                        amount: totalExpense,
                        color: AppColors.expense,
                        icon: Icons.arrow_upward_rounded,
                      ),
                    ),
                  ],
                ),
                const Divider(height: 24),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('Хэмнэлт',
                        style: AppTextStyles.bodyMedium.copyWith(
                            color: AppColors.textSecondary)),
                    Text(
                      CurrencyFormatter.formatWithSign(
                          savings.abs(),
                          isIncome: savings >= 0),
                      style: AppTextStyles.labelLarge.copyWith(
                        color: savings >= 0
                            ? AppColors.income
                            : AppColors.expense,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),
          // guriljaa alga baiwal
          if (sortedCategories.isEmpty && totalIncome == 0)
            Container(
              padding: const EdgeInsets.all(40),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Center(
                child: Column(
                  children: [
                    Icon(Icons.bar_chart_rounded,
                        size: 48, color: AppColors.primaryLight),
                    const SizedBox(height: 12),
                    Text(
                      'Энэ сард гүйлгээ алга',
                      style: AppTextStyles.bodyMedium.copyWith(
                          color: AppColors.textSecondary),
                    ),
                  ],
                ),
              ),
            )
          else ...[
            // zarlagiin pai chart
            if (sortedCategories.isNotEmpty) ...[
              Text('Зарлагын ангилал',
                  style: AppTextStyles.h3),
              const SizedBox(height: 12),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  children: [
                    // pie chart
                    SizedBox(
                      height: 180,
                      child: PieChart(
                        PieChartData(
                          sectionsSpace: 2,
                          centerSpaceRadius: 40,
                          sections: _buildPieSections(
                              sortedCategories, totalExpense),
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    // legend
                    ...sortedCategories.take(5).map((entry) {
                      final pct = totalExpense > 0
                          ? (entry.value / totalExpense * 100)
                          .toStringAsFixed(1)
                          : '0';
                      final index =
                      sortedCategories.indexOf(entry);
                      final color =
                      _chartColors[index % _chartColors.length];
                      return Padding(
                        padding:
                        const EdgeInsets.symmetric(vertical: 4),
                        child: Row(
                          children: [
                            Container(
                              width: 10, height: 10,
                              decoration: BoxDecoration(
                                color: color,
                                shape: BoxShape.circle,
                              ),
                            ),
                            const SizedBox(width: 8),
                            Expanded(
                              child: Text(entry.key,
                                  style: AppTextStyles.bodyMedium),
                            ),
                            Text('$pct%',
                                style: AppTextStyles.bodySmall),
                            const SizedBox(width: 8),
                            Text(
                              CurrencyFormatter.format(entry.value),
                              style: AppTextStyles.labelMedium,
                            ),
                          ],
                        ),
                      );
                    }),
                  ],
                ),
              ),
            ],
          ],
          const SizedBox(height: 100),
        ],
      ),
    );
  }

  static final List<Color> _chartColors = [
    AppColors.primary,
    AppColors.expense,
    AppColors.income,
    Color(0xFFFFCA28),
    Color(0xFFAB47BC),
    Color(0xFF42A5F5),
    Color(0xFFFF8A65),
  ];

  List<PieChartSectionData> _buildPieSections(
      List<MapEntry<String, int>> categories,
      int total,
      ) {
    return List.generate(
      categories.length > 5 ? 5 : categories.length,
          (index) {
        final entry = categories[index];
        final pct = total > 0 ? entry.value / total * 100 : 0.0;
        return PieChartSectionData(
          value: entry.value.toDouble(),
          title: '${pct.toStringAsFixed(0)}%',
          radius: 50,
          color: _chartColors[index % _chartColors.length],
          titleStyle: const TextStyle(
            fontSize: 11,
            fontWeight: FontWeight.w600,
            color: Colors.white,
          ),
        );
      },
    );
  }
}

class _SummaryItem extends StatelessWidget {
  final String label;
  final int amount;
  final Color color;
  final IconData icon;

  const _SummaryItem({
    required this.label,
    required this.amount,
    required this.color,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: color, size: 14),
            const SizedBox(width: 4),
            Text(label,
                style: AppTextStyles.bodySmall.copyWith(
                    color: AppColors.textSecondary)),
          ],
        ),
        const SizedBox(height: 4),
        FittedBox(
          fit: BoxFit.scaleDown,
          child: Text(
            CurrencyFormatter.format(amount),
            style: AppTextStyles.labelLarge.copyWith(color: color),
            maxLines: 1,
          ),
        ),
      ],
    );
  }
}