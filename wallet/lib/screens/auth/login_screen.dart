import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../widgets/app_button.dart';
import 'widgets/auth_text_field.dart';

// register_screen.dart import БАЙХГҮЙ - _ErrorBanner доор тусдаа байна

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _login() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _isLoading = true; _errorMessage = null; });

    try {
      await FirebaseAuth.instance.signInWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );
      if (!mounted) return;
      context.go(AppRoutes.home);
    } on FirebaseAuthException catch (e) {
      setState(() => _errorMessage = _getError(e.code));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  String _getError(String code) {
    switch (code) {
      case 'user-not-found':
        return 'Энэ и-мэйл хаягтай бүртгэл олдсонгүй.';
      case 'wrong-password':
      case 'invalid-credential':
        return 'И-мэйл эсвэл нууц үг буруу байна.';
      case 'too-many-requests':
        return 'Хэт олон удаа оролдлоо. Түр хүлээгээд дахин оролдоно уу.';
      default:
        return 'Алдаа гарлаа. Дахин оролдоно уу.';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 16),
                IconButton(
                  onPressed: () => context.go(AppRoutes.onboarding),
                  icon: const Icon(Icons.arrow_back_ios_new_rounded, size: 20),
                  padding: EdgeInsets.zero,
                  constraints: const BoxConstraints(),
                ),
                const SizedBox(height: 32),
                Text('Тавтай морилно уу!', style: AppTextStyles.h2),
                const SizedBox(height: 8),
                Text(
                  'Дансандаа нэвтэрч орно уу',
                  style: AppTextStyles.bodyMedium.copyWith(
                      color: AppColors.textSecondary),
                ),
                const SizedBox(height: 36),
                AuthTextField(
                  label: 'И-МЭЙЛ',
                  hint: 'bat@example.com',
                  controller: _emailController,
                  keyboardType: TextInputType.emailAddress,
                  prefixIcon: Icons.email_outlined,
                  validator: (v) => (v == null || v.trim().isEmpty)
                      ? 'И-мэйл хаягаа оруулна уу'
                      : null,
                ),
                const SizedBox(height: 20),
                AuthTextField(
                  label: 'НУУЦ ҮГ',
                  hint: 'Нууц үгээ оруулна уу',
                  controller: _passwordController,
                  isPassword: true,
                  prefixIcon: Icons.lock_outline_rounded,
                  validator: (v) => (v == null || v.isEmpty)
                      ? 'Нууц үгээ оруулна уу'
                      : null,
                ),
                const SizedBox(height: 12),
                Align(
                  alignment: Alignment.centerRight,
                  child: Text(
                    'Нууц үгээ мартсан уу?',
                    style: AppTextStyles.labelMedium.copyWith(
                        color: AppColors.primary),
                  ),
                ),
                if (_errorMessage != null) ...[
                  const SizedBox(height: 16),
                  _ErrorBanner(message: _errorMessage!),
                ],
                const SizedBox(height: 32),
                AppButton(
                  label: 'Нэвтрэх',
                  onPressed: _login,
                  isLoading: _isLoading,
                ),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Бүртгэлгүй юу? ',
                      style: AppTextStyles.bodyMedium.copyWith(
                          color: AppColors.textSecondary),
                    ),
                    GestureDetector(
                      onTap: () => context.go(AppRoutes.register),
                      child: Text(
                        'Бүртгүүлэх',
                        style: AppTextStyles.labelMedium.copyWith(
                            color: AppColors.primary),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 32),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// login screen-d zoriulsan error banner
// register_screen.dart deer adilhan _ErrorBanner baina - private class
// tus tustaa baih ni zuv, import hiihgui
class _ErrorBanner extends StatelessWidget {
  final String message;
  const _ErrorBanner({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: AppColors.errorLight,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.error.withOpacity(0.3)),
      ),
      child: Row(
        children: [
          const Icon(Icons.error_outline_rounded,
              color: AppColors.error, size: 18),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              message,
              style: AppTextStyles.bodySmall.copyWith(color: AppColors.error),
            ),
          ),
        ],
      ),
    );
  }
}