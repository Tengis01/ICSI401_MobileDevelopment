package com.example.taskscheduler.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.adapters.CalendarGridAdapter
import com.example.taskscheduler.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _dbHelper : DatabaseHelper?    = null
    private val dbHelper get() = _dbHelper!!

    private var _adapter  : CalendarGridAdapter? = null

    // Odoogiin haruulj baigaa sar bolon jil
    private var currentYear  = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)   // 0-based

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _dbHelper = DatabaseHelper(requireContext())

        val rvGrid      = view.findViewById<RecyclerView>(R.id.rv_calendar_grid)
        val tvMonthYear = view.findViewById<TextView>(R.id.tv_month_year)
        val btnPrev     = view.findViewById<ImageButton>(R.id.btn_prev_month)
        val btnNext     = view.findViewById<ImageButton>(R.id.btn_next_month)

        // 7 baganatai grid - garig ner + odornuud
        val gridManager = GridLayoutManager(requireContext(), 7)
        rvGrid.layoutManager = gridManager

        _adapter = CalendarGridAdapter(
            context    = requireContext(),
            onDayClick = { date ->
                // Odor darval tuhain odoriiin task-iig popup-d haruulna
                DayTasksFragment
                    .newInstance(date)
                    .show(childFragmentManager, "day_tasks")
            }
        )
        rvGrid.adapter = _adapter

        // Omno sariin tovch
        btnPrev.setOnClickListener {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
            updateCalendar(tvMonthYear)
        }

        // Daraa sariin tovch
        btnNext.setOnClickListener {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
            updateCalendar(tvMonthYear)
        }

        updateCalendar(tvMonthYear)
    }

    // Odoogiin sar-d zohih grid-iig uusgej haruulna
    private fun updateCalendar(tvMonthYear: TextView) {

        // Sariin neriig Mongoloor haruulna - jishee ni "МАРТ 2026"
        val cal = Calendar.getInstance()
        cal.set(currentYear, currentMonth, 1)
        val monthName = SimpleDateFormat("MMMM yyyy", Locale("mn"))
            .format(cal.time)
            .uppercase()
        tvMonthYear.text = monthName

        // Buh task-iig avch adapter-t ogno
        _adapter?.buildMonth(currentYear, currentMonth, dbHelper.getAllTasks())
    }

    override fun onResume() {
        super.onResume()
        // Tab ruu butsaj irehed shine task-uudiig haruulna
        val tvMonthYear = view?.findViewById<TextView>(R.id.tv_month_year) ?: return
        updateCalendar(tvMonthYear)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dbHelper = null
        _adapter  = null
    }
}