import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../core/constants/bill_data.dart';
import '../../models/bill_model.dart';

class BillsScreen extends StatelessWidget {
  const BillsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final totalDue = BillData.totalDue;
    final pendingCount = BillData.pendingCount;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: Colors.white,
        leading: IconButton(
          onPressed: () => context.pop(),
          icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
        ),
        title: const Text('Төлбөр төлөх', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
        centerTitle: true,
        actions: [
          IconButton(onPressed: () {}, icon: const Icon(Icons.more_horiz_rounded, color: AppColors.textPrimary)),
        ],
      ),
      body: Column(
        children: [
          // niit dun card - full width
          Container(
            margin: const EdgeInsets.fromLTRB(16, 16, 16, 0),
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [Color(0xFF8B67FF), Color(0xFF5C3DCC)],
              ),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'ТӨЛБӨЛ ЗОХИХ',
                        style: TextStyle(
                          fontSize: 11,
                          letterSpacing: 0.8,
                          color: Colors.white.withValues(alpha: 0.7),
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        '${_fmt(totalDue)} ₮',
                        style: const TextStyle(
                          fontSize: 28,
                          fontWeight: FontWeight.w700,
                          color: Colors.white,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '$pendingCount нэхэмжлэх хүлээгдэж байна',
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.white.withValues(alpha: 0.7),
                        ),
                      ),
                    ],
                  ),
                ),
                Container(
                  width: 48, height: 48,
                  decoration: BoxDecoration(
                    color: Colors.white.withValues(alpha: 0.2),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.receipt_long_rounded, color: Colors.white, size: 24),
                ),
              ],
            ),
          ),
          // uylchilgee list
          Padding(
            padding: const EdgeInsets.only(left: 16, right: 16, top: 12),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(
                'ҮЙЛЧИЛГЭЭ',
                style: TextStyle(
                  fontSize: 11,
                  letterSpacing: 0.8,
                  color: AppColors.textSecondary,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          const SizedBox(height: 12),
          Expanded(
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: ListView.separated(
                padding: EdgeInsets.zero,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: BillData.services.length,
                separatorBuilder: (_, __) =>
                const Divider(height: 0.5, color: AppColors.border, indent: 64),
                itemBuilder: (context, index) {
                  final bill = BillData.services[index];
                  return SizedBox(
                    height: 68,
                    child: _BillTile(
                      bill: bill,
                      onTap: () {
                        if (bill.providerOptions != null) {
                           context.push('/bills/${bill.id}/options');
                        } else {
                           context.push(AppRoutes.billDetailPath(bill.id));
                        }
                      },
                    ),
                  );
                },
              ),
            ),
          ),
          const SizedBox(height: 16),
        ],
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.only(left: 16, right: 16, bottom: 24, top: 12),
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(100),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF6C47FF).withValues(alpha: 0.35),
                  blurRadius: 20,
                  offset: const Offset(0, 8),
                ),
              ],
            ),
            child: ElevatedButton(
              onPressed: () => context.push(
                AppRoutes.billMethodPath('all'),
                extra: BillData.totalDue,
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF6C47FF),
                minimumSize: const Size.fromHeight(52),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(100)),
                elevation: 0,
              ),
              child: Text(
                'Бүгдийг төлөх  ${_fmt(totalDue)}₮',
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );
}

class _BillTile extends StatelessWidget {
  final BillModel bill;
  final VoidCallback onTap;

  const _BillTile({required this.bill, required this.onTap});

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: onTap,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16),
      leading: Container(
        width: 44, height: 44,
        decoration: BoxDecoration(
          color: bill.bgIconColor,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Icon(bill.icon, color: bill.iconColor, size: 22),
      ),
      title: Text(bill.name, style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w600, color: AppColors.textPrimary)),
      subtitle: Text(bill.provider, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary)),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Flexible(
            child: bill.amount != null
                ? FittedBox(
                    fit: BoxFit.scaleDown,
                    child: Text(
                      '${_fmt(bill.amount!)}₮',
                      style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppColors.expense),
                      maxLines: 1,
                    ),
                  )
                : const Text(
                    'Дүн оруулах',
                    style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
                  ),
          ),
          const SizedBox(width: 8),
          const Icon(Icons.chevron_right_rounded,
              color: AppColors.textSecondary, size: 20),
        ],
      ),
    );
  }
}
