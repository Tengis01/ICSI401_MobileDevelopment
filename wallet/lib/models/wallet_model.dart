class WalletCard {
  final String id;
  final String bankName;
  final String cardNumber;  // buren dugaar - display-d mask hiine
  final String expiryDate;  // MM/YY
  final String holderName;
  final int balance;        // simulation balance

  const WalletCard({
    required this.id,
    required this.bankName,
    required this.cardNumber,
    required this.expiryDate,
    required this.holderName,
    required this.balance,
  });

  // **** **** **** 4521 format
  String get maskedNumber {
    final last4 = cardNumber.length >= 4
        ? cardNumber.substring(cardNumber.length - 4)
        : cardNumber;
    return '**** **** **** $last4';
  }

  String get last4 => cardNumber.length >= 4
      ? cardNumber.substring(cardNumber.length - 4)
      : cardNumber;

  Map<String, dynamic> toJson() => {
    'id':          id,
    'bankName':    bankName,
    'cardNumber':  cardNumber,
    'expiryDate':  expiryDate,
    'holderName':  holderName,
    'balance':     balance,
  };

  factory WalletCard.fromJson(Map<String, dynamic> json) => WalletCard(
    id:          json['id'] as String,
    bankName:    json['bankName'] as String,
    cardNumber:  json['cardNumber'] as String,
    expiryDate:  json['expiryDate'] as String,
    holderName:  json['holderName'] as String,
    balance:     json['balance'] as int,
  );
}