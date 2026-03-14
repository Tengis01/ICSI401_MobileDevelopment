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

class ListTasksFragment : DialogFragment() {

    companion object {
        private const val ARG_LIST_ID   = "list_id"
        private const val ARG_LIST_NAME = "list_name"

        fun newInstance(listId: Long, listName: String): ListTasksFragment {
            return ListTasksFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_LIST_ID, listId)
                    putString(ARG_LIST_NAME, listName)
                }
            }
        }
    }

    private var _dbHelper : DatabaseHelper? = null
    private val dbHelper get() = _dbHelper!!

    private var _adapter  : TaskAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_list_tasks, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listId   = arguments?.getLong(ARG_LIST_ID)    ?: return
        val listName = arguments?.getString(ARG_LIST_NAME) ?: ""

        _dbHelper = DatabaseHelper(requireContext())

        view.findViewById<TextView>(R.id.tv_list_title).text = listName

        val rvTasks = view.findViewById<RecyclerView>(R.id.rv_list_tasks)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)

        _adapter = TaskAdapter(
            context = requireContext(),
            onTaskToggle = { task, isDone ->
                dbHelper.toggleTaskDone(task.id, isDone)
                loadTasks(listId, rvTasks, tvEmpty)
            },
            onTaskLongClick = { task ->
                dbHelper.deleteTask(task.id)
                loadTasks(listId, rvTasks, tvEmpty)
            }
        )

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = _adapter

        loadTasks(listId, rvTasks, tvEmpty)
    }

    private fun loadTasks(listId: Long, rv: RecyclerView, tvEmpty: TextView) {
        val tasks = dbHelper.getTasksByList(listId)
        if (tasks.isEmpty()) {
            rv.visibility      = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rv.visibility      = View.VISIBLE
            tvEmpty.visibility = View.GONE
            _adapter?.submitTasks(tasks)
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        val dm = resources.displayMetrics

        // 90% orgon, 52% undur - 3 task asuudalgui haragdana, tsaash scroll hiine
        window.setLayout(
            (dm.widthPixels  * 0.90).toInt(),
            (dm.heightPixels * 0.52).toInt()
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