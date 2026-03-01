package com.example.unitconverter

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Locale

class MainActivity : AppCompatActivity() { // MainActivity class: ene bol main delgets (Activity) udirdah class, AppCompatActivity-gas ulamjilsan

    private data class CategoryCard( // private data class: zovhon ene MainActivity dotroo ashiglakh, data class ni data/utga hadgalahad zoriulsan (equals/toString/copy avtomataar hiigddeg)
        val card: CardView, // val card: CardView object reference, delgetsen deerh neg card component (click hiih bolomjtoi)
        val titleMn: String, // val titleMn: mongoloor haragdah ner (filter hiihdee ashiglaj bna)
        val categoryKey: String // val categoryKey: logic/intent deer damjuulah key (english key) "Length" gej yavuulna
    ) // data class tugsgul

    private lateinit var searchEditText: EditText // private: gadnaas handahgui, lateinit var: daraa нь (onCreate dotor) findViewById hiigeed zaaval utga onoono, odoogoor null bish gej amlana
    private lateinit var cards: List<CategoryCard> // List<CategoryCard>: 6 shirheg cardiin jagsaalt, bas lateinit (daraa ni listOf(...) gej utga onoono)

    override fun onCreate(savedInstanceState: Bundle?) { // override: etseg class-iin (Activity) onCreate-g dahin todorhoilj bna; Bundle? = nullable (baihgui baij bolno)
        super.onCreate(savedInstanceState) // super: etseg onCreate-g ehleed ajilluulna (Activity lifecycle zov yvahad chuhal)
        setContentView(R.layout.activity_main) // activity_main.xml layout-g ene Activity-d holboj delgets deer gargana

        setupToolbar() // toolbar-iin setup (app title, setSupportActionBar)
        setupCategoryCards() // 6 card object-uudaa findViewById hiigeed click listener-uud tavina
        setupSearch() // searchEditText deer TextWatcher tavij, bichih burid filter hiine
    } // onCreate tugsgul

    private fun setupToolbar() { // private function: zovhon dotroo duudagddag; toolbar-iig neg dor setup hiih
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar) // val: dahin oorchlogdohgui local variable; findViewById: XML deerees toolbar component-iig id-aar n olno
        setSupportActionBar(toolbar) // Toolbar-iig ActionBar bolj ajilluulna (AppCompat support)
        supportActionBar?.title = getString(R.string.app_name) // ?. = safe call (supportActionBar null baij bolno); title-g strings.xml dotorh app_name-aar tavina
    } // setupToolbar tugsgul

    private fun setupCategoryCards() { // 6 category card-iig barij avaad, click bolgond openConverter duudna
        searchEditText = findViewById(R.id.searchEditText) // lateinit var searchEditText-d end utga onooj bna; EditText component-iig id-aar n olno

        cards = listOf( // cards list-iig үүсгэж bna; listOf() ni immutable list buteene
            CategoryCard(findViewById(R.id.cardLength), "Урт", "Length"), // CategoryCard object: cardLength view + titleMn="Урт" + categoryKey="Length"
            CategoryCard(findViewById(R.id.cardArea), "Талбай", "Area"), // talbai card
            CategoryCard(findViewById(R.id.cardMass), "Жин", "Mass"), // jin card
            CategoryCard(findViewById(R.id.cardTime), "Хугацаа", "Time"), // hugatsaa card
            CategoryCard(findViewById(R.id.cardSpeed), "Хурд", "Speed"), // hurd card
            CategoryCard(findViewById(R.id.cardTemperature), "Температур", "Temperature") // temperatur card
        ) // listOf tugsgul

        // click -> open ConverterActivity // (comment) ene heseg: card deer darahad ConverterActivity ruu shiljine
        for (c in cards) { // for loop: cards list dotorh element bur deer davtana; c = CategoryCard
            c.card.setOnClickListener { // card component deer click event listener tavij bna
                openConverter(c.categoryKey) // click bolohod openConverter duudna; categoryKey ("Length" гэх мэт) intent extra bolgoj yavuulna
            } // onClick listener tugsgul
        } // for loop tugsgul
    } // setupCategoryCards tugsgul

    private fun normalize(s: String): String { // normalize function: search filter deer string-uudiig "ijil format" bolgoj, compare dahi aldaag bagasgana
        return s // return: doosh tsugluulj bolovsruulsan string butsaa
            .trim() // trim(): urd/ard taliin hooson zai, newline zergiig avna
            .lowercase(Locale("mn", "MN")) // lowercase(): jijig useg bolgono; Locale("mn","MN") ni Mongol helnii lower/upper rule-toi bailgah orолдlogo
            .replace(Regex("[\\u200B\\u200C\\u200D\\uFEFF]"), "") // replace: zero-width char (haragdahgui temdeg) ustgaj bna; keyboard/IME-ees irdeg asuudliig arilgana
            .replace(Regex("\\s+"), " ") // replace: olon hooson zai (tab/newline) -> neg " " bolgono; search input tseverleh
    } // normalize tugsgul

    private fun setupSearch() { // searchEditText bichigdeh buriid cards-iig shuumj haruulah function
        searchEditText.addTextChangedListener(object : TextWatcher { // TextWatcher interface implement hiij bna; object : TextWatcher = anonymous object (nergui class shig)
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {} // override: TextWatcher-iin method; end ashiglahgui tul hooson
            override fun afterTextChanged(s: Editable?) {} // override: bichlegduusnii daraa duudagddag; end ashiglahgui tul hooson

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { // onTextChanged: bichih buriid duudagddana; s = odoo bichigdsen text (nullable)
                val q = normalize(s?.toString() ?: "") // q = query; s null bol "" авна (?: elvis operator); toString() hiigeed normalize hiij tseverlej bna

                if (q.isEmpty()) { // if: query hooson bol (yu ch bichihgui)
                    for (c in cards) c.card.visibility = View.VISIBLE // buh card-iig haruulna; visibility = VISIBLE
                    return // return: ene method-eesee butsaad, doosh filter hiihgui
                } // if tugsgul

                for (c in cards) { // query hooson bish bol cards bur deer davtaad nuuh/haruulah shiidne
                    val title = normalize(c.titleMn) // title = тухайн card-iin mongol ner, normalize hiine (ijil format)
                    val key = normalize(c.categoryKey) // key = english key (Length/Area ...), normalize hiine

                    // кирилл + англи аль аль нь таарна // (comment) filter logic: Mongol title esvel english key ali neg ni query-g aguulsan bol match
                    val match = title.contains(q) || key.contains(q) // contains(): substring shalgana; || = OR; ali neg ni true bol match true
                    c.card.visibility = if (match) View.VISIBLE else View.GONE // if-expression: match bol haruulna, ugui bol GONE (oroinoos space ch avahgui)
                } // for loop tugsgul
            } // onTextChanged tugsgul
        }) // addTextChangedListener tugsgul
    } // setupSearch tugsgul

    private fun openConverter(category: String) { // openConverter: ConverterActivity ruu shiljih function; category = "Length" гэх мэт key
        val intent = Intent(this, ConverterActivity::class.java) // Intent: Activity hoorond shiljih object; this = current context; target = ConverterActivity class
        intent.putExtra("CATEGORY", category) // putExtra: intent dotor extra data hiij yavuulna; key="CATEGORY", value=category
        startActivity(intent) // startActivity: shine Activity ee neene (ConverterActivity)
    } // openConverter tugsgul
} // class tugsgul