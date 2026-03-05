package com.example.bmicalculator

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvBmiValue = findViewById<TextView>(R.id.tvBmiValue)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val btnRefresh = findViewById<FloatingActionButton>(R.id.btnRefresh)

        val bmi = intent.getDoubleExtra("bmi", 0.0)
        val category = intent.getStringExtra("category") ?: ""
        val message = intent.getStringExtra("message") ?: ""

        tvBmiValue.text = String.format(Locale.US, "%.1f", bmi)
        tvCategory.text = category
        tvMessage.text = message

        btnRefresh.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("reset", true)
            startActivity(i)
            finish()
        }
    }
}
