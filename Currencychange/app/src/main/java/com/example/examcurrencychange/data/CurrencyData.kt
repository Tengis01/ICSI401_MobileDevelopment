// data/CurrencyData.kt
package com.example.examcurrencychange.data  // ← examcurrencychange байх ёстой!

object CurrencyData {
    val currencies = listOf(
        Currency("EUR", "Евро",            "🇪🇺", 3726.0),
        Currency("GBP", "Английн фунт",    "🇬🇧", 4358.0),
        Currency("RUB", "Оросын рубль",    "🇷🇺", 11.5),
        Currency("CNY", "Хятадын юань",    "🇨🇳", 470.0),
        Currency("JPY", "Японы иен",       "🇯🇵", 23.0),
        Currency("KRW", "БНСУ-ын вон",     "🇰🇷", 2.5),
        Currency("AUD", "Австрали доллар", "🇦🇺", 2180.0),
        Currency("CHF", "Швейцарь франк",  "🇨🇭", 3850.0),
        Currency("CAD", "Канад доллар",    "🇨🇦", 2450.0),
        Currency("SGD", "Сингапур доллар", "🇸🇬", 2530.0),
        Currency("SEK", "Швед крон",       "🇸🇪", 308.0),
        Currency("TRY", "Туркийн Лир",     "🇹🇷", 98.0),
        Currency("HKD", "Гонконг доллар",  "🇭🇰", 436.0)
    )

    fun findByCode(code: String): Currency? = currencies.find { it.code == code }
}