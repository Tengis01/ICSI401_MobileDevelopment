// CurrencyConverterFragment.kt
package com.example.examcurrencychange  // ← root package!

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.examcurrencychange.data.CurrencyData               // ← засах!
import com.example.examcurrencychange.data.SharedPrefHelper           // ← засах!
import com.example.examcurrencychange.databinding.FragmentCurrencyConverterBinding // ← засах!
import java.text.NumberFormat
import java.util.Locale

class CurrencyConverterFragment : Fragment() {

    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefHelper: SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefHelper = SharedPrefHelper(requireContext())

        val currencyCode = arguments?.getString("currency_code")
            ?: prefHelper.getLastCurrency()

        setupUI(currencyCode)
        setupButtons(currencyCode)
    }

    private fun setupUI(currencyCode: String) {
        val currency = CurrencyData.findByCode(currencyCode) ?: return
        binding.tvFlagLarge.text   = currency.flag
        binding.tvCurrencyCode.text = currency.code
        binding.etAmount.setText(prefHelper.getLastAmount())
        prefHelper.saveLastCurrency(currencyCode)
    }

    private fun setupButtons(currencyCode: String) {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnConvert.setOnClickListener {
            convertCurrency(currencyCode)
        }
    }

    private fun convertCurrency(currencyCode: String) {
        val amountText = binding.etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Дүн оруулна уу!", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Зөв тоо оруулна уу!", Toast.LENGTH_SHORT).show()
            return
        }
        val currency = CurrencyData.findByCode(currencyCode) ?: return
        val resultMnt = amount * currency.rateToMnt

        // ⚠️ .toLong() нэмж overload ambiguity засна!
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 0
        binding.tvResult.text = formatter.format(resultMnt.toLong())

        prefHelper.saveLastAmount(amountText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}