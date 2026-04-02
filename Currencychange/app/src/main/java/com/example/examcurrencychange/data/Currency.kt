// data/Currency.kt
package com.example.examcurrencychange.data

// ⚠️ constructor
data class Currency(
    val code: String,
    val nameMn: String,
    val flag: String,
    val rateToMnt: Double
)