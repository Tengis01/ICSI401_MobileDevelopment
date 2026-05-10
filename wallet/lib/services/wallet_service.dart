import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:uuid/uuid.dart';
import '../models/wallet_model.dart';

class WalletService {
  WalletService._();
  static final WalletService instance = WalletService._();

  static const _key = 'saved_cards';

  // buh kart avah
  Future<List<WalletCard>> getCards() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStr = prefs.getString(_key);
    if (jsonStr == null) return _defaultCards();

    final list = jsonDecode(jsonStr) as List;
    return list
        .map((e) => WalletCard.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  // shine kart nemeh
  Future<void> addCard(WalletCard card) async {
    final cards = await getCards();
    cards.add(card);
    await _save(cards);
  }

  // kart ustgah
  Future<void> deleteCard(String cardId) async {
    final cards = await getCards();
    cards.removeWhere((c) => c.id == cardId);
    await _save(cards);
  }

  Future<void> _save(List<WalletCard> cards) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(
      _key,
      jsonEncode(cards.map((c) => c.toJson()).toList()),
    );
  }

  // anhnii default kartuud - hоосоn wallet-d haruulah
  List<WalletCard> _defaultCards() => [];

  // shine kart-iin ID uusgeh
  String generateId() => const Uuid().v4().substring(0, 8);
}