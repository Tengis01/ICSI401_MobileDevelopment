// data/Currency.kt
package com.example.examcurrencychange.data

// ⚠️ параметрүүд нь constructor-т байх ёстой, body-д биш!
data class Currency(
    val code: String,
    val nameMn: String,
    val flag: String,
    val rateToMnt: Double
)