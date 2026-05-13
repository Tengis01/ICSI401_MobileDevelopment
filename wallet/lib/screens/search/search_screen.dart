import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/category_data.dart';
import '../../models/category_model.dart';
import '../../models/transaction_model.dart';
import '../../providers/auth_provider.dart';
import '../../providers/transaction_provider.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/transaction_tile.dart';

class SearchScreen extends ConsumerStatefulWidget {
  const SearchScreen({super.key});

  @override
  ConsumerState<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends ConsumerState<SearchScreen> {
  final _searchController = TextEditingController();
  List<TransactionModel> _results = [];
  List<String> _recentSearches = [];
  bool _isSearching = false;
  bool _hasSearched = false;

  static const String _recentKey = 'recent_searches';

  @override
  void initState() {
    super.initState();
    _loadRecentSearches();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadRecentSearches() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _recentSearches = prefs.getStringList(_recentKey) ?? [];
    });
  }

  Future<void> _saveRecentSearch(String query) async {
    final prefs = await SharedPreferences.getInstance();
    final list = prefs.getStringList(_recentKey) ?? [];
    list.remove(query);
    list.insert(0, query);
    if (list.length > 8) list.removeLast();
    await prefs.setStringList(_recentKey, list);
    setState(() => _recentSearches = list);
  }

  Future<void> _search(String query) async {
    if (query.trim().isEmpty) return;

    final user = ref.read(currentUserProvider);
    if (user == null) return;

    setState(() => _isSearching = true);

    await _saveRecentSearch(query.trim());

    final results = await ref
        .read(transactionServiceProvider)
        .searchTransactions(user.uid, query.trim());

    setState(() {
      _results = results;
      _isSearching = false;
      _hasSearched = true;
    });
  }

  void _quickSearch(CategoryModel category) {
    _searchController.text = category.name;
    _search(category.name);
  }

  Future<void> _clearRecent() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_recentKey);
    setState(() => _recentSearches = []);
  }

  void _removeRecentItem(String item) async {
    final prefs = await SharedPreferences.getInstance();
    _recentSearches.remove(item);
    await prefs.setStringList(_recentKey, _recentSearches);
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        automaticallyImplyLeading: false,
        title: TextField(
          controller: _searchController,
          autofocus: true,
          style: AppTextStyles.bodyLarge,
          decoration: InputDecoration(
            hintText: 'Гүйлгээ хайх...',
            hintStyle:
                AppTextStyles.bodyMedium.copyWith(color: AppColors.textHint),
            prefixIcon: const Icon(Icons.search_rounded,
                color: AppColors.textSecondary),
            suffixIcon: _searchController.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.close_rounded,
                        color: AppColors.textSecondary, size: 20),
                    onPressed: () {
                      _searchController.clear();
                      setState(() {
                        _results = [];
                        _hasSearched = false;
                      });
                    },
                  )
                : null,
            filled: true,
            fillColor: AppColors.surfaceVariant,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide(color: AppColors.primary, width: 1.5),
            ),
            contentPadding:
                const EdgeInsets.symmetric(vertical: 0, horizontal: 16),
            isDense: true,
          ),
          onChanged: (v) => setState(() {}),
          onSubmitted: _search,
          textInputAction: TextInputAction.search,
        ),
        actions: [
          TextButton(
            onPressed: () => context.go(AppRoutes.home),
            child: Text('Цуцлах',
                style: AppTextStyles.labelMedium
                    .copyWith(color: AppColors.textSecondary)),
          ),
        ],
      ),
      body: _isSearching
          ? const Center(child: CircularProgressIndicator())
          : _hasSearched
              ? _SearchResults(
                  results: _results,
                  query: _searchController.text,
                )
              : _RecentSearches(
                  searches: _recentSearches,
                  onCategoryTap: _quickSearch,
                  onTap: (q) {
                    _searchController.text = q;
                    _search(q);
                  },
                  onRemove: _removeRecentItem,
                  onClear: _clearRecent,
                ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push(AppRoutes.entry),
        backgroundColor: AppColors.primary,
        shape: const CircleBorder(),
        child: const Icon(Icons.add_rounded, color: Colors.white, size: 28),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
      bottomNavigationBar: const AppBottomNav(currentIndex: 2),
    );
  }
}

class _RecentSearches extends StatelessWidget {
  final List<String> searches;
  final ValueChanged<CategoryModel> onCategoryTap;
  final ValueChanged<String> onTap;
  final ValueChanged<String> onRemove;
  final VoidCallback onClear;

  const _RecentSearches({
    required this.searches,
    required this.onCategoryTap,
    required this.onTap,
    required this.onRemove,
    required this.onClear,
  });

  @override
  Widget build(BuildContext context) {
    final categories = [
      ...CategoryData.expenseCategories,
      ...CategoryData.incomeCategories,
    ];

    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text('Түргэн хайлт', style: AppTextStyles.labelLarge),
        const SizedBox(height: 12),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: categories.map((category) {
            final color = Color(category.colorValue);
            return InkWell(
              onTap: () => onCategoryTap(category),
              borderRadius: BorderRadius.circular(100),
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 12,
                  vertical: 8,
                ),
                decoration: BoxDecoration(
                  color: color.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(100),
                  border: Border.all(color: color.withValues(alpha: 0.25)),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      CategoryData.getIconData(category.icon),
                      color: color,
                      size: 16,
                    ),
                    const SizedBox(width: 6),
                    Text(
                      category.name,
                      style: AppTextStyles.labelMedium.copyWith(
                        color: AppColors.textPrimary,
                      ),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),
        ),
        const SizedBox(height: 24),
        if (searches.isEmpty) ...[
          Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              children: [
                Icon(Icons.search_rounded,
                    size: 48, color: AppColors.primaryLight),
                const SizedBox(height: 12),
                Text('Хайлт хийнэ үү', style: AppTextStyles.h3),
                const SizedBox(height: 8),
                Text(
                  'Ангилал эсвэл тэмдэглэлээр хайж болно',
                  style: AppTextStyles.bodyMedium
                      .copyWith(color: AppColors.textSecondary),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ] else ...[
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text('Сүүлд хайсан', style: AppTextStyles.labelLarge),
              TextButton(
                onPressed: onClear,
                child: Text('Цэвэрлэх',
                    style: AppTextStyles.labelMedium
                        .copyWith(color: AppColors.textSecondary)),
              ),
            ],
          ),
          const SizedBox(height: 8),
          ...searches.map((q) => ListTile(
                contentPadding: EdgeInsets.zero,
                leading: const Icon(Icons.history_rounded,
                    color: AppColors.textSecondary),
                title: Text(q, style: AppTextStyles.bodyMedium),
                trailing: IconButton(
                  icon: const Icon(Icons.close_rounded,
                      size: 18, color: AppColors.textSecondary),
                  onPressed: () => onRemove(q),
                ),
                onTap: () => onTap(q),
              )),
        ],
      ],
    );
  }
}

class _SearchResults extends StatelessWidget {
  final List<TransactionModel> results;
  final String query;

  const _SearchResults({required this.results, required this.query});

  @override
  Widget build(BuildContext context) {
    if (results.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.search_off_rounded,
                size: 64, color: AppColors.primaryLight),
            const SizedBox(height: 16),
            Text('"$query" олдсонгүй',
                style: AppTextStyles.h3, textAlign: TextAlign.center),
            const SizedBox(height: 8),
            Text(
              'Өөр үгээр хайж үзнэ үү',
              style: AppTextStyles.bodyMedium
                  .copyWith(color: AppColors.textSecondary),
            ),
          ],
        ),
      );
    }

    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text('${results.length} үр дүн олдлоо', style: AppTextStyles.bodySmall),
        const SizedBox(height: 12),
        ...results.map((tx) => Column(
              children: [
                TransactionTile(
                  transaction: tx,
                  onTap: () => context.push(AppRoutes.transactionPath(tx.id)),
                ),
                const Divider(height: 1),
              ],
            )),
      ],
    );
  }
}
