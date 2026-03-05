package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

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

        // Height picker (reversed display)
        val min = 120
        val max = 220

        npHeight.minValue = 0
        npHeight.maxValue = max - min
        npHeight.wrapSelectorWheel = true
        npHeight.displayedValues = (min..max).map { it.toString() }.reversed().toTypedArray()

        // default 165cm -> index
        npHeight.value = max - 165

        fun updateHeightLabel() {
            tvHeightInfo.text = "Таны өндөр: ${max - npHeight.value}см"
        }
        updateHeightLabel()
        npHeight.setOnValueChangedListener { _, _, _ -> updateHeightLabel() }

        // ===== Gender toggle UI =====
        fun renderGender() {
            if (isMaleSelected) {
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

        cardMale.setOnClickListener {
            isMaleSelected = true
            renderGender()
        }
        cardFemale.setOnClickListener {
            isMaleSelected = false
            renderGender()
        }
        renderGender()

        // Reset from Result
        if (intent.getBooleanExtra("reset", false)) {
            isMaleSelected = true
            renderGender()

            npHeight.value = max - 165
            updateHeightLabel()

            etAge.setText("")
            etWeight.setText("")
        }

        // ===== Calculate =====
        btnCalculate.setOnClickListener {
            // age validate
            val age = etAge.text.toString().trim().toIntOrNull()
            if (age == null || age <= 0) {
                etAge.error = "Насаа зөв оруул"
                etAge.requestFocus()
                return@setOnClickListener
            }
            if (age < 5) {
                etAge.error = "Энэ апп 5+ насанд"
                etAge.requestFocus()
                return@setOnClickListener
            }

            // weight validate
            val weightKg = etWeight.text.toString().trim().replace(',', '.').toDoubleOrNull()
            if (weightKg == null || weightKg <= 0) {
                etWeight.error = "Жингээ зөв оруул"
                etWeight.requestFocus()
                return@setOnClickListener
            }

            val heightCm = (max - npHeight.value).toDouble()
            val heightM = heightCm / 100.0
            val bmi = weightKg / (heightM * heightM)

            val genderText = if (isMaleSelected) "Эрэгтэй" else "Эмэгтэй"
            val (category, message) = classifyBmiMn(age, bmi, isMaleSelected)

            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("bmi", bmi)
                putExtra("category", category)
                putExtra("message", message)
                putExtra("gender", genderText)
                putExtra("heightCm", heightCm)
                putExtra("weightKg", weightKg)
                putExtra("age", age)
            }
            startActivity(intent)
        }
    }

    private fun classifyBmiMn(age: Int, bmi: Double, isMale: Boolean): Pair<String, String> {
        // 5–19: chart хэрэгтэй
        if (age in 5..19) {
            val msg = buildString {
                append("Та өсвөр насны ангилалд орж байна (5–19). ")
//                append("Энэ үед BMI-г нас + хүйсээр нь “BMI-for-age” графикаар үнэлдэг. ")
//                append("Одоогоор энэ апп тэр графикийг оруулаагүй тул зөвхөн BMI тоог харуулж байна.")
            }
            return "Өсвөр нас" to msg
        }

        // Adult (20+): category ижил, message нь gender-ээр жаахан өөр
        return when {
            bmi < 18.5 -> {
                val extra = if (isMale) {
                    " (Эрэгтэй) Булчин алдалтаас сэргийлж уураг + хүчний дасгал нэм."
                } else {
                    " (Эмэгтэй) Тэжээллэг хоол, тогтмол хооллолт бариад ядаргаа/сарын тэмдгийн өөрчлөлт байвал анхаар."
                }
                "Жингийн дутагдалтай" to ("Илүү тэжээллэг хоол, зөв дэглэм баримтал.$extra")
            }
            bmi < 25.0 -> {
                val extra = if (isMale) {
                    " (Эрэгтэй) Хөдөлгөөн, хүчний дасгалаа тогтмол хадгал."
                } else {
                    " (Эмэгтэй) Алхалт + core/strength хослуулаад хэв маягаа хадгал."
                }
                "Хэвийн" to ("Сайн байна. Одоогийн хэв маягаа хадгал.$extra")
            }
            bmi < 30.0 -> {
                val extra = if (isMale) {
                    " (Эрэгтэй) Гэдэс орчмын өөх хуримтлал ихсэх магадлалтай—алхалт + хүчний дасгал + унтлага."
                } else {
                    " (Эмэгтэй) Бэлхүүсээ (waist) хэмжиж хяна—кардио + strength 7 хоногт 3–4 удаа."
                }
                "Илүүдэл жинтэй" to ("Хөдөлгөөн нэм, илчлэгээ бага зэрэг бууруул.$extra")
            }
            else -> {
                val extra = if (isMale) {
                    " (Эрэгтэй) Даралт/чихрийн шижин эрсдэл өснө—шат дараатай бууруулж, хэрэгтэй бол эмчтэй зөвлөлд."
                } else {
                    " (Эмэгтэй) Дааврын нөлөөтэй жин нэмэлт байж болно—ерөнхий үзлэг/мэргэжилтэнтэй зөвлөлдөхийг бодоорой."
                }
                "Таргалалттай" to ("Эмч/мэргэжилтнээс зөвлөгөө авахыг бодоорой.$extra")
            }
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}