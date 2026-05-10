import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../models/wallet_model.dart';
import '../../services/wallet_service.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/app_button.dart';

class WalletScreen extends StatefulWidget {
  const WalletScreen({super.key});

  @override
  State<WalletScreen> createState() => _WalletScreenState();
}

class _WalletScreenState extends State<WalletScreen> {
  List<WalletCard> _cards = [];
  int _selectedIndex = 0;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadCards();
  }

  Future<void> _loadCards() async {
    final cards = await WalletService.instance.getCards();
    setState(() {
      _cards = cards;
      _isLoading = false;
    });
  }

  Future<void> _deleteCard(String cardId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16)),
        title: const Text('Карт устгах уу?'),
        content: const Text(
            'Энэ картыг устгахдаа итгэлтэй байна уу?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Болих'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: Text('Устгах',
                style: TextStyle(color: AppColors.error)),
          ),
        ],
      ),
    );

    if (confirmed != true) return;
    await WalletService.instance.deleteCard(cardId);
    await _loadCards();
    if (_selectedIndex >= _cards.length && _selectedIndex > 0) {
      setState(() => _selectedIndex = _cards.length - 1);
    }
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
        automaticallyImplyLeading: false,
        title: const Text('Хэтэвч'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _cards.isEmpty
          ? _EmptyWallet(
        onAddCard: () async {
          final result =
          await context.push(AppRoutes.addCard);
          if (result == true) _loadCards();
        },
      )
          : _WalletContent(
        cards: _cards,
        selectedIndex: _selectedIndex,
        onPageChanged: (i) =>
            setState(() => _selectedIndex = i),
        onAddCard: () async {
          final result =
          await context.push(AppRoutes.addCard);
          if (result == true) _loadCards();
        },
        onDeleteCard: _deleteCard,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push(AppRoutes.entry),
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

class _WalletContent extends StatelessWidget {
  final List<WalletCard> cards;
  final int selectedIndex;
  final ValueChanged<int> onPageChanged;
  final VoidCallback onAddCard;
  final ValueChanged<String> onDeleteCard;

  const _WalletContent({
    required this.cards,
    required this.selectedIndex,
    required this.onPageChanged,
    required this.onAddCard,
    required this.onDeleteCard,
  });

  String _fmt(int v) => v.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'),
        (m) => '${m[1]},',
  );

  @override
  Widget build(BuildContext context) {
    final selected = cards[selectedIndex];

    return Column(
      children: [
        const SizedBox(height: 16),
        // kart carousel
        SizedBox(
          height: 200,
          child: PageView.builder(
            padEnds: false,
            controller: PageController(
              viewportFraction: 0.85,
              initialPage: selectedIndex,
            ),
            itemCount: cards.length,
            onPageChanged: onPageChanged,
            itemBuilder: (context, index) {
              final card = cards[index];
              final isActive = index == selectedIndex;
              return AnimatedScale(
                scale: isActive ? 1.0 : 0.92,
                duration: const Duration(milliseconds: 300),
                child: GestureDetector(
                  onLongPress: () => onDeleteCard(card.id),
                  child: _CardWidget(card: card),
                ),
              );
            },
          ),
        ),
        const SizedBox(height: 8),
        // dots
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(cards.length, (i) {
            return AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              margin: const EdgeInsets.symmetric(horizontal: 3),
              width: i == selectedIndex ? 20 : 6,
              height: 6,
              decoration: BoxDecoration(
                color: i == selectedIndex
                    ? AppColors.primary
                    : AppColors.border,
                borderRadius: BorderRadius.circular(3),
              ),
            );
          }),
        ),
        const SizedBox(height: 16),
        // selected card balance
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(14),
            ),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(selected.bankName,
                          style: AppTextStyles.labelMedium),
                      Text(
                        '**** ${selected.last4}',
                        style: AppTextStyles.bodySmall,
                      ),
                    ],
                  ),
                ),
                Text(
                  '${_fmt(selected.balance)}₮',
                  style: AppTextStyles.h3.copyWith(
                      color: AppColors.primary),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        // shine kart nemeh
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: OutlinedButton.icon(
            onPressed: onAddCard,
            icon: Icon(Icons.add_rounded,
                color: AppColors.primary, size: 18),
            label: Text('Шинэ карт нэмэх',
                style: TextStyle(color: AppColors.primary)),
            style: OutlinedButton.styleFrom(
              side: BorderSide(color: AppColors.primary),
              minimumSize: const Size.fromHeight(48),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(100)),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(
              horizontal: 20, vertical: 8),
          child: Row(
            children: [
              const Icon(Icons.info_outline_rounded,
                  size: 14, color: AppColors.textSecondary),
              const SizedBox(width: 6),
              Text(
                'Карт удаан дарж устгах боломжтой',
                style: AppTextStyles.bodySmall.copyWith(
                    fontSize: 11),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

// kart visual widget
class _CardWidget extends StatelessWidget {
  final WalletCard card;

  const _CardWidget({required this.card});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 8),
      padding: const EdgeInsets.all(22),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.splashStart, AppColors.splashEnd],
        ),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: AppColors.primary.withOpacity(0.25),
            blurRadius: 16,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                card.bankName.toUpperCase(),
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w700,
                  color: Colors.white,
                  letterSpacing: 1.2,
                ),
              ),
              const Icon(Icons.credit_card_rounded,
                  color: Colors.white, size: 26),
            ],
          ),
          const Spacer(),
          Text(
            card.maskedNumber,
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w500,
              color: Colors.white,
              letterSpacing: 2,
            ),
          ),
          const SizedBox(height: 14),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('CARD HOLDER',
                      style: TextStyle(
                          fontSize: 8,
                          color: Colors.white.withOpacity(0.6))),
                  Text(
                    card.holderName.toUpperCase(),
                    style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w500,
                        color: Colors.white),
                  ),
                ],
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text('EXPIRES',
                      style: TextStyle(
                          fontSize: 8,
                          color: Colors.white.withOpacity(0.6))),
                  Text(
                    card.expiryDate,
                    style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w500,
                        color: Colors.white),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }
}

// hoosон wallet state
class _EmptyWallet extends StatelessWidget {
  final VoidCallback onAddCard;

  const _EmptyWallet({required this.onAddCard});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 40),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: 80, height: 80,
              decoration: const BoxDecoration(
                color: AppColors.primaryLight,
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.account_balance_wallet_rounded,
                color: AppColors.primary, size: 36,
              ),
            ),
            const SizedBox(height: 20),
            Text('Хэтэвч хоосон байна',
                style: AppTextStyles.h3,
                textAlign: TextAlign.center),
            const SizedBox(height: 8),
            Text(
              'Эхний картаа холбоод банкны гүйлгээг автоматаар хянаж эхлээрэй.',
              style: AppTextStyles.bodyMedium.copyWith(
                  color: AppColors.textSecondary, height: 1.6),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 28),
            AppButton(
              label: '+ Карт холбох',
              onPressed: onAddCard,
            ),
          ],
        ),
      ),
    );
  }
}