package com.example.unitconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlin.math.round

class ConverterActivity : AppCompatActivity() { // ConverterActivity: 2-dahi delgets, converter hiih UI + logic end baidag

    private lateinit var spinnerFrom: Spinner // "from" unit songoh Spinner; lateinit = onCreate dotor findViewById hiij utga onoono
    private lateinit var spinnerTo: Spinner // "to" unit songoh Spinner; lateinit = daraa ni onCreate dotor utga onoono
    private lateinit var inputEditText: EditText // oruulah utga bichih EditText
    private lateinit var outputEditText: EditText // gargah utga haruulah EditText (end setText hiij bna)
    private lateinit var resultTextView: TextView // "Үр дүн: ..." gesen neg mor text haruulah TextView
    private lateinit var convertButton: Button // "Хөрвүүлэх" button

    private var category: Category = Category.LENGTH // odoogiin category; default ni LENGTH (Activity ehlehdee utgatai baih yostoi tul var + default)
    private var units: List<UnitItem> = emptyList() // odoogiin category-d harah unit-uudiin list; ehendee hooson list

    override fun onCreate(savedInstanceState: Bundle?) { // Activity lifecycle-iin onCreate; delgets shineer uusheh uyd duudagddana
        super.onCreate(savedInstanceState) // etseg onCreate-g ajilluulna
        setContentView(R.layout.activity_converter) // activity_converter.xml layout-g holboj delgets deer gargana

        val toolbar = findViewById<Toolbar>(R.id.toolbarConverter) // toolbar component-iig id-aar n olj local variable-d hiine
        setSupportActionBar(toolbar) // toolbar-iig ActionBar bolgoj ajilluulna (AppCompat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // back arrow (Up button) haruulna; ?. = null baih bolomjtoi
        toolbar.setNavigationOnClickListener { finish() } // back arrow darahad finish() -> ene Activity-g haaj butsna

        spinnerFrom = findViewById(R.id.spinnerFrom) // spinnerFrom lateinit var-d XML deerees view object-g onooh
        spinnerTo = findViewById(R.id.spinnerTo) // spinnerTo-d view object-g onooh
        inputEditText = findViewById(R.id.editInput) // input EditText-g onooh
        outputEditText = findViewById(R.id.editOutput) // output EditText-g onooh
        resultTextView = findViewById(R.id.textResult) // resultTextView-g onooh
        convertButton = findViewById(R.id.buttonConvert) // convertButton-g onooh

        val raw = intent.getStringExtra("CATEGORY") ?: "LENGTH" // Intent extra-s CATEGORY key-eer string avna; baihgui bol "LENGTH" default
        category = parseCategory(raw) // raw string-iig Category enum ruu hurvuulj category-d onooh

        supportActionBar?.title = categoryTitleMn(category) // toolbar title-g category-iin mongol nertei string-aar tavina

        units = unitsFor(category) // songogdson category-d taarsan unit-uudiin list avna
        setupSpinners(units) // spinnerFrom/spinnerTo 2-t list-iig adapter-aar holboj songolt haruulna

        convertButton.setOnClickListener { // button deer click event listener tavina
            doConvert() // click bolohod conversion logic duudna
        } // setOnClickListener tugsgul
    } // onCreate tugsgul

    private fun setupSpinners(unitItems: List<UnitItem>) { // spinners setup hiih function; unitItems = spinner dotor haruulah list
        val labels = unitItems.map { it.label } // UnitItem list-iig label-uudruu map hiij String list bolgoj bna (Spinner haruulah text)

        val adapter = ArrayAdapter(this, R.layout.spinner_item, labels) // ArrayAdapter: spinner-d zoriulsan adapter; this = context; spinner_item = item layout; labels = data
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // dropdown deer haragdah item layout-g tohiruulna

        spinnerFrom.adapter = adapter // spinnerFrom-d adapter set hiij data holbono
        spinnerTo.adapter = adapter // spinnerTo-d bas adil adapter set hiij data holbono

        spinnerFrom.setSelection(0) // spinnerFrom default songoltoor ehnii element (index 0)-g songono
        spinnerTo.setSelection(if (labels.size > 1) 1 else 0) // spinnerTo default: 2-oos deesh element baival index 1, ugui bol 0
    } // setupSpinners tugsgul

    private fun doConvert() { // user input + selected units unshaad result tootsooldog function
        val inputStr = inputEditText.text.toString().trim() // EditText text-g String bolgoj avaad trim hiine
        if (inputStr.isEmpty()) { // hooson bol
            toast(getString(R.string.enter_value)) // "utga oruul" gesen message toast-aar haruulna
            return // цааш үргэлжлүүлэхгүй
        } // if tugsgul

        val inputValue = inputStr.toDoubleOrNull() // string-iig Double bolgoj oroldono; bolohgui bol null
        if (inputValue == null) { // parse amjiltgui bol
            toast(getString(R.string.invalid_input)) // "buruu input" gesen message
            return // цааш үргэлжлүүлэхгүй
        } // if tugsgul

        val fromIndex = spinnerFrom.selectedItemPosition // spinnerFrom deer songogdson item-iin index
        val toIndex = spinnerTo.selectedItemPosition // spinnerTo deer songogdson item-iin index

        val from = units.getOrNull(fromIndex) // units list-ees fromIndex deerh UnitItem-g avna; index buruu bol null
        val to = units.getOrNull(toIndex) // units list-ees toIndex deerh UnitItem-g avna; index buruu bol null

        if (from == null || to == null) { // ali neg ni null bol
            toast(getString(R.string.conversion_error)) // conversion error message
            return // цааш үргэлжлүүлэхгүй
        } // if tugsgul

        val result = try { // try-catch: convert dotor exception garsan ch app unahgui
            convert(category, inputValue, from, to) // conversion core function duudna
        } catch (e: Exception) { // aldaa garval
            null // result-g null bolgono
        } // try-catch tugsgul

        if (result == null) { // result null bol
            toast(getString(R.string.conversion_error)) // error message
            return // цааш үргэлжлүүлэхгүй
        } // if tugsgul

        val pretty = formatNumber(result) // result too-g format hiij (6 oron) goy haruulah string bolgono
        outputEditText.setText(pretty) // output EditText dotor result bichne

        val line = getString( // UI deer "Үр дүн: A unit = B unit" format-aar message uusgene
            R.string.result_format, // strings.xml dotorh template: "Үр дүн: %1$s %2$s = %3$s %4$s"
            formatNumber(inputValue), // input too-g format hiij
            from.label, // from unit label
            pretty, // result formatted
            to.label // to unit label
        ) // getString tugsgul
        resultTextView.text = line // resultTextView deer line-g tavina
    } // doConvert tugsgul

    private fun convert(cat: Category, value: Double, from: UnitItem, to: UnitItem): Double { // convert core: cat + value + from/to unit аваад Double result butsaana
        return when (cat) { // when: switch shig; cat-iin torloosoos hamaarna
            Category.TEMPERATURE -> convertTemperature(value, from.key, to.key) // temperatur bol factor ashiglahgui; special formula ashiglaj convertTemperature duudna
            else -> { // бусад бүх category (urt/talbai/jin/hugatsaa/hurd) bol "base factor" arga
                val base = value * from.factor // base unit ruu shiljine: value * from.factor (factor ni base unit ruu hurvuuleh koeff)
                base / to.factor // base-aas target unit ruu: base / to.factor
            } // else block tugsgul
        } // when tugsgul
    } // convert tugsgul

    private fun convertTemperature(v: Double, fromKey: String, toKey: String): Double { // temperatur convert: v = input; fromKey/toKey = "C","F","K"
        if (fromKey == toKey) return v // neg negjuu bol shuud adil utgaa butsaanа

        val celsius = when (fromKey) { // ehleed bugdiig Celsius ruu shiljuulj baina
            "C" -> v // C bol shuud
            "F" -> (v - 32.0) * 5.0 / 9.0 // F -> C formula
            "K" -> v - 273.15 // K -> C formula
            else -> v // tanigdahgui key irvel unchanged
        } // when (fromKey) tugsgul

        return when (toKey) { // daraa нь Celsius-aas target ruu shiljuulne
            "C" -> celsius // C bol shuud
            "F" -> celsius * 9.0 / 5.0 + 32.0 // C -> F formula
            "K" -> celsius + 273.15 // C -> K formula
            else -> celsius // tanigdahgui key irvel Celsius butsaana
        } // when (toKey) tugsgul
    } // convertTemperature tugsgul

    private fun formatNumber(x: Double): String { // too-g goy haruulah function (6 oron)
        val rounded = round(x * 1_000_000.0) / 1_000_000.0 // round(): 6 oron hurtel dukhuulj baina (1,000,000-aar urjuuleed buцаa huvaana)
        return if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString() // хэрвээ бүхэл тоо бол .0-г avaad Long bolgoj string; ugui bol normal string
    } // formatNumber tugsgul

    private fun toast(msg: String) { // toast haruulah helper function
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() // Android Toast: this=context; msg=text; show()=haruulah
    } // toast tugsgul

    private fun parseCategory(raw: String): Category { // intent extra-s irsen raw string-iig Category enum ruu hurvuuleh function
        val x = raw.trim().lowercase() // raw string-iig tseverleed jijig bolgoj compare hiih belen bolgono

        return when (x) { // x utgaas hamaarna
            "length", "urt", "урт", "l" -> Category.LENGTH // ali ch helber irsen LENGTH bolgono
            "area", "talbai", "талбай", "a" -> Category.AREA // AREA
            "mass", "jin", "жин", "m" -> Category.MASS // MASS
            "time", "hugatsaa", "хугацаа", "t" -> Category.TIME // TIME
            "speed", "hurd", "хурд", "s" -> Category.SPEED // SPEED
            "temperature", "temperatur", "температур", "temp" -> Category.TEMPERATURE // TEMPERATURE
            "length" -> Category.LENGTH // (давхардсан) "length" дахин байгаагийн утга: илүүдэл, гэхдээ ажиллахад саадгүй
            else -> { // дээрхтэй таарахгүй бол
                when (raw.uppercase()) { // raw-г uppercase болгоод шалгана (шинэ хэлбэрээр "LENGTH" гэх мэтээр ирж болно)
                    "LENGTH" -> Category.LENGTH
                    "AREA" -> Category.AREA
                    "MASS" -> Category.MASS
                    "TIME" -> Category.TIME
                    "SPEED" -> Category.SPEED
                    "TEMPERATURE" -> Category.TEMPERATURE
                    else -> Category.LENGTH // юу ч таарахгүй бол default LENGTH
                } // inner when tugsgul
            } // else block tugsgul
        } // outer when tugsgul
    } // parseCategory tugsgul

    private fun categoryTitleMn(cat: Category): String { // toolbar title haruulahad Category -> string resource avah function
        return when (cat) { // cat-iin torloor
            Category.LENGTH -> getString(R.string.urt) // strings.xml dotorh urt
            Category.AREA -> getString(R.string.talbai) // talbai
            Category.MASS -> getString(R.string.jin) // jin
            Category.TIME -> getString(R.string.hugatsaa) // hugatsaa
            Category.SPEED -> getString(R.string.hurd) // hurd
            Category.TEMPERATURE -> getString(R.string.temperatur) // temperatur
        } // when tugsgul
    } // categoryTitleMn tugsgul

    private fun unitsFor(cat: Category): List<UnitItem> { // Category -> UnitItem list butsaah function (spinner dotor haragdah data endees irne)
        return when (cat) { // category-iin torloor unit list songono

            Category.LENGTH -> listOf( // LENGTH units; base = meter (m)
                UnitItem("km", "Километр", 1000.0), // 1 km = 1000 m
                UnitItem("m", "Метр", 1.0), // 1 m = 1 m (base)
                UnitItem("cm", "Сантиметр", 0.01), // 1 cm = 0.01 m
                UnitItem("mm", "Миллиметр", 0.001), // 1 mm = 0.001 m
                UnitItem("ft", "Фут", 0.3048), // 1 ft = 0.3048 m
                UnitItem("in", "Инч", 0.0254) // 1 in = 0.0254 m
            ) // LENGTH list tugsgul

            Category.AREA -> listOf( // AREA units; base = square meter (m2)
                UnitItem("m2", "м²", 1.0), // base
                UnitItem("km2", "км²", 1_000_000.0), // 1 km2 = 1,000,000 m2
                UnitItem("cm2", "см²", 0.0001), // 1 cm2 = 0.0001 m2
                UnitItem("ft2", "фут²", 0.09290304), // 1 ft2 = 0.09290304 m2
                UnitItem("in2", "инч²", 0.00064516), // 1 in2 = 0.00064516 m2
                UnitItem("ga", "Га", 10_000.0), // 1 га = 10,000 m2
                UnitItem("acre", "Акр", 4046.8564224) // 1 acre = 4046.8564224 m2
            ) // AREA list tugsgul

            Category.MASS -> listOf( // MASS units; base = kilogram (kg)
                UnitItem("ton", "Тонн", 1000.0), // 1 ton = 1000 kg
                UnitItem("kg", "Килограмм", 1.0), // base
                UnitItem("g", "Грамм", 0.001), // 1 g = 0.001 kg
                UnitItem("mg", "Миллиграмм", 0.000001), // 1 mg = 0.000001 kg
                UnitItem("lb", "Фунт", 0.45359237) // 1 lb = 0.45359237 kg
            ) // MASS list tugsgul

            Category.TIME -> listOf( // TIME units; base = second (s)
                UnitItem("s", "Секунд", 1.0), // base
                UnitItem("min", "Минут", 60.0), // 1 min = 60 s
                UnitItem("h", "Цаг", 3600.0), // 1 h = 3600 s
                UnitItem("day", "Өдөр", 86400.0), // 1 day = 86400 s
                UnitItem("week", "7 хоног", 604800.0), // 1 week = 604800 s
                UnitItem("month", "Сар", 2_592_000.0), // 1 month = 30 day gej toiotsooj bna (approx)
                UnitItem("year", "Жил", 31_536_000.0) // 1 year = 365 day gej toiotsooj bna
            ) // TIME list tugsgul

            Category.SPEED -> listOf( // SPEED units; base = meter per second (m/s)
                UnitItem("mps", "м/с", 1.0), // base
                UnitItem("kmh", "км/ц", 0.2777777777777778), // 1 km/h = 0.277777... m/s

                UnitItem("mph_mongol", "м/ц", 0.0002777777777777778), // "m/hour" (метр/цаг) = 1/3600 m/s
                UnitItem("mpm", "м/мин", 0.016666666666666666), // "m/min" = 1/60 m/s

                UnitItem("kmps", "км/с", 1000.0), // 1 km/s = 1000 m/s
                UnitItem("kmpm", "км/мин", 16.666666666666668) // 1 km/min = 1000/60 m/s
            ) // SPEED list tugsgul

            Category.TEMPERATURE -> listOf( // TEMPERATURE units; factor энд ашиглахгүй (special formula)
                UnitItem("C", "Цельс", 0.0), // C
                UnitItem("F", "Фаренгейт", 0.0), // F
                UnitItem("K", "Кельвин", 0.0) // K
            ) // TEMPERATURE list tugsgul
        } // when tugsgul
    } // unitsFor tugsgul

    enum class Category { LENGTH, AREA, MASS, TIME, SPEED, TEMPERATURE } // enum class: fixed torluudiin set; Category ni 6 utgatai

    data class UnitItem( // data class: unit-iin data hadgalah object; equals/toString/copy avtomataar
        val key: String, // key: convertTemperature deer ashiglah, bas internal id shig ("km","m","C"...)
        val label: String, // label: Spinner deer haragdah text ("Километр"...)
        val factor: Double // factor: base ruu hurvuuleh koeff; temperature deer ashiglahgui
    ) // UnitItem data class tugsgul
} // ConverterActivity class tugsgul