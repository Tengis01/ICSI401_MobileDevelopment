import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import '../../models/category_model.dart';
import '../../models/transaction_model.dart';
import '../../providers/transaction_provider.dart';
import '../../services/app_notification_service.dart';
import '../../services/notification_service.dart';
import '../../widgets/app_button.dart';
import 'widgets/amount_input.dart';
import 'widgets/category_grid.dart';
import 'widgets/date_picker_row.dart';
import 'widgets/type_toggle.dart';

class EntryScreen extends ConsumerStatefulWidget {
  const EntryScreen({super.key});

  @override
  ConsumerState<EntryScreen> createState() => _EntryScreenState();
}

class _EntryScreenState extends ConsumerState<EntryScreen> {
  final _amountController = TextEditingController();
  final _noteController = TextEditingController();

  TransactionType _type = TransactionType.expense;
  CategoryModel? _selectedCategory;
  DateTime _selectedDate = DateTime.now();
  bool _isLoading = false;

  @override
  void dispose() {
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  bool get _canSave =>
      _amountController.text.isNotEmpty &&
          int.tryParse(_amountController.text) != null &&
          int.parse(_amountController.text) > 0 &&
          _selectedCategory != null;

  Future<void> _save() async {
    if (!_canSave) return;
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) return;

    setState(() => _isLoading = true);

    try {
      final service = ref.read(transactionServiceProvider);

      final docRef = FirebaseFirestore.instance
          .collection(AppConstants.usersCollection)
          .doc(user.uid)
          .collection(AppConstants.transactionsCollection)
          .doc();

      final tx = TransactionModel(
        id: docRef.id,
        userId: user.uid,
        type: _type,
        amount: int.parse(_amountController.text),
        categoryId: _selectedCategory!.id,
        categoryName: _selectedCategory!.name,
        categoryIcon: _selectedCategory!.icon,
        note: _noteController.text.trim().isEmpty
            ? null
            : _noteController.text.trim(),
        date: _selectedDate,
        createdAt: DateTime.now(),
      );

      await service.addTransaction(tx);

      // guriljaa hadgalagdsan ued notification
      await NotificationService.instance.showTransactionNotification(
        categoryName: _selectedCategory!.name,
        amount: int.parse(_amountController.text),
        isIncome: _type == TransactionType.income,
      );

      await AppNotificationService.instance.addNotification(
        title: _type == TransactionType.income
            ? 'Орлого бүртгэгдлээ'
            : 'Зарлага бүртгэгдлээ',
        body: '${_selectedCategory!.name} · ${_amountController.text}₮',
        type: _type == TransactionType.income
            ? AppNotificationType.income
            : AppNotificationType.expense,
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Row(
            children: [
              const Icon(Icons.check_circle_rounded,
                  color: Colors.white, size: 18),
              const SizedBox(width: 8),
              Text('Амжилттай хадгалагдлаа',
                  style: AppTextStyles.bodyMedium.copyWith(
                      color: Colors.white)),
            ],
          ),
          backgroundColor: AppColors.success,
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10)),
          margin: const EdgeInsets.all(16),
          duration: const Duration(seconds: 2),
        ),
      );
      context.pop();
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Алдаа гарлаа: $e'),
          backgroundColor: AppColors.error,
          behavior: SnackBarBehavior.floating,
        ),
      );
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Гүйлгээ нэмэх'),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 8),
            // expense / income toggle
            TypeToggle(
              selectedType: _type,
              onChanged: (type) {
                setState(() {
                  _type = type;
                  // toggle hiihed category-g clear hiih
                  _selectedCategory = null;
                });
              },
            ),
            const SizedBox(height: 28),
            // dun input
            AmountInput(
              controller: _amountController,
              type: _type,
            ),
            const SizedBox(height: 28),
            // angilal
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Ангилал', style: AppTextStyles.labelLarge),
                if (_selectedCategory != null)
                  Text(
                    _selectedCategory!.name,
                    style: AppTextStyles.labelMedium.copyWith(
                        color: AppColors.primary),
                  ),
              ],
            ),
            const SizedBox(height: 12),
            CategoryGrid(
              type: _type,
              selectedCategory: _selectedCategory,
              onSelected: (cat) {
                setState(() => _selectedCategory = cat);
              },
            ),
            const SizedBox(height: 20),
            // temdeglel
            Text('ТЭМДЭГЛЭЛ', style: AppTextStyles.labelSmall.copyWith(
              letterSpacing: 0.8, color: AppColors.textSecondary,
            )),
            const SizedBox(height: 6),
            TextField(
              controller: _noteController,
              style: AppTextStyles.bodyLarge,
              maxLines: 2,
              decoration: InputDecoration(
                hintText: 'Тэмдэглэл нэмэх...',
                hintStyle: AppTextStyles.bodyMedium.copyWith(
                    color: AppColors.textHint),
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
                  borderSide: BorderSide(
                      color: AppColors.primary, width: 1.5),
                ),
              ),
            ),
            const SizedBox(height: 16),
            // ognoo
            Text('ОГНОО', style: AppTextStyles.labelSmall.copyWith(
              letterSpacing: 0.8, color: AppColors.textSecondary,
            )),
            const SizedBox(height: 6),
            DatePickerRow(
              selectedDate: _selectedDate,
              onDateChanged: (date) {
                setState(() => _selectedDate = date);
              },
            ),
            const SizedBox(height: 32),
            // hadgalah button
            ListenableBuilder(
              listenable: _amountController,
              builder: (context, _) {
                return AppButton(
                  label: 'Хадгалах',
                  onPressed: _canSave ? _save : null,
                  isLoading: _isLoading,
                );
              },
            ),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}
