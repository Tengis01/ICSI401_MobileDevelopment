import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../providers/auth_provider.dart';
import '../screens/splash/splash_screen.dart';
import '../screens/onboarding/onboarding_screen.dart';
import '../screens/auth/login_screen.dart';
import '../screens/auth/register_screen.dart';
import '../screens/home/home_screen.dart';
import '../screens/entry/entry_screen.dart';
import '../screens/transaction/transaction_detail_screen.dart';
import '../screens/stats/stats_screen.dart';
import '../screens/search/search_screen.dart';
import '../screens/profile/profile_screen.dart';
import '../screens/profile/profile_edit_screen.dart';
import '../screens/profile/notification_settings_screen.dart';
import '../screens/profile/language_screen.dart';
import '../screens/notifications/notification_screen.dart';
import '../screens/simulation/send_simulation_screen.dart';
import '../screens/simulation/receive_simulation_screen.dart';
import '../screens/recharge/recharge_amount_screen.dart';
import '../screens/recharge/recharge_bank_screen.dart';
import '../screens/recharge/recharge_success_screen.dart';
import '../screens/bills/bills_screen.dart';
import '../screens/bills/bill_detail_screen.dart';
import '../screens/bills/bill_method_screen.dart';
import '../screens/bills/bill_receipt_screen.dart';
import '../screens/bills/service_options_screen.dart';
import '../screens/bills/bill_processing_screen.dart';
import '../screens/bills/bill_success_screen.dart';
import '../screens/bills/bill_failure_screen.dart';
import '../screens/wallet/wallet_screen.dart';
import '../screens/wallet/add_card_screen.dart';

class AppRoutes {
  static const String splash = '/';
  static const String onboarding = '/onboarding';
  static const String login = '/login';
  static const String register = '/register';
  static const String home = '/home';
  static const String entry = '/entry';
  static const String transaction = '/transaction/:id';
  static const String stats = '/stats';
  static const String search = '/search';
  static const String profile = '/profile';
  static const String profileEdit = '/profile/edit';
  static const String notifications = '/notifications';
  static const String notificationSettings = '/profile/notifications';
  static const String language = '/profile/language';
  static const String send = '/send';
  static const String receive = '/receive';
  static const String recharge = '/recharge';
  static const String rechargeBank = '/recharge/bank';
  static const String rechargeSuccess = '/recharge/success';
  static const String bills = '/bills';
  static const String billDetail = '/bills/:serviceId';
  static const String billMethod = '/bills/:serviceId/method';
  static const String billReceipt = '/bills/:serviceId/receipt';
  static const String billProcessing = '/bills/:serviceId/processing';
  static const String billSuccess = '/bills/:serviceId/success';
  static const String billFailure = '/bills/:serviceId/failure';
  static const String wallet = '/wallet';
  static const String addCard = '/wallet/add-card';

  static String transactionPath(String id) => '/transaction/$id';
  static String billDetailPath(String id) => '/bills/$id';
  static String billMethodPath(String id) => '/bills/$id/method';
  static String billReceiptPath(String id) => '/bills/$id/receipt';
  static String billProcessingPath(String id) => '/bills/$id/processing';
  static String billSuccessPath(String id) => '/bills/$id/success';
  static String billFailurePath(String id) => '/bills/$id/failure';
}

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: AppRoutes.splash,
    redirect: (context, state) {
      if (authState.isLoading) return null;
      final isLoggedIn = authState.valueOrNull != null;
      final loc = state.matchedLocation;

      if (loc == AppRoutes.splash || loc == AppRoutes.onboarding) {
        return null;
      }

      if (isLoggedIn && (loc == AppRoutes.login || loc == AppRoutes.register)) {
        return AppRoutes.home;
      }
      if (!isLoggedIn && loc == AppRoutes.home) {
        return AppRoutes.login;
      }
      return null;
    },
    routes: [
      GoRoute(
        path: AppRoutes.splash,
        builder: (c, s) => const SplashScreen(),
      ),
      GoRoute(
        path: AppRoutes.onboarding,
        builder: (c, s) => const OnboardingScreen(),
      ),
      GoRoute(
        path: AppRoutes.login,
        builder: (c, s) => const LoginScreen(),
      ),
      GoRoute(
        path: AppRoutes.register,
        builder: (c, s) => const RegisterScreen(),
      ),
      GoRoute(
        path: AppRoutes.home,
        builder: (c, s) => const HomeScreen(),
      ),
      GoRoute(
        path: AppRoutes.entry,
        builder: (c, s) => const EntryScreen(),
      ),
      GoRoute(
        path: AppRoutes.transaction,
        builder: (c, s) => TransactionDetailScreen(
          transactionId: s.pathParameters['id']!,
        ),
      ),
      GoRoute(
        path: AppRoutes.stats,
        builder: (c, s) => const StatsScreen(),
      ),
      GoRoute(
        path: AppRoutes.search,
        builder: (c, s) => const SearchScreen(),
      ),
      GoRoute(
        path: AppRoutes.profile,
        builder: (c, s) => const ProfileScreen(),
      ),
      GoRoute(
        path: AppRoutes.profileEdit,
        builder: (c, s) => const ProfileEditScreen(),
      ),
      GoRoute(
        path: AppRoutes.notifications,
        builder: (c, s) => const NotificationScreen(),
      ),
      GoRoute(
        path: AppRoutes.notificationSettings,
        builder: (c, s) => const NotificationSettingsScreen(),
      ),
      GoRoute(
        path: AppRoutes.language,
        builder: (c, s) => const LanguageScreen(),
      ),
      GoRoute(
        path: AppRoutes.send,
        builder: (c, s) => const SendSimulationScreen(),
      ),
      GoRoute(
        path: AppRoutes.receive,
        builder: (c, s) => const ReceiveSimulationScreen(),
      ),
      GoRoute(
        path: AppRoutes.recharge,
        builder: (c, s) => const RechargeAmountScreen(),
      ),
      GoRoute(
        path: AppRoutes.rechargeBank,
        builder: (c, s) => RechargeBankScreen(
          amount: s.extra as int,
        ),
      ),
      GoRoute(
        path: AppRoutes.rechargeSuccess,
        builder: (c, s) {
          final extra = s.extra as Map<String, dynamic>;
          return RechargeSuccessScreen(
            amount: extra['amount'] as int,
            bankName: extra['bankName'] as String,
          );
        },
      ),
      GoRoute(
        path: AppRoutes.bills,
        builder: (c, s) => const BillsScreen(),
      ),
      GoRoute(
        path: AppRoutes.billDetail,
        builder: (c, s) => BillDetailScreen(
          serviceId: s.pathParameters['serviceId']!,
        ),
      ),
      GoRoute(
        path: '/bills/:serviceId/options',
        builder: (c, s) => ServiceOptionsScreen(
          serviceId: s.pathParameters['serviceId']!,
        ),
      ),
      GoRoute(
        path: AppRoutes.billMethod,
        builder: (c, s) => BillMethodScreen(
          serviceId: s.pathParameters['serviceId']!,
          amount: s.extra as int,
        ),
      ),
      GoRoute(
        path: AppRoutes.billReceipt,
        builder: (c, s) {
          final extra = s.extra as Map<String, dynamic>;
          return BillReceiptScreen(
            serviceId: s.pathParameters['serviceId']!,
            txId: extra['txId'] as String,
            amount: extra['amount'] as int,
            cardLast4: extra['cardLast4'] as String,
            bankName: extra['bankName'] as String,
          );
        },
      ),
      GoRoute(
        path: AppRoutes.billProcessing,
        builder: (c, s) {
          final extra = s.extra as Map<String, dynamic>;
          return BillProcessingScreen(
            serviceId: s.pathParameters['serviceId']!,
            amount: extra['amount'] as int,
            cardLast4: extra['cardLast4'] as String,
            bankName: extra['bankName'] as String,
          );
        },
      ),
      GoRoute(
        path: AppRoutes.billSuccess,
        builder: (c, s) {
          final extra = s.extra as Map<String, dynamic>;
          return BillSuccessScreen(
            serviceId: s.pathParameters['serviceId']!,
            txId: extra['txId'] as String,
            amount: extra['amount'] as int,
            cardLast4: extra['cardLast4'] as String,
            bankName: extra['bankName'] as String,
          );
        },
      ),
      GoRoute(
        path: AppRoutes.billFailure,
        builder: (c, s) {
          final error = s.extra as String;
          return BillFailureScreen(
            serviceId: s.pathParameters['serviceId']!,
            errorMessage: error,
          );
        },
      ),
      GoRoute(
        path: AppRoutes.wallet,
        builder: (c, s) => const WalletScreen(),
      ),
      GoRoute(
        path: AppRoutes.addCard,
        builder: (c, s) => const AddCardScreen(),
      ),
    ],
  );
});
