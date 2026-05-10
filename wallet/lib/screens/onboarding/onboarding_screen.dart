import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../core/constants/app_constants.dart';
import 'widgets/onboarding_page.dart';

class OnboardingScreen extends StatefulWidget {
  const OnboardingScreen({super.key});

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  final List<OnboardingPageData> _pages = const [
    OnboardingPageData(
      icon: Icons.trending_up_rounded,
      title: 'Орлого, зарлагаа\nухаалгаар хяна',
      subtitle: 'Бүх шилжүүлгээ нэг дороос харж,\nтөсвөө хялбархан удирд.',
    ),
    OnboardingPageData(
      icon: Icons.account_balance_wallet_rounded,
      title: 'Бүх данс, картаа\nнэг дор',
      subtitle: 'Банкны данс, карт, цахим хэтэвчээ\nхолбоод бүртгэлээ автоматжуул.',
    ),
    OnboardingPageData(
      icon: Icons.bar_chart_rounded,
      title: 'Дэлгэрэнгүй\nстатистик, тайлан',
      subtitle: 'Зарлагынхаа чиглэлийг ойлгож,\nилүү ухаалаг шийдвэр гар.',
    ),
  ];

  Future<void> _finishOnboarding() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(AppConstants.onboardingSeenKey, true);
    if (!mounted) return;
    context.go(AppRoutes.register);
  }

  void _goNextPage() {
    if (_currentPage == _pages.length - 1) {
      _finishOnboarding();
    } else {
      _pageController.nextPage(
        duration: AppConstants.normalDuration,
        curve: Curves.easeInOut,
      );
    }
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isLastPage = _currentPage == _pages.length - 1;
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            Align(
              alignment: Alignment.topRight,
              child: TextButton(
                onPressed: _finishOnboarding,
                child: Text('Алгасах',
                    style: AppTextStyles.labelMedium.copyWith(
                        color: AppColors.textSecondary)),
              ),
            ),
            Expanded(
              child: PageView.builder(
                controller: _pageController,
                itemCount: _pages.length,
                onPageChanged: (index) => setState(() => _currentPage = index),
                itemBuilder: (context, index) =>
                    OnboardingPage(data: _pages[index]),
              ),
            ),
            const SizedBox(height: 16),
            // dots indicator
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(_pages.length, (index) {
                return AnimatedContainer(
                  duration: AppConstants.shortDuration,
                  margin: const EdgeInsets.symmetric(horizontal: 4),
                  width: _currentPage == index ? 24 : 8,
                  height: 8,
                  decoration: BoxDecoration(
                    color: _currentPage == index
                        ? AppColors.primary
                        : AppColors.border,
                    borderRadius: BorderRadius.circular(4),
                  ),
                );
              }),
            ),
            const SizedBox(height: 32),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: ElevatedButton(
                onPressed: _goNextPage,
                child: Text(isLastPage ? 'Эхлэх' : 'Үргэлжлүүлэх →'),
              ),
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}