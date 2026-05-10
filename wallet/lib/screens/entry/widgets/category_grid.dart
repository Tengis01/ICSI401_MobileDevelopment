import 'package:flutter/material.dart';
import '../../../app/theme/app_colors.dart';
import '../../../app/theme/app_text_styles.dart';
import '../../../core/constants/category_data.dart';
import '../../../models/category_model.dart';
import '../../../models/transaction_model.dart';

class CategoryGrid extends StatelessWidget {
  final TransactionType type;
  final CategoryModel? selectedCategory;
  final ValueChanged<CategoryModel> onSelected;

  const CategoryGrid({
    super.key,
    required this.type,
    required this.selectedCategory,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    final categories = type == TransactionType.expense
        ? CategoryData.expenseCategories
        : CategoryData.incomeCategories;

    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 4,
        mainAxisSpacing: 12,
        crossAxisSpacing: 12,
        childAspectRatio: 0.85,
      ),
      itemCount: categories.length,
      itemBuilder: (context, index) {
        final cat = categories[index];
        final isSelected = selectedCategory?.id == cat.id;
        final catColor = Color(cat.colorValue);

        return GestureDetector(
          onTap: () => onSelected(cat),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 180),
            decoration: BoxDecoration(
              color: isSelected ? catColor : AppColors.surfaceVariant,
              borderRadius: BorderRadius.circular(14),
              border: isSelected
                  ? null
                  : Border.all(color: AppColors.border),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  CategoryData.getIconData(cat.icon),
                  color: isSelected ? Colors.white : catColor,
                  size: 26,
                ),
                const SizedBox(height: 5),
                Text(
                  cat.name,
                  style: AppTextStyles.bodySmall.copyWith(
                    fontSize: 10,
                    color: isSelected
                        ? Colors.white
                        : AppColors.textSecondary,
                    fontWeight: isSelected
                        ? FontWeight.w600
                        : FontWeight.w400,
                  ),
                  textAlign: TextAlign.center,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}