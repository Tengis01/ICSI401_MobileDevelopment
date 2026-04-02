package com.example.examcurrencychange

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.examcurrencychange.data.CurrencyData
import com.example.examcurrencychange.data.SharedPrefHelper
import com.example.examcurrencychange.databinding.FragmentCurrencyConverterBinding
import java.text.NumberFormat
import java.util.Locale

class CurrencyConverterFragment : Fragment() {

    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefHelper: SharedPrefHelper

    // odoogiin songogdson valyutiin kod
    private var currentCurrencyCode: String = "EUR"

    // swap state - false = EUR→MNT, true = MNT→EUR
    private var isSwapped: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefHelper = SharedPrefHelper(requireContext())

        // bundle esvel sharedprefs-aas avna
        currentCurrencyCode = arguments?.getString("currency_code")
            ?: prefHelper.getLastCurrency()

        setupUI(currentCurrencyCode)
        setupButtons()
    }

    private fun setupUI(currencyCode: String) {
        val currency = CurrencyData.findByCode(currencyCode) ?: return
        currentCurrencyCode = currencyCode
        prefHelper.saveLastCurrency(currencyCode)

        // swap state hamaarch flag, ner haruulna
        updateDirectionUI()

        binding.etAmount.setText(prefHelper.getLastAmount())

        // valyut soligdohod result tseverlene
        binding.tvResult.text = "0"
        binding.tvRate.text = ""
    }

    // swap state-aas hamaarchuulj deed/dood flag, ner soliino
    private fun updateDirectionUI() {
        val currency = CurrencyData.findByCode(currentCurrencyCode) ?: return

        if (!isSwapped) {
            // deer: songogdson valyut, door: MNT
            binding.tvFlagLarge.text    = currency.flag
            binding.tvCurrencyCode.text = currency.code
            binding.tvFlagResult.text   = "🇲🇳"
            binding.tvCodeResult.text   = "MNT"
        } else {
            // deer: MNT, door: songogdson valyut
            binding.tvFlagLarge.text    = "🇲🇳"
            binding.tvCurrencyCode.text = "MNT"
            binding.tvFlagResult.text   = currency.flag
            binding.tvCodeResult.text   = currency.code
        }
    }

    private fun setupButtons() {
        // butsah tovch - jaguulalt ruu harih
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // dropdown - valyut songoh dialog neene
        binding.layoutCurrencySelector.setOnClickListener {
            showCurrencyPicker()
        }

        // swap - chiglel soliino, result tseverlene
        binding.btnSwap.setOnClickListener {
            isSwapped = !isSwapped
            updateDirectionUI()
            binding.tvResult.text = "0"
            binding.tvRate.text = ""
        }

        binding.btnConvert.setOnClickListener {
            convertCurrency()
        }

        // input deer apostrophe format hiine
        setupAmountFormatter()
    }

    // bichih burt 1000000 -> 1'000'000 bolgono
    private fun setupAmountFormatter() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {

            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // davhar duudagdahgui bolgono
                if (isFormatting) return
                isFormatting = true

                val input = s.toString()

                // tsever too bolgono
                val clean = input.replace("'", "")

                if (clean.isNotEmpty()) {
                    val number = clean.toLongOrNull()
                    if (number != null) {
                        // format hiij apostrophe tavina
                        val formatted = NumberFormat.getNumberInstance(Locale.US)
                            .format(number)
                            .replace(",", "'")

                        binding.etAmount.setText(formatted)
                        // cursor-g togsgoloo shine
                        binding.etAmount.setSelection(formatted.length)
                    }
                }

                isFormatting = false
            }
        })
    }

    private fun showCurrencyPicker() {
        val currencies = CurrencyData.currencies

        // dialog-d haruulah ner: "EUR - Евро" geh met
        val items = currencies.map { "${it.flag}  ${it.code} — ${it.nameMn}" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Валют сонгох")
            .setItems(items) { _, index ->
                setupUI(currencies[index].code)
            }
            .setNegativeButton("Болих", null)
            .show()
    }

    private fun convertCurrency() {
        // tsever too bolgono
        val amountText = binding.etAmount.text.toString().replace("'", "").trim()

        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Дүн оруулна уу!", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Зөв тоо оруулна уу!", Toast.LENGTH_SHORT).show()
            return
        }

        val currency = CurrencyData.findByCode(currentCurrencyCode) ?: return

        if (!isSwapped) {
            // EUR → MNT: dun * hans
            val resultMnt = amount * currency.rateToMnt
            binding.tvResult.text = formatWithApostrophe(resultMnt)
            binding.tvRate.text = "1 ${currency.code} = ${formatWithApostrophe(currency.rateToMnt)} ₮"
        } else {
            // MNT → EUR: dun / hans
            val resultForeign = amount / currency.rateToMnt
            binding.tvResult.text = formatWithApostrophe(resultForeign)
            binding.tvRate.text = "1 ₮ = ${formatWithApostrophe(1.0 / currency.rateToMnt)} ${currency.code}"
        }

        // apostrophe-guigeer hadgalna
        prefHelper.saveLastAmount(amountText)
    }

    // 3'000'000.50 geh met format hiine
    private fun formatWithApostrophe(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(value).replace(",", "'")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}