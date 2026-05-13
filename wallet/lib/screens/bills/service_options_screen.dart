import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../core/constants/bill_data.dart';
import '../../models/bill_model.dart';
import '../../widgets/app_button.dart';

class ServiceOptionsScreen extends StatefulWidget {
  final String serviceId;

  const ServiceOptionsScreen({super.key, required this.serviceId});

  @override
  State<ServiceOptionsScreen> createState() => _ServiceOptionsScreenState();
}

class _ServiceOptionsScreenState extends State<ServiceOptionsScreen> {
  String? _selectedProviderId;
  final _inputController = TextEditingController();
  final _customAmountController = TextEditingController();
  int? _selectedAmount = 5000;
  
  final List<int?> _amounts = [5000, 10000, 20000, 50000, null];

  @override
  void dispose() {
    _inputController.dispose();
    _customAmountController.dispose();
    super.dispose();
  }

  BillModel get bill => BillData.services.firstWhere((s) => s.id == widget.serviceId);

  bool get _canProceed {
    if (_selectedProviderId == null) return false;
    if (_inputController.text.isEmpty) return false;
    
    if (bill.amount == null) {
       if (_selectedAmount == null) {
          if (_customAmountController.text.isEmpty || (int.tryParse(_customAmountController.text) ?? 0) <= 0) {
             return false;
          }
       }
    }
    return true;
  }

  int get _finalAmount {
    if (bill.amount != null) return bill.amount!;
    if (_selectedAmount != null) return _selectedAmount!;
    return int.tryParse(_customAmountController.text) ?? 0;
  }

  String _fmt(int v) => v.toString().replaceAllMapped(
        RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: Text(bill.name),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('ОПЕРАТОР СОНГОХ', style: TextStyle(fontSize: 11, letterSpacing: 0.8, color: AppColors.textSecondary, fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            GridView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 8,
                mainAxisSpacing: 8,
                childAspectRatio: 170 / 56,
              ),
              itemCount: bill.providerOptions!.length,
              itemBuilder: (context, index) {
                final opt = bill.providerOptions![index];
                final isSelected = _selectedProviderId == opt.id;
                return GestureDetector(
                  onTap: () => setState(() => _selectedProviderId = opt.id),
                  child: Container(
                    decoration: BoxDecoration(
                      color: isSelected ? const Color(0xFFF5F3FF) : Colors.white,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: isSelected ? const Color(0xFF6C47FF) : AppColors.border,
                        width: 1.5,
                      ),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Container(
                          width: 32,
                          height: 32,
                          decoration: BoxDecoration(
                            color: opt.color,
                            shape: BoxShape.circle,
                            boxShadow: [
                              BoxShadow(
                                color: opt.color.withValues(alpha: 0.28),
                                blurRadius: 8,
                                offset: const Offset(0, 3),
                              ),
                            ],
                          ),
                          alignment: Alignment.center,
                          child: Text(
                            opt.name.substring(0, 1).toUpperCase(),
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 14,
                              fontWeight: FontWeight.w800,
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(opt.name, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppColors.textPrimary)),
                      ],
                    ),
                  ),
                );
              },
            ),
            const SizedBox(height: 24),
            
            Text(widget.serviceId == 'phone' ? 'УТАСНЫ ДУГААР' : 'ГЭРЭЭНИЙ ДУГААР', style: const TextStyle(fontSize: 11, letterSpacing: 0.8, color: AppColors.textSecondary, fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppColors.border, width: 1.5),
              ),
              child: TextField(
                controller: _inputController,
                keyboardType: widget.serviceId == 'phone' ? TextInputType.phone : TextInputType.text,
                onChanged: (_) => setState(() {}),
                decoration: InputDecoration(
                  hintText: widget.serviceId == 'phone' ? '9911 2233' : 'Оруулах...',
                  prefixIcon: Icon(widget.serviceId == 'phone' ? Icons.smartphone_rounded : Icons.numbers_rounded, color: AppColors.textSecondary),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(vertical: 16),
                ),
              ),
            ),
            
            if (bill.amount == null) ...[
              const SizedBox(height: 24),
              const Text('ДҮН СОНГОХ', style: TextStyle(fontSize: 11, letterSpacing: 0.8, color: AppColors.textSecondary, fontWeight: FontWeight.w600)),
              const SizedBox(height: 12),
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: _amounts.map((amt) {
                    final isSelected = _selectedAmount == amt;
                    final isCustom = amt == null;
                    return Padding(
                      padding: const EdgeInsets.only(right: 8),
                      child: GestureDetector(
                        onTap: () {
                          setState(() {
                            _selectedAmount = amt;
                            if (!isCustom) _customAmountController.clear();
                          });
                        },
                        child: Container(
                          height: 36,
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          decoration: BoxDecoration(
                            color: isSelected ? const Color(0xFF6C47FF) : const Color(0xFFF2F2F7),
                            borderRadius: BorderRadius.circular(100),
                          ),
                          alignment: Alignment.center,
                          child: Text(
                            isCustom ? 'Бусад' : '${_fmt(amt)}₮',
                            style: TextStyle(
                              fontSize: 14,
                              color: isSelected ? Colors.white : AppColors.textPrimary,
                              fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
                            ),
                          ),
                        ),
                      ),
                    );
                  }).toList(),
                ),
              ),
              if (_selectedAmount == null) ...[
                 const SizedBox(height: 12),
                 Container(
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: AppColors.border, width: 1.5),
                  ),
                  child: TextField(
                    controller: _customAmountController,
                    keyboardType: TextInputType.number,
                    inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                    onChanged: (_) => setState(() {}),
                    decoration: const InputDecoration(
                      hintText: 'Дүн оруулах...',
                      prefixIcon: Icon(Icons.money_rounded, color: AppColors.textSecondary),
                      border: InputBorder.none,
                      contentPadding: EdgeInsets.symmetric(vertical: 16),
                    ),
                  ),
                ),
              ],
            ],
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.only(left: 16, right: 16, bottom: 32, top: 16),
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(100),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF6C47FF).withValues(alpha:0.3),
                  blurRadius: 20,
                  offset: const Offset(0, 8),
                ),
              ],
            ),
            child: AppButton(
              label: widget.serviceId == 'phone' ? 'Цэнэглэх' : 'Үргэлжлүүлэх',
              onPressed: _canProceed
                  ? () => context.push(
                AppRoutes.billMethodPath(widget.serviceId),
                extra: _finalAmount,
              )
                  : null,
            ),
          ),
        ),
      ),
    );
  }
}
