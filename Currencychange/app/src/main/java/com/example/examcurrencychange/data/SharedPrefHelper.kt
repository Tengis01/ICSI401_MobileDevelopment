// data/SharedPrefHelper.kt
package com.example.examcurrencychange.data  // ← засах!

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LAST_CURRENCY = "last_currency_code"
        const val KEY_LAST_AMOUNT   = "last_amount"
    }

    fun saveLastCurrency(code: String) {
        prefs.edit().putString(KEY_LAST_CURRENCY, code).apply()
    }
    fun getLastCurrency(): String = prefs.getString(KEY_LAST_CURRENCY, "EUR") ?: "EUR"

    fun saveLastAmount(amount: String) {
        prefs.edit().putString(KEY_LAST_AMOUNT, amount).apply()
    }
    fun getLastAmount(): String = prefs.getString(KEY_LAST_AMOUNT, "1") ?: "1"
}