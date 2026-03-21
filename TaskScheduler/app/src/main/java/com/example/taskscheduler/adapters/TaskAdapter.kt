package com.example.taskscheduler.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Task

class TaskAdapter(
    private val context        : Context,
    private val onTaskToggle   : (Task, Boolean) -> Unit,
    private val onTaskLongClick: (Task) -> Unit,
    private val onEditClick    : (Task) -> Unit   // edit icon darval duudagdah callback
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
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
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
        private val tvHeader: TextView = view.findViewById(R.id.tv_section_header)
        fun bind(title: String) { tvHeader.text = title }
    }

    // Task item haruulah ViewHolder
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Zuun taliin section ongoiin mur
        private val viewLine : View      = view.findViewById(R.id.view_section_line)

        // Checkbox zurag haruulah ImageView
        private val ivCheck  : ImageView = view.findViewById(R.id.iv_checkbox)

        // Task-iin ner haruulah TextView
        private val tvTitle  : TextView  = view.findViewById(R.id.tv_task_name)

        // Ogno + tsagiin medeelel haruulah TextView
        private val tvTime   : TextView  = view.findViewById(R.id.tv_time_info)

        // Edit icon - task-iig zasvarлах
        private val ivEdit   : ImageView = view.findViewById(R.id.iv_edit)

        fun bind(task: Task) {
            tvTitle.text = task.title

            // Section-oos hamaaruulj zuun taliin ongoiin moriig budana
            viewLine.setBackgroundColor(sectionColor(task.section))

            // Task hiigdsen esehees ni hamaaruulaad haragdah style-iig tohiruulna
            applyDoneStyle(task.isDone)

            // Ogno tsagiin medeelliig formatted baidlaar haruulna
            bindTimeInfo(task)

            // Checkbox deer darval hiigdsen tuluv-g esregeer ni solino
            ivCheck.setOnClickListener { onTaskToggle(task, !task.isDone) }

            // Edit icon darval tuhain task-iin medeelleeer edit dialog neene
            ivEdit.setOnClickListener { onEditClick(task) }

            // Task item deer urt darval ustgah action hiine
            itemView.setOnLongClickListener {

                // Ustgahyn umnu text deer dunduur zuraas tavij, ulaan unguur haruulna
                // Ingesneer hereglegchid ustgah action bolj baigaag oilgono
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.setTextColor(context.getColor(R.color.accent_red))
                tvTitle.alpha = 0.7f

                // Baga zereg huleesnii daraa jinhene ustgah callback duudna
                itemView.postDelayed({ onTaskLongClick(task) }, 400)
                true
            }

            // Task item deer engiin darval checkbox darsantai adil uildel hiine
            itemView.setOnClickListener { onTaskToggle(task, !task.isDone) }
        }

        // Ogno tsagiin medeelliig format hiij haruulah function
        // Jishee:
        //   Ogno + ehleh tsag + duusah tsag (neg ondor) → 2026.03.19  22:18 - 23:26
        //   Ogno + ehleh tsag + duusah tsag (oor odor)  → 2026.03.19  22:18 - 03.26  22:18
        //   Ogno + ehleh tsag                           → 2026.03.19  22:18
        //   Zuvhan ogno                                 → 2026.03.19
        private fun bindTimeInfo(task: Task) {
            if (task.date.isEmpty()) {
                tvTime.visibility = View.GONE
                return
            }

            // Ognoig "yyyy-MM-dd" → "yyyy.MM.dd" helber ruu huvirgana
            val datePart = task.date.replace("-", ".")

            val text = when {

                // Ehleh tsag + duusah tsag 2 hоёр baiх - oor odor baij bolno
                task.time.isNotEmpty() && task.endTime.isNotEmpty() -> {
                    val startPart = "$datePart  ${task.time}"

                    // EndTime-iin ognoог тооцоолно - ehleh tsag + duusah tsag neg ondor ch baij bolno
                    val endDatePart = resolveEndDate(task.date, task.time, task.endTime)

                    if (endDatePart == task.date) {
                        // Neg odor dotor duusdag - ognoig davtahgui
                        "$startPart - ${task.endTime}"
                    } else {
                        // Oor odor duusdag - end ognoig haruulna
                        val endShort = endDatePart.substring(5).replace("-", ".")
                        "$startPart - $endShort  ${task.endTime}"
                    }
                }

                // Zuvhan ehleh tsag baiх
                task.time.isNotEmpty() -> "$datePart  ${task.time}"

                // Zuvhan ogno baiх
                else -> datePart
            }

            tvTime.text       = text
            tvTime.visibility = View.VISIBLE
        }

        // EndTime ehleh tsagaas baga baivaл daraa odor duusdаg gej uzne
        // Jishee: ehleh 23:00, duusah 01:00 → daraa odor
        private fun resolveEndDate(date: String, startTime: String, endTime: String): String {
            val startMin = timeToMinutes(startTime)
            val endMin   = timeToMinutes(endTime)

            // EndTime ehleh tsagaas ih baivaл neg ondor - ognoo adil
            if (endMin > startMin) return date

            // EndMin <= startMin = shono damjsan - 1 odor nemne
            return try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val cal = java.util.Calendar.getInstance()
                cal.time = sdf.parse(date)!!
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                sdf.format(cal.time)
            } catch (e: Exception) {
                date
            }
        }

        // "HH:mm" → minutaar huvirgah tuslah function
        private fun timeToMinutes(time: String): Int {
            val parts = time.split(":")
            return parts[0].toInt() * 60 + parts[1].toInt()
        }

        // Section-oos hamaaruulj zuun taliin ongoiin ongog butsaana
        private fun sectionColor(section: String): Int = when (section) {
            Task.SECTION_TODAY    -> context.getColor(R.color.section_today)
            Task.SECTION_UPCOMING -> context.getColor(R.color.section_upcoming)
            else                  -> context.getColor(R.color.section_someday)
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

                // Bas baga zereg transparant bolgoj duussan gedgiig iluu tod haruulna
                tvTitle.alpha = 0.6f

            } else {

                // Hiigeegui task bol uncheck checkbox zurag tavina
                ivCheck.setImageResource(R.drawable.ic_checkbox_unchecked)

                // Dunduur zuraasiig arilgana
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

                // Engiin undsen text ungiig tavina
                tvTitle.setTextColor(context.getColor(R.color.text_primary))

                // Buren tod haruulna
                tvTitle.alpha = 1.0f
            }
        }
    }

    // Task-uudiig Today, Upcoming, Someday hesgeer ni huvaaj
    // Header + task-uud helbereer items jagsaalt ruu hiih function
    fun submitTasks(tasks: List<Task>) {
        items.clear()

        // Section bur dotroo ehleh tsagaar erembelne
        // Tsaggui task-uud tsagtaitai taskuudiin araas orno
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
        items.clear()

        // Zovhon ognootoi task-uudiig uldeej, neg neg ognoogoor ni group hiine
        // toSortedMap ashiglasnaar ognoonuud usuh daraallaar baina
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