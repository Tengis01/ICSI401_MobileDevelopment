package com.example.taskscheduler.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.data.TaskList

class TaskListAdapter(
    private val context: Context,
    private var lists: MutableList<TaskList>,
    private val onListClick: (TaskList) -> Unit,
    private val onAddNewClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        // Engiin list card haruulah item type
        private const val VIEW_TYPE_LIST = 0

        // Shine list nemeh card haruulah item type
        private const val VIEW_TYPE_ADD = 1
    }

    // RecyclerView deer niit heden item haragdahig butsaana
    // End jinhene list-uudees гадна suuld ni neg add card baigaa uchraas +1 hiij baina
    override fun getItemCount() = lists.size + 1

    // Tuhain bairshliin item yamar turliinх ve gedgiig todorhoilno
    // Hervee position ni list-uudiin hemjeenees baga bol engiin list card
    // Harin suuliin neg item bol add card baina
    override fun getItemViewType(position: Int): Int {
        return if (position < lists.size) VIEW_TYPE_LIST else VIEW_TYPE_ADD
    }

    // Item-iin turluusuu hamaaraad zohih layout-iig inflate hiij ViewHolder uusgene
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_LIST) {

            // Engiin list card-iin layout-g View bolgoj baina
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_task_list, parent, false)

            // List card-d zoriulsan ViewHolder uusgej butsaana
            ListViewHolder(view)

        } else {

            // Shine list nemeh card-iin layout-g View bolgoj baina
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_add_list, parent, false)

            // Add card-d zoriulsan ViewHolder uusgej butsaana
            AddViewHolder(view)
        }
    }

    // Belen bolson ViewHolder deer zohih medeelliig ni tavina
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // Hervee engiin list card bol tuhain TaskList-iin medeelliig holboj baina
        if (holder is ListViewHolder) {
            holder.bind(lists[position])

            // Hervee add card bol click event-ee holboj baina
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    // Engiin list card haruulah ViewHolder
    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // List-iin icon haruulah ImageView
        private val ivIcon: ImageView = view.findViewById(R.id.iv_list_icon)

        // List-iin ner haruulah TextView
        private val tvName: TextView = view.findViewById(R.id.tv_list_name)

        // Tuhain list deer heden task baigaag haruulah TextView
        private val tvCount: TextView = view.findViewById(R.id.tv_task_count)

        // TaskList object-iin medeelliig card deer tavina
        fun bind(taskList: TaskList) {

            // List-iin neriig haruulna
            tvName.text = taskList.name

            // Task-iin too-g string resource ashiglaad haruulna
            tvCount.text = context.getString(R.string.tasks_count, taskList.taskCount)

            // Icon-iiin neriig drawable resource id bolgoj huvirgaad zurag ni tavina
            val iconRes = getIconResource(taskList.iconName)
            ivIcon.setImageResource(iconRes)

            // Card deer darval tuhain list-iin click callback duudna
            itemView.setOnClickListener { onListClick(taskList) }
        }
    }

    // Shine list nemeh card haruulah ViewHolder
    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Add card deer darval shine list nemeh callback duudna
        fun bind() {
            itemView.setOnClickListener { onAddNewClick() }
        }
    }

    // Shine list-uudiin medeelel irhed huuchin jagsaaltiig shinechilne
    fun updateLists(newLists: List<TaskList>) {

        // Huuchin list-uudiig tseverlej baina
        lists.clear()

        // Shine list-uudiig nemj baina
        lists.addAll(newLists)

        // RecyclerView-g dahin zurj shine medeelel haruulna
        notifyDataSetChanged()
    }

    // Icon-iin neriig jinhene drawable resource id ruu huvirgah function
    private fun getIconResource(iconName: String): Int {
        return when (iconName) {

            // Huviin list-iin icon
            "ic_personal" -> R.drawable.ic_personal

            // Ajliin list-iin icon
            "ic_business" -> R.drawable.ic_business

            // Sudalgaanii list-iin icon
            "ic_study" -> R.drawable.ic_study

            // Hervee tanigdahgui ner irvel default icon ashiglana
            else -> R.drawable.ic_personal
        }
    }
}