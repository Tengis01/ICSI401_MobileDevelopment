package com.example.taskscheduler.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import com.example.taskscheduler.R
import com.example.taskscheduler.adapters.SectionSpinnerAdapter
import com.example.taskscheduler.data.DatabaseHelper
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.notifications.ReminderScheduler
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.Calendar
import java.util.Locale

object AddTaskDialog {

    // Shine task nemeh esvel baigaa task-iig edit hiih dialog haruulah function
    // task = null baivaл ADD gorим, task damjuulvaл EDIT gorим
    fun show(
        context     : Context,
        dbHelper    : DatabaseHelper,
        onTaskAdded : () -> Unit,
        task        : Task? = null
    ) {
        // Edit gorим esehiig todorhoilno
        val isEditMode = task != null

        // dialog_add_task.xml layout-g unshaad dialog dotor ashiglah View bolgoj baina
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_add_task, null)

        val etTitle        = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_task_title)
        val spinnerList    = dialogView.findViewById<Spinner>(R.id.spinner_list)
        val spinnerSection = dialogView.findViewById<Spinner>(R.id.spinner_section)
        val tvDate         = dialogView.findViewById<TextView>(R.id.tv_selected_date)
        val tvTime         = dialogView.findViewById<TextView>(R.id.tv_selected_time)
        val tvEndTime      = dialogView.findViewById<TextView>(R.id.tv_selected_end_time)
        val btnDate        = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_date)
        val btnTime        = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_time)
        val btnEndTime     = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_end_time)
        val switchRemind   = dialogView.findViewById<SwitchMaterial>(R.id.switch_remind)
        val llCustomReminder = dialogView.findViewById<LinearLayout>(R.id.ll_custom_reminder)
        val btnReminderDate  = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_reminder_date)
        val btnReminderTime  = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_reminder_time)
        val tvReminderDate   = dialogView.findViewById<TextView>(R.id.tv_reminder_date)
        val tvReminderTime   = dialogView.findViewById<TextView>(R.id.tv_reminder_time)

        // Database-aas odoo baigaa buh list-iig avch baina
        val lists = dbHelper.getAllLists()
        spinnerList.adapter = darkSpinnerAdapter(context, lists.map { it.name })

        // Section spinner - ongtoi custom adapter ashiglana
        val sections = listOf(
            context.getString(R.string.today),
            context.getString(R.string.upcoming),
            context.getString(R.string.someday)
        )
        spinnerSection.adapter = SectionSpinnerAdapter(context, sections)

        // Edit gorim baivaл tuhain task-iin utguudiig field-uudad uridilj duurgene
        // Add gorim baival default utguudaar ewnene
        var selectedDate    = task?.date    ?: ""
        var selectedTime    = task?.time    ?: ""
        var selectedEndTime      = task?.endTime ?: ""
        var selectedReminderDate = task?.reminderDate ?: ""
        var selectedReminderTime = task?.reminderTime ?: ""

        if (isEditMode) {
            // Garchig - baigaa task-iin garchgaar duurgene
            etTitle.setText(task!!.title)

            // List spinner - tuhain task-iin list-iig songono
            val listIndex = lists.indexOfFirst { it.id == task.listId }
            if (listIndex >= 0) spinnerList.setSelection(listIndex)

            // Section spinner - tuhain task-iin hesgiig songono
            val sectionIndex = when (task.section) {
                Task.SECTION_TODAY    -> 0
                Task.SECTION_UPCOMING -> 1
                else                  -> 2
            }
            spinnerSection.setSelection(sectionIndex)

            // Ogno baival delgets deer haruulna
            if (task.date.isNotEmpty()) tvDate.text = task.date

            // Ehleh tsag baival haruulna, duusah tsag songoh tovchiig idewkhjuulna
            if (task.time.isNotEmpty()) {
                tvTime.text          = task.time
                btnEndTime.isEnabled = true
                switchRemind.isEnabled = true
            }

            // Duusah tsag baival haruulna
            if (task.endTime.isNotEmpty()) {
                tvEndTime.text         = task.endTime
                switchRemind.isEnabled = true
            }

            if (selectedReminderDate.isNotEmpty()) tvReminderDate.text = selectedReminderDate
            if (selectedReminderTime.isNotEmpty()) tvReminderTime.text = selectedReminderTime

            if (selectedReminderDate.isNotEmpty() || selectedReminderTime.isNotEmpty()) {
                switchRemind.isChecked = true
                llCustomReminder.visibility = android.view.View.VISIBLE
            }

        } else {
            // Add gorim - anhnaasaa "hezee negen tsagt" songogdson - duusah tsag baihgui uchir
            spinnerSection.setSelection(2)
            btnEndTime.isEnabled   = false
            switchRemind.isEnabled = false
        }

        // Ognoo songoh tovch daragdahad DatePickerDialog neej baina
        btnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->

                // Songogdson ognoog yyyy-MM-dd helbereer hadgalj baina
                selectedDate = String.format(Locale.ROOT, "%04d-%02d-%02d", y, m + 1, d)
                tvDate.text  = selectedDate

                // Duusah tsag songogdson baivaл section auto-switch hiine
                if (selectedEndTime.isNotEmpty()) {
                    autoSelectSection(spinnerSection, selectedDate)
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Ehleh tsag songoh tovch daragdahad MaterialTimePicker neej baina
        btnTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
            if (fragmentManager != null) {
                val materialTimePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .setTitleText("Эхлэх цаг сонгох")
                    .build()

                materialTimePicker.addOnPositiveButtonClickListener {
                    val h = materialTimePicker.hour
                    val min = materialTimePicker.minute
                    // Songogdson ehleh tsag-iig HH:mm helbereer hadgalj baina
                    selectedTime           = String.format(Locale.ROOT, "%02d:%02d", h, min)
                    tvTime.text            = selectedTime
                    btnEndTime.isEnabled   = true
                    switchRemind.isEnabled = true
                }
                materialTimePicker.show(fragmentManager, "START_TIME_PICKER")
            }
        }

        // Duusah tsag songoh tovch daragdahad MaterialTimePicker neej baina
        btnEndTime.setOnClickListener {
            val parts  = selectedTime.split(":")
            val startH = parts[0].toInt()
            val startM = parts[1].toInt()

            var initialH = startH
            var initialM = startM + 1
            if (initialM >= 60) {
                initialM -= 60
                initialH = (initialH + 1) % 24
            }

            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
            if (fragmentManager != null) {
                val materialTimePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(initialH)
                    .setMinute(initialM)
                    .setTitleText("Дуусан цаг сонгох")
                    .build()

                materialTimePicker.addOnPositiveButtonClickListener {
                    val h = materialTimePicker.hour
                    val min = materialTimePicker.minute
                    // Duusah tsag ehleh tsagaas ih baih yostoi, teguugeer shalgaj baina
                    if (h > startH || (h == startH && min > startM)) {
                        selectedEndTime        = String.format(Locale.ROOT, "%02d:%02d", h, min)
                        tvEndTime.text         = selectedEndTime
                        switchRemind.isEnabled = true

                        // Duusah tsag songogdsonii daraa ognoo baival section auto-switch hiine
                        if (selectedDate.isNotEmpty()) {
                            autoSelectSection(spinnerSection, selectedDate)
                        }
                    } else {
                        Toast.makeText(context, "Duusah tsag ehleh tsagaas ih baih yostoi", Toast.LENGTH_SHORT).show()
                    }
                }
                materialTimePicker.show(fragmentManager, "END_TIME_PICKER")
            }
        }

        switchRemind.setOnCheckedChangeListener { _, isChecked ->
            llCustomReminder.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }

        btnReminderDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->
                selectedReminderDate = String.format(Locale.ROOT, "%04d-%02d-%02d", y, m + 1, d)
                tvReminderDate.text  = selectedReminderDate
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnReminderTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
            if (fragmentManager != null) {
                val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .setTitleText("Сануулах цаг сонгох")
                    .build()

                picker.addOnPositiveButtonClickListener {
                    selectedReminderTime = String.format(Locale.ROOT, "%02d:%02d", picker.hour, picker.minute)
                    tvReminderTime.text  = selectedReminderTime
                }
                picker.show(fragmentManager, "REMINDER_TIME_PICKER")
            }
        }

        // Edit gorim baivaл "Zasvarлах", add gorim baivaл "Daalgavar Nemeh" garchig
        val titleRes = if (isEditMode) R.string.edit_task else R.string.add_task

        // Task nemeh dialog-iig uusgej baina
        // Title, custom view, save bolon cancel tovch-iig tohiruulj baina
        val dialog = AlertDialog.Builder(context, R.style.DarkDialogTheme)
            .setTitle(context.getString(titleRes))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.btn_save), null)
            .setNegativeButton(context.getString(R.string.btn_cancel), null)
            .create()

        // Dialog-iig delgets deer haruulna
        dialog.show()

        // Show()-iin daraa orgoniig tokhiruulna, omnoo hiivel ajillahgui
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.text_primary))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.text_primary))

        // Save tovch deer custom logic bichij baina
        // Ingesneer validation hiigeed zuv uyd ni l dialog haagdana
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            // Hereglegchiin oruulsan garchgiig avch hooson zai-g arilgaj baina
            val title = etTitle.text.toString().trim()

            // Hervee garchig hooson bol aldaa zaagaad tsaash yavahgui
            if (title.isEmpty()) {
                etTitle.error = "Garchig oruulna uu"
                return@setOnClickListener
            }

            // Hervee yamar ch list baikhgui bol ehleed list nemeh heregtei gedgiig medegdene
            if (lists.isEmpty()) {
                Toast.makeText(context, "Ehleed jagsaalt nemne uu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Davkhtsalt shalgahad edit gorиmд tuhain task-iig exclude hiine
            // Ingesneer task ooroo oor task-tai davkhtsaj baina gej uzehgui
            val excludeId = task?.id ?: -1L
            if (!dbHelper.isTimeAvailable(selectedDate, selectedTime, selectedEndTime, excludeId)) {
                Toast.makeText(
                    context,
                    "Tuhain tsagt argiin daalgavar baina, tsagiig oorchlono uu",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Duusah tsag songoogui bol zaavал someday, songogdson bol spinner-iin utgiig avna
            val sectionValue = if (selectedEndTime.isEmpty()) {
                Task.SECTION_SOMEDAY
            } else {
                when (spinnerSection.selectedItemPosition) {
                    0    -> Task.SECTION_TODAY
                    1    -> Task.SECTION_UPCOMING
                    else -> Task.SECTION_SOMEDAY
                }
            }

            if (isEditMode) {
                // Edit gorim - baigaa task-iig shine medeelleeer shinechlene
                // copy() ashiglasnaar isDone baidlaa hamgaalj uldene
                val updatedTask = task!!.copy(
                    title   = title,
                    listId  = lists[spinnerList.selectedItemPosition].id,
                    section = sectionValue,
                    date    = selectedDate,
                    time    = selectedTime,
                    endTime = selectedEndTime,
                    reminderDate = if (switchRemind.isChecked) selectedReminderDate else "",
                    reminderTime = if (switchRemind.isChecked) selectedReminderTime else ""
                )
                dbHelper.updateTask(updatedTask)

                // Omno ni schedule hiisen notification-uudiig cancel hiij shine-eer schedule hiine
                ReminderScheduler.cancel(context, task.id)
                if (switchRemind.isChecked && (selectedDate.isNotEmpty() && selectedTime.isNotEmpty() || selectedReminderDate.isNotEmpty() && selectedReminderTime.isNotEmpty())) {
                    ReminderScheduler.schedule(
                        context   = context,
                        taskId    = task.id,
                        taskTitle = title,
                        date      = selectedDate,
                        startTime = selectedTime,
                        endTime   = selectedEndTime,
                        reminderDate = selectedReminderDate,
                        reminderTime = selectedReminderTime
                    )
                }

            } else {
                // Add gorim - shine task object uusgeed database ruu hadgalj baina
                val taskId = dbHelper.insertTask(
                    Task(
                        title   = title,
                        listId  = lists[spinnerList.selectedItemPosition].id,
                        section = sectionValue,
                        date    = selectedDate,
                        time    = selectedTime,
                        endTime = selectedEndTime,
                        reminderDate = if (switchRemind.isChecked) selectedReminderDate else "",
                        reminderTime = if (switchRemind.isChecked) selectedReminderTime else ""
                    )
                )

                // Remind toggle asaaltai baivaл 3 notification schedule hiine:
                // startTime - 10min, endTime - 5min, endTime
                if (switchRemind.isChecked && (selectedDate.isNotEmpty() && selectedTime.isNotEmpty() || selectedReminderDate.isNotEmpty() && selectedReminderTime.isNotEmpty())) {
                    ReminderScheduler.schedule(
                        context   = context,
                        taskId    = taskId,
                        taskTitle = title,
                        date      = selectedDate,
                        startTime = selectedTime,
                        endTime   = selectedEndTime,
                        reminderDate = selectedReminderDate,
                        reminderTime = selectedReminderTime
                    )
                }
            }

            // Task amjilttai nemegdsen tul gadnah screen deer list-ee shinechleh function duudna
            onTaskAdded()

            // Dialog-iig haana
            dialog.dismiss()
        }
    }

    // Songogdson ognoog unuudriin ognotoi harltsaad section spinner-g auto-switch hiine
    // Onoodor = index 0 (today), hoishilval = index 1 (upcoming)
    private fun autoSelectSection(spinner: Spinner, selectedDate: String) {
        val cal   = Calendar.getInstance()
        val today = String.format(
            Locale.ROOT, "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        spinner.setSelection(if (selectedDate == today) 0 else 1)
    }

    // Spinner deer ashiglah custom adapter butsaah function
    // Ene ni dark theme-tei spinner-iin item-uudiig haruulahad ashiglagdana
    private fun darkSpinnerAdapter(context: Context, items: List<String>): ArrayAdapter<String> {

        // Songogdson uyd haragdah item-iin layout-g ashiglaj adapter uusgej baina
        val adapter = ArrayAdapter(context, R.layout.spinner_item, items)

        // Dropdown zadrahad haragdah itemuudiin layout-g tohiruulj baina
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        return adapter
    }
}