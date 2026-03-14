package com.example.taskscheduler.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Task
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        // Sariin garchig haruulah item type
        private const val TYPE_MONTH = 0

        // Udriin garchig haruulah item type
        private const val TYPE_DAY = 1

        // Task card haruulah item type
        private const val TYPE_TASK = 2
    }

    // RecyclerView deer haragdah buh item-uudiig end hadgalna
    // Ene dotor MonthItem, DayItem, esvel Task object orj bolno
    private val items = mutableListOf<Any>()

    // Sariin garchig hadgalah data class
    data class MonthItem(val name: String)

    // Udriin garchig hadgalah data class
    data class DayItem(val number: String, val weekday: String)

    // RecyclerView deer hedэн item haragdahig butsaana
    override fun getItemCount() = items.size

    // Tuhain bairshild baigaa item yamar turliinх ve gedgiig todorhoilno
    // Ingesneer RecyclerView yamar layout ashiglahaa medne
    override fun getItemViewType(position: Int) = when (items[position]) {
        is MonthItem -> TYPE_MONTH
        is DayItem -> TYPE_DAY
        else -> TYPE_TASK
    }

    // Item-iin turleesee hamaaraad zohih ViewHolder uusgene
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // LayoutInflater ashiglaad XML layout-uudiig View bolgoj baina
        val inflater = LayoutInflater.from(context)

        return when (viewType) {

            // Sariin garchig bol item_calendar_month.xml ashiglana
            TYPE_MONTH -> MonthVH(
                inflater.inflate(R.layout.item_calendar_month, parent, false)
            )

            // Udriin garchig bol item_calendar_day.xml ashiglana
            TYPE_DAY -> DayVH(
                inflater.inflate(R.layout.item_calendar_day, parent, false)
            )

            // Task bol item_calendar_task.xml ashiglana
            else -> TaskVH(
                inflater.inflate(R.layout.item_calendar_task, parent, false)
            )
        }
    }

    // Belen bolson ViewHolder deer zohih medeelliig ni tavina
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {

            // MonthVH bol MonthItem medeelliig holboj baina
            is MonthVH -> holder.bind(items[position] as MonthItem)

            // DayVH bol DayItem medeelliig holboj baina
            is DayVH -> holder.bind(items[position] as DayItem)

            // TaskVH bol Task medeelliig holboj baina
            is TaskVH -> holder.bind(items[position] as Task)
        }
    }

    // Sariin garchig haruulah ViewHolder
    inner class MonthVH(view: View) : RecyclerView.ViewHolder(view) {

        // Sariin neriig haruulah TextView
        private val tv: TextView = view.findViewById(R.id.tv_month_name)

        // MonthItem-iin neriig TextView deer tavina
        fun bind(item: MonthItem) {
            tv.text = item.name
        }
    }

    // Udriin garchig haruulah ViewHolder
    inner class DayVH(view: View) : RecyclerView.ViewHolder(view) {

        // Udriin too haruulah TextView
        private val tvNum: TextView = view.findViewById(R.id.tv_day_number)

        // Doloo honogiin neriig haruulah TextView
        private val tvName: TextView = view.findViewById(R.id.tv_day_name)

        // DayItem-iin medeeleliig TextView-uud deer tavina
        fun bind(item: DayItem) {
            tvNum.text = item.number
            tvName.text = item.weekday
        }
    }

    // Task card haruulah ViewHolder
    inner class TaskVH(view: View) : RecyclerView.ViewHolder(view) {

        // Task-iin garchig haruulah TextView
        private val tvTitle: TextView = view.findViewById(R.id.tv_task_title)

        // Task-iin tsag haruulah TextView
        private val tvTime: TextView = view.findViewById(R.id.tv_task_time)

        // Task object-iin medeeleliig card deer tavina
        fun bind(task: Task) {

            // Task-iin garchgiig haruulna
            tvTitle.text = task.title

            // Hervee task tsagtai bol tsag-iig ni haruulna
            // Tsag baihgui bol buhel odor gej haruulna
            tvTime.text = if (task.time.isNotEmpty()) task.time
            else context.getString(R.string.all_day)

            // Hervee task hiigdsen bol text-iig buudaruulj haruulna
            // Ingesneer duussan task gedeg ni haragdanа
            if (task.isDone) {
                tvTitle.alpha = 0.4f
                tvTime.alpha = 0.4f
            } else {

                // Hiigeegui task bol engiin tod haruulna
                tvTitle.alpha = 1f
                tvTime.alpha = 1f
            }
        }
    }

    // Ognootoi task-uudiig sariin daraa, daraa ni udriin garчigtai ni hamt
    // zov daraаллаар ni items jagsaalt ruu beldej oruulah function
    fun submitTasks(tasks: List<Task>) {

        // Huuchin haragdaj baisan item-uudiig tseverlej baina
        items.clear()

        // Zovhon ognootoi task-uudiig uldeej, ognoogoor ni erembelej baina
        val sorted = tasks
            .filter { it.date.isNotEmpty() }
            .sortedBy { it.date }

        // Hervee ognootoi task baihgui bol hooson baidlaar shinechlene
        if (sorted.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        // String helbertei ognoog Date object bolgoj unshih format
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Sariin neriig gargah format
        val monthFmt = SimpleDateFormat("MMMM", Locale("mn"))

        // Udriin toog gargah format
        val dayNumFmt = SimpleDateFormat("d", Locale.getDefault())

        // Doloo honogiin neriig gargah format
        val dayNameFmt = SimpleDateFormat("EEEE", Locale("mn"))

        // Umnuh task-iin sariin neriig sanad yвах huvisagch
        var lastMonth = ""

        // Umnuh task-iin odriig sanad yвах huvisagch
        var lastDay = ""

        // Task buriig neg negээр ni shalgaj sariin, odriin, task item-uudiig beldene
        sorted.forEach { task ->

            // Task-iin date string-iig Date object bolgoj huvirgana
            // Hervee buruu format-tai bol ene task-iig alga alhamaar alгasana
            val date = try {
                dateFmt.parse(task.date)
            } catch (e: Exception) {
                null
            } ?: return@forEach

            // Sariin neriig gargaj ehnii useg-g tom bolgoj baina
            val monthStr = monthFmt.format(date).replaceFirstChar { it.uppercase() }

            // Task-iin ognoog unique tulhuur bolgon ashiglaj baina
            val dayStr = task.date

            // Hervee ene task shine sard hamaarч baival ehleed sariin garchig nemeh heregtei
            if (monthStr != lastMonth) {
                items.add(MonthItem(monthStr))
                lastMonth = monthStr

                // Sar soligdson bol umnuh udriin medeelliig reset hiij baina
                lastDay = ""
            }

            // Hervee ene task shine odor deer baival udriin garchig nemeh heregtei
            if (dayStr != lastDay) {
                items.add(
                    DayItem(
                        number = dayNumFmt.format(date),
                        weekday = dayNameFmt.format(date).replaceFirstChar { it.uppercase() }
                    )
                )
                lastDay = dayStr
            }

            // Suuld ni tuhain odor deer hamaarah task-iig nemej baina
            items.add(task)
        }

        // Shine beldsen item-uudiig RecyclerView deer dahin zurj haruulna
        notifyDataSetChanged()
    }
}