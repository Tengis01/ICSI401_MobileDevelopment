// MainActivity.kt
package com.example.examcurrencychange  // ← root package, R энд байна

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.examcurrencychange.databinding.ActivityMainBinding  // ← засах!

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CurrencyListFragment())
                .commit()
        }
    }

    fun openConverter(currencyCode: String) {
        val fragment = CurrencyConverterFragment().apply {
            arguments = Bundle().apply {
                putString("currency_code", currencyCode)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack("converter")
            .commit()
    }
}