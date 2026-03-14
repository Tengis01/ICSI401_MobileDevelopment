package com.example.taskscheduler.notifications

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.taskscheduler.data.DatabaseHelper

class TaskReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val KEY_TASK_ID    = "task_id"
        const val KEY_TASK_TITLE = "task_title"
        const val KEY_TYPE       = "type"

        const val TYPE_BEFORE = "before"
        const val TYPE_AT     = "at"
    }

    override fun doWork(): Result {

        val taskId    = inputData.getLong(KEY_TASK_ID, -1)
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: return Result.failure()
        val type      = inputData.getString(KEY_TYPE)       ?: return Result.failure()

        val (notifTitle, notifMessage) = when (type) {
            TYPE_BEFORE -> Pair(taskTitle, "Daalgavar 5 minutiin daraa duusna")
            TYPE_AT     -> Pair(taskTitle, "Daalgavriin duusah tsag bolloo")
            else        -> return Result.failure()
        }

        val notifId = if (type == TYPE_BEFORE) (taskId * 2).toInt() else (taskId * 2 + 1).toInt()

        NotificationHelper.showNotification(context, notifId, notifTitle, notifMessage)

        // Duusah tsagiin notification ilgesenii daraa task-iig checked bolgono
        if (type == TYPE_AT && taskId != -1L) {
            markTaskDone(taskId)
        }

        return Result.success()
    }

    // DatabaseHelper ashiglahgui - shuud SQLite update hiine
    // Worker-iin context deer DatabaseHelper instantiate hiiheed
    // zarim uyd connection hаагддаг uchir ingej bichiv
    private fun markTaskDone(taskId: Long) {
        val dbHelper = DatabaseHelper(context)
        val db: SQLiteDatabase = dbHelper.writableDatabase
        try {
            val cv = ContentValues().apply {
                put(DatabaseHelper.COL_TASK_DONE, 1)
            }
            db.update(
                DatabaseHelper.TABLE_TASKS,
                cv,
                "${DatabaseHelper.COL_TASK_ID} = ?",
                arrayOf(taskId.toString())
            )
        } finally {
            db.close()
            dbHelper.close()
        }
    }
}