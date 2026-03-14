package com.example.taskscheduler.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Task
import java.util.Calendar

class CalendarGridAdapter(
    private val context: Context,
    private val onDayClick: (date: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_DAY    = 1
    }

    // Neg odoriig toloologch data class
    data class DayCell(
        val day: Int,          // 1-31, 0 = hooson nugas
        val date: String,      // "2026-03-15", hooson bol ""
        val dots: List<String> // section-uudiin jagsaalt dot zuraхад
    )

    private val cells = mutableListOf<Any>()   // String header esvel DayCell

    // Dolgoon gariin neriig Mongoloor todorhoilno
    // Daваa-гаас эхлэнэ
    private val dayNames = listOf("Да", "Мя", "Лх", "Пү", "Ба", "Бя", "Ня")

    override fun getItemCount() = cells.size

    override fun getItemViewType(position: Int) =
        if (cells[position] is String) TYPE_HEADER else TYPE_DAY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == TYPE_HEADER) {
            HeaderVH(inflater.inflate(R.layout.item_calendar_grid_header, parent, false))
        } else {
            DayVH(inflater.inflate(R.layout.item_calendar_grid_day, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderVH -> holder.bind(cells[position] as String)
            is DayVH    -> holder.bind(cells[position] as DayCell)
        }
    }

    inner class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(name: String) {
            (itemView as TextView).text = name
        }
    }

    inner class DayVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDay  : TextView     = view.findViewById(R.id.tv_day_num)
        private val llDots : LinearLayout = view.findViewById(R.id.ll_dots)
        private val dot1   : View         = view.findViewById(R.id.dot1)
        private val dot2   : View         = view.findViewById(R.id.dot2)
        private val dot3   : View         = view.findViewById(R.id.dot3)

        fun bind(cell: DayCell) {
            if (cell.day == 0) {
                // Hooson nugas - yum haruulahgui
                tvDay.text       = ""
                dot1.visibility  = View.INVISIBLE
                dot2.visibility  = View.INVISIBLE
                dot3.visibility  = View.INVISIBLE
                itemView.isClickable = false
                return
            }

            tvDay.text = cell.day.toString()
            itemView.isClickable = true

            // Unuudriin ognoog todruulj haruulna
            val today = getTodayString()
            if (cell.date == today) {
                tvDay.setTextColor(context.getColor(R.color.accent_red))
                tvDay.setBackgroundResource(R.drawable.shape_today_bg)
            } else {
                tvDay.setTextColor(context.getColor(R.color.text_primary))
                tvDay.background = null
            }

            // Dot-uudiig section-oos hamaruulj budana
            val dotViews = listOf(dot1, dot2, dot3)
            dotViews.forEach { it.visibility = View.GONE }

            cell.dots.forEachIndexed { i, section ->
                if (i < dotViews.size) {
                    dotViews[i].visibility = View.VISIBLE
                    val color = when (section) {
                        Task.SECTION_TODAY    -> context.getColor(R.color.section_today)
                        Task.SECTION_UPCOMING -> context.getColor(R.color.section_upcoming)
                        else                  -> context.getColor(R.color.section_someday)
                    }
                    // Dot-iin ongig dinamikaar tavih
                    val drawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(color)
                    }
                    dotViews[i].background = drawable
                }
            }

            itemView.setOnClickListener {
                if (cell.date.isNotEmpty()) onDayClick(cell.date)
            }
        }
    }

    // Songogdson sar bolон жилийн grid cell-uudiig uusgej baina
    fun buildMonth(year: Int, month: Int, tasks: List<Task>) {
        cells.clear()

        // Dolgoon gariin header nemiig nemne
        dayNames.forEach { cells.add(it) }

        val cal = Calendar.getInstance()
        cal.set(year, month, 1)

        // Daваа = 0 gej tootsood ehnii odor yamar garig bolohiig olno
        // Calendar.MONDAY = 2, tul (dayOfWeek - 2 + 7) % 7
        val firstDayOffset = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7

        // Ehnii odoroos omno hooson nugas nemne
        repeat(firstDayOffset) { cells.add(DayCell(0, "", emptyList())) }

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            val dateStr  = String.format("%04d-%02d-%02d", year, month + 1, day)
            // Tuhain odoriin hiigeegui task-uudiin section-uudiig avna
            val dayTasks = tasks.filter { it.date == dateStr && !it.isDone }
            val dots     = dayTasks.map { it.section }.distinct().take(3)
            cells.add(DayCell(day, dateStr, dots))
        }

        notifyDataSetChanged()
    }

    // Unuudriin ognoog "yyyy-MM-dd" helbereer butsaana
    private fun getTodayString(): String {
        val cal = Calendar.getInstance()
        return String.format(
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }
}