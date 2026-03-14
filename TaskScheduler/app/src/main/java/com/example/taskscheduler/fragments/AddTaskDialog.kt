package com.example.taskscheduler.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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

    // Shine task nemeh dialog haruulah function
    fun show(
        context: Context,
        dbHelper: DatabaseHelper,
        onTaskAdded: () -> Unit
    ) {

        // dialog_add_task.xml layout-g unshaad dialog dotor ashiglah View bolgoj baina
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_add_task, null)

        val etTitle    = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_task_title)
        val spinnerList    = dialogView.findViewById<Spinner>(R.id.spinner_list)
        val spinnerSection = dialogView.findViewById<Spinner>(R.id.spinner_section)
        val tvDate         = dialogView.findViewById<TextView>(R.id.tv_selected_date)
        val tvTime         = dialogView.findViewById<TextView>(R.id.tv_selected_time)
        val tvEndTime      = dialogView.findViewById<TextView>(R.id.tv_selected_end_time)
        val btnDate        = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_date)
        val btnTime        = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_time)
        val btnEndTime     = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pick_end_time)
        val switchRemind   = dialogView.findViewById<SwitchMaterial>(R.id.switch_remind)

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

        // Anhnaasaa "hezee negen tsagt" songogdson - duusah tsag baihgui uchir
        spinnerSection.setSelection(2)

        var selectedDate    = ""
        var selectedTime    = ""
        var selectedEndTime = ""

        btnEndTime.isEnabled   = false
        switchRemind.isEnabled = false

        // Ognoo songoh tovch daragdahad DatePickerDialog neej baina
        btnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->

                // Locale.ROOT - tsever toо format, locale-aas hamaarахгүй
                selectedDate = String.format(Locale.ROOT, "%04d-%02d-%02d", y, m + 1, d)
                tvDate.text  = selectedDate

                // Duusah tsag songogdson baivaл section auto-switch hiine
                // Songoogui bol hezee negen tsagt heveer uldene
                if (selectedEndTime.isNotEmpty()) {
                    autoSelectSection(spinnerSection, selectedDate)
                }

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Ehleh tsag songoh tovch daragdahad TimePickerDialog neej baina
        btnTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(context, { _, h, min ->
                selectedTime         = String.format(Locale.ROOT, "%02d:%02d", h, min)
                tvTime.text          = selectedTime
                btnEndTime.isEnabled = true
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        // Duusah tsag songoh tovch daragdahad TimePickerDialog neej baina
        btnEndTime.setOnClickListener {
            val parts  = selectedTime.split(":")
            val startH = parts[0].toInt()
            val startM = parts[1].toInt()

            TimePickerDialog(context, { _, h, min ->
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
            }, startH, startM + 1, true).show()
        }

        val dialog = AlertDialog.Builder(context, R.style.DarkDialogTheme)
            .setTitle(context.getString(R.string.add_task))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.btn_save), null)
            .setNegativeButton(context.getString(R.string.btn_cancel), null)
            .create()

        dialog.show()

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.text_primary))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.text_primary))

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = etTitle.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Garchig oruulna uu"
                return@setOnClickListener
            }

            if (lists.isEmpty()) {
                Toast.makeText(context, "Ehleed jagsaalt nemne uu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!dbHelper.isTimeAvailable(selectedDate, selectedTime, selectedEndTime)) {
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

            val taskId = dbHelper.insertTask(
                Task(
                    title   = title,
                    listId  = lists[spinnerList.selectedItemPosition].id,
                    section = sectionValue,
                    date    = selectedDate,
                    time    = selectedTime,
                    endTime = selectedEndTime
                )
            )

            if (switchRemind.isChecked && selectedDate.isNotEmpty() && selectedEndTime.isNotEmpty()) {
                ReminderScheduler.schedule(
                    context   = context,
                    taskId    = taskId,
                    taskTitle = title,
                    date      = selectedDate,
                    endTime   = selectedEndTime
                )
            }

            onTaskAdded()
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

    // Spinner deer ashiglah custom dark adapter butsaah function
    private fun darkSpinnerAdapter(context: Context, items: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(context, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        return adapter
    }
}