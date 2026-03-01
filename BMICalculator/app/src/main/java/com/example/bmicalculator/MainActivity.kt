package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var isMaleSelected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardMale = findViewById<MaterialCardView>(R.id.cardMale)
        val cardFemale = findViewById<MaterialCardView>(R.id.cardFemale)

        val npHeight = findViewById<NumberPicker>(R.id.npHeight)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val btnCalculate = findViewById<MaterialButton>(R.id.btnCalculate)
        val tvHeightInfo = findViewById<TextView>(R.id.tvHeightInfo)

        // ondor songoh setup
        val min = 120
        val max = 220

        npHeight.minValue = 0
        npHeight.maxValue = max - min
        npHeight.wrapSelectorWheel = true

        val reversed = (min..max).map { it.toString() }.reversed().toTypedArray()
        npHeight.displayedValues = reversed

        // default 165 болгоё
        npHeight.value = max - 165

        tvHeightInfo.text = "Таны өндөр: ${max - npHeight.value}см"
        npHeight.setOnValueChangedListener { _, _, _ ->
            tvHeightInfo.text = "Таны өндөр: ${max - npHeight.value}см"
        }

        // gender toggle
        fun renderGender(){
            if (isMaleSelected){
                cardMale.strokeWidth = dp(2)
                cardMale.strokeColor = getColor(R.color.c_primary)
                cardMale.setCardBackgroundColor(getColor(R.color.c_selected_tint))

                cardFemale.strokeWidth = dp(1)
                cardFemale.strokeColor = getColor(R.color.c_border)
                cardFemale.setCardBackgroundColor(getColor(R.color.c_surface))
            } else {
                cardFemale.strokeWidth = dp(2)
                cardFemale.strokeColor = getColor(R.color.c_primary)
                cardFemale.setCardBackgroundColor(getColor(R.color.c_selected_tint))

                cardMale.strokeWidth = dp(1)
                cardMale.strokeColor = getColor(R.color.c_border)
                cardMale.setCardBackgroundColor(getColor(R.color.c_surface))
            }
        }

        cardMale.setOnClickListener{
            isMaleSelected = true
            renderGender()
        }
        cardFemale.setOnClickListener{
            isMaleSelected = false
            renderGender()
        }

        if (intent.getBooleanExtra("reset", false)) {
            // default болгох
            isMaleSelected = true
            renderGender()
            npHeight.value = max - 165
            tvHeightInfo.text = "Таны өндөр: 165см"
            etAge.setText("")
            etWeight.setText("")
        }

        renderGender()

        // calculate button
        btnCalculate.setOnClickListener{
            val heightCm = (max - npHeight.value).toDouble()

            val weightKg = etWeight.text.toString().trim().replace(',', '.').toDoubleOrNull()
            if (weightKg == null || weightKg <= 0){
                etWeight.error = "Жингээ зөв оруул"
                etWeight.requestFocus()
                return@setOnClickListener
            }

            val age = etAge.text.toString().trim().toIntOrNull() ?: 0

            val heightM = heightCm / 100
            val bmi = weightKg / (heightM * heightM)

            val (category, message) = classifyBmiMn(bmi)
            val gender = if (isMaleSelected) "Эрэгтэй" else "Эмэгтэй"
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("bmi", bmi)
                putExtra("category", category)
                putExtra("message", message)
                putExtra("gender", gender)
                putExtra("heightCm", heightCm)
                putExtra("weightKg", weightKg)
                putExtra("age", age)
            }
            startActivity(intent)

//            findViewById<TextView>(R.id.tvTitle).text = String.format(Locale.US, "BMI: %.1f (%s), bmi, categoty")
        }
    }
    private fun classifyBmiMn(bmi: Double): Pair<String, String> = when {
        bmi < 18.5 -> "Жингийн дутагдалтай" to "Илүү тэжээллэг хоол, зөв дэглэм баримтал."
        bmi < 25.0 -> "Хэвийн" to "Сайн байна. Одоогийн хэв маягаа хадгал."
        bmi < 30.0 -> "Илүүдэл жинтэй" to "Хөдөлгөөн нэм, илчлэгээ бага зэрэг бууруул."
        else -> "Таргалалттай" to "Эмч/мэргэжилтнээс зөвлөгөө авахыг бодоорой."
    }
    
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

}