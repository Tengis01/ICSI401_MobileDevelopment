package com.example.taskscheduler.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.taskscheduler.R

// Section spinner-iin custom adapter
// Today=ulaan, Upcoming=nogoon, Someday=tsagaan ongoor haruulna
class SectionSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    // Section index-ees zohih ongiig butsaana
    private fun colorForPosition(position: Int): Int = when (position) {
        0    -> context.getColor(R.color.section_today)
        1    -> context.getColor(R.color.section_upcoming)
        else -> context.getColor(R.color.section_someday)
    }

    // Songogdson item haragdah uyd
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)
        val tv = view.findViewById<TextView>(android.R.id.text1)
            ?: (view as? TextView)
            ?: view.findViewWithTag("text") as? TextView
            ?: (view as ViewGroup).getChildAt(0) as? TextView
            ?: return view
        tv.text      = items[position]
        tv.setTextColor(colorForPosition(position))
        return view
    }

    // Dropdown zadrahad item bur
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_dropdown_item, parent, false)
        val tv = view.findViewById<TextView>(android.R.id.text1)
            ?: (view as? TextView)
            ?: (view as ViewGroup).getChildAt(0) as? TextView
            ?: return view
        tv.text      = items[position]
        tv.setTextColor(colorForPosition(position))
        return view
    }
}