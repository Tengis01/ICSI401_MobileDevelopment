import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../providers/transaction_provider.dart';
import '../../widgets/transaction_tile.dart';
import '../../widgets/app_bottom_nav.dart';
import 'widgets/balance_card.dart';
import 'widgets/quick_actions_row.dart';
import 'widgets/home_empty_state.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  void _showMoreSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: AppColors.border,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            const SizedBox(height: 20),
            Text('Илүү', style: AppTextStyles.h3),
            const SizedBox(height: 16),
            _MoreItem(
              icon: Icons.bar_chart_rounded,
              label: 'Статистик',
              onTap: () {
                Navigator.pop(ctx);
                context.push(AppRoutes.stats);
              },
            ),
            _MoreItem(
              icon: Icons.search_rounded,
              label: 'Хайх',
              onTap: () {
                Navigator.pop(ctx);
                context.push(AppRoutes.search);
              },
            ),
            _MoreItem(
              icon: Icons.person_outline_rounded,
              label: 'Профайл',
              onTap: () {
                Navigator.pop(ctx);
                context.push(AppRoutes.profile);
              },
            ),
            const SizedBox(height: 8),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final user = FirebaseAuth.instance.currentUser;
    final transactionsAsync = ref.watch(recentTransactionsProvider);
    final now = DateTime.now();
    final summaryAsync = ref.watch(
      monthSummaryProvider((year: now.year, month: now.month)),
    );

    final hour = now.hour;
    final greeting = hour < 12
        ? 'Өглөөний мэнд'
        : hour < 17
        ? 'Өдрийн мэнд'
        : 'Оройн мэнд';

    return Scaffold(
      backgroundColor: AppColors.background,
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Container(
              color: Colors.white,
              padding: EdgeInsets.only(
                top: MediaQuery.of(context).padding.top + 8,
                left: 20, right: 20, bottom: 20,
              ),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 20,
                    backgroundColor: AppColors.primaryLight,
                    child: Text(
                      user?.displayName?.isNotEmpty == true
                          ? user!.displayName![0].toUpperCase()
                          : 'Б',
                      style: AppTextStyles.labelLarge.copyWith(
                          color: AppColors.primary),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('$greeting 👋', style: AppTextStyles.bodySmall),
                      Text(
                        user?.displayName ?? 'Хэрэглэгч',
                        style: AppTextStyles.labelLarge,
                      ),
                    ],
                  ),
                  const Spacer(),
                  Stack(
                    children: [
                      IconButton(
                        onPressed: () {},
                        icon: const Icon(Icons.notifications_outlined),
                      ),
                      Positioned(
                        right: 10, top: 10,
                        child: Container(
                          width: 8, height: 8,
                          decoration: const BoxDecoration(
                            color: AppColors.expense,
                            shape: BoxShape.circle,
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 16)),
          SliverToBoxAdapter(
            child: summaryAsync.when(
              data: (s) => BalanceCard(
                totalBalance: s.income - s.expense,
                income: s.income,
                expense: s.expense,
              ),
              loading: () => const _SkeletonBox(
                  height: 140,
                  margin: EdgeInsets.symmetric(horizontal: 20)),
              error: (_, __) => const SizedBox(),
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 24)),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: QuickActionsRow(
                onSend: () => context.push(AppRoutes.send),
                onReceive: () => context.push(AppRoutes.receive),
                onTopUp: () => context.push(AppRoutes.bills),
                onMore: () => _showMoreSheet(context),
              ),
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 24)),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Сүүлийн гүйлгээ', style: AppTextStyles.h3),
                  TextButton(
                    onPressed: () {},
                    child: Text('Бүгд',
                        style: AppTextStyles.labelMedium.copyWith(
                            color: AppColors.primary)),
                  ),
                ],
              ),
            ),
          ),
          transactionsAsync.when(
            data: (transactions) {
              if (transactions.isEmpty) {
                return SliverFillRemaining(
                  child: HomeEmptyState(
                    onAdd: () => context.push(AppRoutes.entry),
                  ),
                );
              }
              return SliverList(
                delegate: SliverChildBuilderDelegate(
                      (context, index) {
                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      child: Column(
                        children: [
                          TransactionTile(
                            transaction: transactions[index],
                            onTap: () => context.push(
                              AppRoutes.transactionPath(transactions[index].id),
                            ),
                          ),
                          const Divider(height: 1),
                        ],
                      ),
                    );
                  },
                  childCount: transactions.length,
                ),
              );
            },
            loading: () => SliverToBoxAdapter(
              child: Column(
                children: List.generate(
                  4,
                      (i) => Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 20, vertical: 10),
                    child: Row(
                      children: [
                        const _SkeletonBox(width: 44, height: 44,
                            borderRadius: 12),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const _SkeletonBox(height: 14, width: 120),
                              const SizedBox(height: 6),
                              const _SkeletonBox(height: 11, width: 80),
                            ],
                          ),
                        ),
                        const _SkeletonBox(height: 14, width: 70),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            error: (e, _) => SliverToBoxAdapter(
              child: Padding(
                padding: const EdgeInsets.all(40),
                child: Text('Алдаа гарлаа',
                    style: AppTextStyles.bodyMedium.copyWith(
                        color: AppColors.textSecondary),
                    textAlign: TextAlign.center),
              ),
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 100)),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push(AppRoutes.entry),
        backgroundColor: AppColors.primary,
        shape: const CircleBorder(),
        child: const Icon(Icons.add_rounded, color: Colors.white, size: 28),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
      // _BottomNav class ustgaad AppBottomNav-g ashiglaj baina
      bottomNavigationBar: const AppBottomNav(currentIndex: 0),
    );
  }
}

class _MoreItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _MoreItem({
    required this.icon,
    required this.label,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Icon(icon, color: AppColors.primary),
      title: Text(label, style: AppTextStyles.bodyLarge),
      onTap: onTap,
    );
  }
}

// _BottomNav болон _NavItem class-g USTGASAN
// app_bottom_nav.dart-d reusable bolgoson

class _SkeletonBox extends StatelessWidget {
  final double? width;
  final double height;
  final double borderRadius;
  final EdgeInsets? margin;

  const _SkeletonBox({
    this.width,
    required this.height,
    this.borderRadius = 6,
    this.margin,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      margin: margin,
      decoration: BoxDecoration(
        color: AppColors.surfaceVariant,
        borderRadius: BorderRadius.circular(borderRadius),
      ),
    );
  }
}