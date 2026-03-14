package com.example.taskscheduler.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.taskscheduler.R
import com.example.taskscheduler.adapters.TaskAdapter
import com.example.taskscheduler.data.DatabaseHelper

class AllTasksFragment : Fragment() {

    // Database-tei ajillah helper object
    private var _dbHelper: DatabaseHelper? = null
    private val dbHelper get() = _dbHelper!!

    // RecyclerView deer task-uudiig haruulah adapter
    private var _adapter: TaskAdapter? = null
    private lateinit var rvTasks: RecyclerView

    // Fragment delgets deer garah ued ashiglah layout-iig end uusgene
    // fragment_all_tasks.xml file-g View object bolgoj butsaaj baina
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // XML layout-iig program ashiglah bolomjtoi View bolgoj huvirgaj baina
        val view = inflater.inflate(R.layout.fragment_all_tasks, container, false)

        // Belen bolson View-g butsaana
        return view
    }

    // onCreateView-aas butssan view belen bolsonii daraa ene function ajillana
    // End RecyclerView, adapter, button zergiig holboj ogogdol achaalna
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Database helper-iig uusgej baina
        // Ene objectoor damjuulaad task-uudaa database-aas avna, shinechilne, ustgana
        _dbHelper = DatabaseHelper(requireContext())

        // fragment_all_tasks layout dotorh RecyclerView-g id-aar ni oldoj avna
        rvTasks = view.findViewById(R.id.rv_all_tasks)

        // Task-uudiig haruulah adapter uusgej baina
        // onTaskToggle ni checkbox oorchlogdohod ajillah logic
        // onTaskLongClick ni task deer urt darahad ajillah logic
        _adapter = TaskAdapter(
            context = requireContext(),
            onTaskToggle = { task, isDone ->

                // Songogdson task-iin hiisen esehiig database deer shinechilj baina
                dbHelper.toggleTaskDone(task.id, isDone)

                // Orson medeeleliig delgets deer shuud haruulahyn tuld list-iig dahin achaalna
                loadTasks()
            },
            onTaskLongClick = { task ->

                // Urt daragdсан task-iig database-aas ustgaj baina
                dbHelper.deleteTask(task.id)

                // Task ustsanii daraa delgets deer shine jagsaalt gargahyn tuld dahin achaalna
                loadTasks()
            }
        )

        // RecyclerView deer item-uudiig deerees doosh ni neg baganaar haruulah tohirgoo
        // Ene screen deer task-uud list helbereer haragdana
        rvTasks.layoutManager = LinearLayoutManager(requireContext())

        // RecyclerView-g adapter-t ni holboj baina
        rvTasks.adapter = _adapter

        // Shine task nemeh floating button-g olj click event holboj baina
        view.findViewById<FloatingActionButton>(R.id.fab_add_task)
            .setOnClickListener {

                // AddTaskDialog-iig neej shine task oruulah bolomj olgoj baina
                // onTaskAdded dotor loadTasks duudaj shine nemegdsen task-iig delgets deer haruulna
                AddTaskDialog.show(
                    context = requireContext(),
                    dbHelper = dbHelper,
                    onTaskAdded = { loadTasks() }
                )
            }

        // Anhnii udaad database deer baigaa task-uudiig unshaad delgets deer gargana
        loadTasks()
    }

    // Database dotorh buh task-iig unshaad adapter ruu ogno
    // Ingesneer RecyclerView deer hamgiin suuliin medeelel haragdana
    private fun loadTasks() {

        // getAllTasks function-eer database-aas task-uudiig avna
        // submitTasks function-eer adapteriin medeeleliig shinechilne
        _adapter?.submitTasks(dbHelper.getAllTasks())
    }

    // Fragment dahin delgets deer idevhtei haragdah ued task-uudiig shinechilne
    // Ingesneer oor delgetsees butsaj irehad hamgiin suuliin baidal haragdana
    override fun onResume() {
        super.onResume()

        // Task-uudiig dahin achaalj delgets deer shineчилнэ
        loadTasks()
    }

    // Fragment-iin view ustah ued ashiglasan reference-uudiig tseverlej baina
    // Ene ni memory ashiglalt deer tus boldog
    override fun onDestroyView() {
        super.onDestroyView()

        // Database helper reference-iig null bolgono
        _dbHelper = null

        // Adapter reference-iig null bolgono
        _adapter = null
    }
}