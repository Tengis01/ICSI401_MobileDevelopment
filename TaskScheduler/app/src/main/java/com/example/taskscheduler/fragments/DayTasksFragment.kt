package com.example.taskscheduler.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.adapters.TaskAdapter
import com.example.taskscheduler.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale

class DayTasksFragment : DialogFragment() {

    companion object {
        private const val ARG_DATE = "date"

        // CalendarFragment-aas duudah uyd date damjuulna - "2026-03-15"
        fun newInstance(date: String): DayTasksFragment {
            return DayTasksFragment().apply {
                arguments = Bundle().apply { putString(ARG_DATE, date) }
            }
        }
    }

    private var _dbHelper : DatabaseHelper? = null
    private val dbHelper get() = _dbHelper!!
    private var _adapter  : TaskAdapter?    = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_day_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = arguments?.getString(ARG_DATE) ?: return

        _dbHelper = DatabaseHelper(requireContext())

        // Ognoog unshihad amar helber ruu huvirgana - jishee ni "18  Мягмар"
        view.findViewById<TextView>(R.id.tv_day_title).text = formatDate(date)

        val rvTasks = view.findViewById<RecyclerView>(R.id.rv_day_tasks)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_day_empty)

        _adapter = TaskAdapter(
            context = requireContext(),
            onTaskToggle = { task, isDone ->
                dbHelper.toggleTaskDone(task.id, isDone)
                loadTasks(date, rvTasks, tvEmpty)
            },
            onTaskLongClick = { task ->
                dbHelper.deleteTask(task.id)
                loadTasks(date, rvTasks, tvEmpty)
            },
            onEditClick = { task ->
                AddTaskDialog.show(
                    context     = requireContext(),
                    dbHelper    = dbHelper,
                    onTaskAdded = { loadTasks(date, rvTasks, tvEmpty) },
                    task        = task
                )
            }
        )

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = _adapter

        loadTasks(date, rvTasks, tvEmpty)
    }

    // Tuhain ognoond baigaa hiigeegui task-uudiig л haruulna
    private fun loadTasks(date: String, rv: RecyclerView, tvEmpty: TextView) {
        val tasks = dbHelper.getAllTasks()
            .filter { it.date == date && !it.isDone }

        if (tasks.isEmpty()) {
            rv.visibility      = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rv.visibility      = View.VISIBLE
            tvEmpty.visibility = View.GONE
            _adapter?.submitTasks(tasks)
        }
    }

    // "2026-03-18" → "18  Мягмар" helber ruu huvirgana
    private fun formatDate(dateStr: String): String {
        return try {
            val sdf  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateStr) ?: return dateStr
            SimpleDateFormat("d  EEEE", Locale("mn")).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    // Dialog-iin hemjee - 90% orgon, 50% undur, center-t
    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        val dm = resources.displayMetrics

        window.setLayout(
            (dm.widthPixels  * 0.90).toInt(),
            (dm.heightPixels * 0.50).toInt()
        )
        window.setGravity(android.view.Gravity.CENTER)
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes.dimAmount = 0.6f
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dbHelper = null
        _adapter  = null
    }
}