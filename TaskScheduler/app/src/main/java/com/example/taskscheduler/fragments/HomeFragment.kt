package com.example.taskscheduler.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.example.taskscheduler.R
import com.example.taskscheduler.adapters.TaskListAdapter
import com.example.taskscheduler.data.DatabaseHelper
import com.example.taskscheduler.data.TaskList

class HomeFragment : Fragment() {

    private var _dbHelper: DatabaseHelper? = null
    private val dbHelper get() = _dbHelper!!

    private var _adapter: TaskListAdapter? = null
    private lateinit var rvLists: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _dbHelper = DatabaseHelper(requireContext())
        rvLists   = view.findViewById(R.id.rv_task_lists)

        _adapter = TaskListAdapter(
            context       = requireContext(),
            lists         = mutableListOf(),
            onListClick   = { taskList ->
                // List card darval tuhain list-iin task-uudiig haruulah dialog fragment neene
                ListTasksFragment
                    .newInstance(taskList.id, taskList.name)
                    .show(childFragmentManager, "list_tasks")
            },
            onAddNewClick = { showAddListDialog() }
        )

        val layoutManager = GridLayoutManager(requireContext(), 2)
        rvLists.layoutManager = layoutManager
        rvLists.adapter = _adapter

        loadLists()
    }

    private fun loadLists() {
        _adapter?.updateLists(dbHelper.getAllLists())
    }

    private fun showAddListDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_list, null)

        val etName      : TextInputEditText = dialogView.findViewById(R.id.et_list_name)
        val spinnerIcon : Spinner           = dialogView.findViewById(R.id.spinner_icon)

        val iconOptions = arrayOf("Huviin", "Ajil", "Sudalgaa")
        val iconValues  = arrayOf("ic_personal", "ic_business", "ic_study")

        val iconAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, iconOptions)
        iconAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerIcon.adapter = iconAdapter

        val dialog = AlertDialog.Builder(requireContext(), R.style.DarkDialogTheme)
            .setTitle(getString(R.string.add_list))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.btn_save), null)
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .create()

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Ner orulna uu"
                return@setOnClickListener
            }
            dbHelper.insertList(TaskList(
                name     = name,
                iconName = iconValues[spinnerIcon.selectedItemPosition]
            ))
            loadLists()
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        loadLists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dbHelper = null
        _adapter  = null
    }
}