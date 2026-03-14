package com.example.taskscheduler.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Task

class TaskAdapter(
    private val context: Context,
    private val onTaskToggle: (Task, Boolean) -> Unit,
    private val onTaskLongClick: (Task) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        // Section garchig haruulah item type
        const val VIEW_TYPE_HEADER = 0

        // Task mur haruulah item type
        const val VIEW_TYPE_TASK = 1
    }

    // RecyclerView deer haragdah buh item-uudiig hadgalna
    // Ene dotor String header esvel Task object orj bolno
    private val items = mutableListOf<Any>()

    // RecyclerView deer heden item haragdahig butsaana
    override fun getItemCount() = items.size

    // Tuhain bairshild baigaa item yamar turliinх ve gedgiig todorhoilno
    // String bol header, busad ni task gej uzne
    override fun getItemViewType(position: Int): Int =
        if (items[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_TASK

    // Item-iin turleesee hamaaraad zohih ViewHolder uusgene
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_HEADER) {

            // Header item-d ashiglah layout-g inflate hiij baina
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)

        } else {

            // Task item-d ashiglah layout-g inflate hiij baina
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false)
            TaskViewHolder(view)
        }
    }

    // ViewHolder belen bolsonii daraa zohih medeelliig ni tavina
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as String)
            is TaskViewHolder   -> holder.bind(items[position] as Task)
        }
    }

    // Section garchig haruulah ViewHolder
    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Garchig haruulah TextView
        private val tvHeader: TextView = view.findViewById(R.id.tv_section_header)

        fun bind(title: String) {
            tvHeader.text = title
        }
    }

    // Task item haruulah ViewHolder
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Zuun taliin section ongoiin mur
        private val viewLine  : View         = view.findViewById(R.id.view_section_line)

        // Checkbox zurag haruulah ImageView
        private val ivCheck   : ImageView    = view.findViewById(R.id.iv_checkbox)

        // Task-iin ner haruulah TextView
        private val tvTitle   : TextView     = view.findViewById(R.id.tv_task_name)

        // Tsagiin mor - tsag baikhgui bol nuursan (GONE) baina
        private val llTimeRow : LinearLayout = view.findViewById(R.id.ll_time_row)

        // Hereglegchiin songosон ehleh tsag haruulah TextView
        private val tvStart   : TextView     = view.findViewById(R.id.tv_start_time)

        // Duusah tsag baival haragdah separator
        private val tvEndSep  : TextView     = view.findViewById(R.id.tv_end_time_separator)

        // Duusah tsag haruulah TextView
        private val tvEnd     : TextView     = view.findViewById(R.id.tv_end_time)

        // Task medeelliig UI deer tavij, click event-uudiig holboh function
        fun bind(task: Task) {

            // Task-iin garchgiig haruulna
            tvTitle.text = task.title

            // Section-oos hamaaruulj zuun taliin ongoiin moriig budana
            viewLine.setBackgroundColor(sectionColor(task.section))

            // Task hiigdsen esehees ni hamaaruulaad haragdah style-iig tohiruulna
            applyDoneStyle(task.isDone)

            // Tsagiin moriin medeelliig tavina
            bindTimeRow(task)

            // Checkbox deer darval hiigdsen tuluv-g esregeer ni solino
            ivCheck.setOnClickListener {
                onTaskToggle(task, !task.isDone)
            }

            // Task item deer urt darval ustgah action hiine
            itemView.setOnLongClickListener {

                // Ustgahyn umnu text deer dunduur zuraas tavij, ulaan unguur haruulna
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.setTextColor(context.getColor(R.color.accent_red))
                tvTitle.alpha = 0.7f

                // Baga zereg huleesnii daraa jinhene ustgah callback duudna
                itemView.postDelayed({ onTaskLongClick(task) }, 400)
                true
            }

            // Task item deer engiin darval checkbox darsantai adil uildel hiine
            itemView.setOnClickListener {
                onTaskToggle(task, !task.isDone)
            }
        }

        // Section-oos hamaaruulj zuun taliin ongoiin ongog butsaana
        private fun sectionColor(section: String): Int = when (section) {
            Task.SECTION_TODAY    -> context.getColor(R.color.section_today)
            Task.SECTION_UPCOMING -> context.getColor(R.color.section_upcoming)
            else                  -> context.getColor(R.color.section_someday)
        }

        // Ehleh bolon duusah tsagiig haruulah function
        private fun bindTimeRow(task: Task) {
            if (task.time.isEmpty()) {
                llTimeRow.visibility = View.GONE
                return
            }

            llTimeRow.visibility = View.VISIBLE
            tvStart.text = task.time

            if (task.endTime.isNotEmpty()) {
                tvEndSep.visibility = View.VISIBLE
                tvEnd.visibility    = View.VISIBLE
                tvEnd.text          = task.endTime
            } else {
                tvEndSep.visibility = View.GONE
                tvEnd.visibility    = View.GONE
            }
        }

        // Task hiigdsen esehees hamaaruulj UI style-iig oorchloh function
        private fun applyDoneStyle(isDone: Boolean) {

            if (isDone) {

                // Hiigdsen task bol checked checkbox zurag tavina
                ivCheck.setImageResource(R.drawable.ic_checkbox_checked)

                // Text deer dunduur zuraas tavina
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                // Text-iin ungiig budaaruulna
                tvTitle.setTextColor(context.getColor(R.color.text_hint))
                tvTitle.alpha = 0.6f

            } else {

                // Hiigeegui task bol uncheck checkbox zurag tavina
                ivCheck.setImageResource(R.drawable.ic_checkbox_unchecked)

                // Dunduur zuraasiig arilgana
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTitle.setTextColor(context.getColor(R.color.text_primary))
                tvTitle.alpha = 1.0f
            }
        }
    }

    // Task-uudiig Today, Upcoming, Someday hesgeer ni huvaaj
    // Header + task-uud helbereer items jagsaalt ruu hiih function
    fun submitTasks(tasks: List<Task>) {

        // Umnuh item-uudiig tseverlej baina
        items.clear()

        // Section bur dotroo ehleh tsagaar erembelne
        // Tsaggui task-uud tsagtaitai taskuudiin araas orно
        fun List<Task>.sortedByTime() = sortedWith(
            compareBy(
                { if (it.time.isEmpty()) 1 else 0 },
                { it.time }
            )
        )

        // Today hesegt hamaarah task-uudiig shuugej avna
        val today    = tasks.filter { it.section == Task.SECTION_TODAY }.sortedByTime()

        // Upcoming hesegt hamaarah task-uudiig shuugej avna
        val upcoming = tasks.filter { it.section == Task.SECTION_UPCOMING }.sortedByTime()

        // Someday hesegt hamaarah task-uudiig shuugej avna
        val someday  = tasks.filter { it.section == Task.SECTION_SOMEDAY }.sortedByTime()

        // Hervee today hesegt task baival ehleed header, daraa ni task-uudiig nemej baina
        if (today.isNotEmpty()) {
            items.add(context.getString(R.string.section_today))
            items.addAll(today)
        }

        // Hervee upcoming hesegt task baival ehleed header, daraa ni task-uudiig nemej baina
        if (upcoming.isNotEmpty()) {
            items.add(context.getString(R.string.section_upcoming))
            items.addAll(upcoming)
        }

        // Hervee someday hesegt task baival ehleed header, daraa ni task-uudiig nemej baina
        if (someday.isNotEmpty()) {
            items.add(context.getString(R.string.section_someday))
            items.addAll(someday)
        }

        // Shine jagsaaltaar RecyclerView-g dahin zurj haruulna
        notifyDataSetChanged()
    }

    // Calendar zorilgotoi task-uudiig ognoogoor ni buleglej
    // Header + task-uud helbereer items jagsaalt ruu hiih function
    fun submitCalendarTasks(tasks: List<Task>) {

        // Umnuh item-uudiig tseverlej baina
        items.clear()

        // Zovhon ognootoi task-uudiig uldeej, neg neg ognoogoor ni group hiine
        val grouped = tasks
            .filter { it.date.isNotEmpty() }
            .groupBy { it.date }
            .toSortedMap()

        // Ognoo buriin huvid ehleed ognoonii header, daraa ni tuhain odoriiin task-uudiig nemej baina
        grouped.forEach { (date, dayTasks) ->
            items.add(formatDateHeader(date))
            items.addAll(dayTasks)
        }

        // Shine jagsaaltaar RecyclerView-g dahin zurj haruulna
        notifyDataSetChanged()
    }

    // yyyy-MM-dd helbertei ognoog iluu hun unshihad amar header text bolgoj huvirgah function
    private fun formatDateHeader(dateStr: String): String {
        return try {
            val sdf  = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = sdf.parse(dateStr) ?: return dateStr
            val out  = java.text.SimpleDateFormat("d  EEEE", java.util.Locale("mn"))
            out.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }
}