// data/CurrencyData.kt
package com.example.examcurrencychange.data

object CurrencyData {
    val currencies = listOf(
        Currency("EUR", "Евро",            "🇪🇺", 4118.0),
        Currency("GBP", "Английн фунт",    "🇬🇧", 4725.0),
        Currency("RUB", "Оросын рубль",    "🇷🇺", 44.0),
        Currency("CNY", "Хятадын юань",    "🇨🇳", 518.0),
        Currency("JPY", "Японы иен",       "🇯🇵", 23.0),
        Currency("KRW", "БНСУ-ын вон",     "🇰🇷", 2.5),
        Currency("AUD", "Австрали доллар", "🇦🇺", 2455.0),
        Currency("CHF", "Швейцарь франк",  "🇨🇭", 4472.0),
        Currency("CAD", "Канад доллар",    "🇨🇦", 2565.0),
        Currency("SGD", "Сингапур доллар", "🇸🇬", 2773.0),
        Currency("SEK", "Швед крон",       "🇸🇪", 376.0),
        Currency("TRY", "Туркийн Лир",     "🇹🇷", 80.0),
        Currency("HKD", "Гонконг доллар",  "🇭🇰", 455.0)
    )

    fun findByCode(code: String): Currency? = currencies.find { it.code == code }
}