import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../app/router.dart';
import '../../app/theme/app_colors.dart';
import '../../app/theme/app_text_styles.dart';
import '../../widgets/app_button.dart';
import 'widgets/auth_text_field.dart';

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _register() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _isLoading = true; _errorMessage = null; });

    try {
      final credential = await FirebaseAuth.instance
          .createUserWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );
      await credential.user?.updateDisplayName(_nameController.text.trim());
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
      case 'email-already-in-use':
        return 'Энэ и-мэйл хаяг аль хэдийн бүртгэлтэй байна.';
      case 'weak-password':
        return 'Нууц үг хэт сул байна. Хамгийн багадаа 8 тэмдэгт.';
      case 'invalid-email':
        return 'И-мэйл хаяг буруу байна.';
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
                Text('Бүртгэл үүсгэх', style: AppTextStyles.h2),
                const SizedBox(height: 8),
                Text('Санхүүгийн аяллаа эхлүүлцгээе',
                    style: AppTextStyles.bodyMedium.copyWith(
                        color: AppColors.textSecondary)),
                const SizedBox(height: 36),
                AuthTextField(
                  label: 'НЭР', hint: 'Бат-Эрдэнэ',
                  controller: _nameController,
                  prefixIcon: Icons.person_outline_rounded,
                  validator: (v) =>
                  (v == null || v.trim().isEmpty) ? 'Нэрээ оруулна уу' : null,
                ),
                const SizedBox(height: 20),
                AuthTextField(
                  label: 'И-МЭЙЛ', hint: 'bat@example.com',
                  controller: _emailController,
                  keyboardType: TextInputType.emailAddress,
                  prefixIcon: Icons.email_outlined,
                  validator: (v) {
                    if (v == null || v.trim().isEmpty) return 'И-мэйл хаягаа оруулна уу';
                    if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(v.trim())) {
                      return 'И-мэйл хаяг буруу байна';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 20),
                AuthTextField(
                  label: 'НУУЦ ҮГ', hint: 'Хамгийн багадаа 8 тэмдэгт',
                  controller: _passwordController,
                  isPassword: true,
                  prefixIcon: Icons.lock_outline_rounded,
                  validator: (v) {
                    if (v == null || v.isEmpty) return 'Нууц үгээ оруулна уу';
                    if (v.length < 8) return 'Хамгийн багадаа 8 тэмдэгт байх ёстой';
                    return null;
                  },
                ),
                if (_errorMessage != null) ...[
                  const SizedBox(height: 16),
                  _ErrorBanner(message: _errorMessage!),
                ],
                const SizedBox(height: 32),
                AppButton(
                  label: 'Бүртгүүлэх',
                  onPressed: _register,
                  isLoading: _isLoading,
                ),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text('Бүртгэлтэй юу? ',
                        style: AppTextStyles.bodyMedium.copyWith(
                            color: AppColors.textSecondary)),
                    GestureDetector(
                      onTap: () => context.go(AppRoutes.login),
                      child: Text('Нэвтрэх',
                          style: AppTextStyles.labelMedium.copyWith(
                              color: AppColors.primary)),
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
        border: Border.all(color: AppColors.error.withValues(alpha:0.3)),
      ),
      child: Row(
        children: [
          const Icon(Icons.error_outline_rounded,
              color: AppColors.error, size: 18),
          const SizedBox(width: 8),
          Expanded(
            child: Text(message,
                style: AppTextStyles.bodySmall.copyWith(
                    color: AppColors.error)),
          ),
        ],
      ),
    );
  }
}