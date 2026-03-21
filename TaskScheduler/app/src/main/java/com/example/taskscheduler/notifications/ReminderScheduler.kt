package com.example.taskscheduler.notifications

import android.content.Context
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun schedule(
        context   : Context,
        taskId    : Long,
        taskTitle : String,
        date      : String,
        startTime : String,
        endTime   : String
    ) {
        // Date baihgui bol yamar ch schedule hiihgui
        if (date.isEmpty()) return

        // Ehleh tsag baival startTime songoogui baivaас endTime baivaas schedule hiihgui
        // Ehleh tsag болон endTime хоёулаа хоосон бол schedule хийхгүй
        if (startTime.isEmpty() && endTime.isEmpty()) return

        val sdf       = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val nowMillis = System.currentTimeMillis()

        // Ehleh tsag baival 10 min omno notification schedule hiine
        if (startTime.isNotEmpty()) {
            val startDate   = sdf.parse("$date $startTime") ?: return
            val startMillis = startDate.time
            val delayStart  = startMillis - nowMillis - (10 * 60 * 1000)

            if (delayStart > 0) {
                scheduleWorker(
                    context   = context,
                    taskId    = taskId,
                    taskTitle = taskTitle,
                    type      = TaskReminderWorker.TYPE_START,
                    delayMs   = delayStart
                )
            }
        }

        // EndTime baihgui bol TYPE_BEFORE, TYPE_AT schedule hiihgui
        if (endTime.isEmpty()) return

        val endDate   = sdf.parse("$date $endTime") ?: return
        val endMillis = endDate.time
        val delayAt   = endMillis - nowMillis

        if (delayAt <= 0) return

        // Duusah tsagiin notification - zaavал schedule hiine
        scheduleWorker(
            context   = context,
            taskId    = taskId,
            taskTitle = taskTitle,
            type      = TaskReminderWorker.TYPE_AT,
            delayMs   = delayAt
        )

        // 5 min omno notification
        val delayBefore = delayAt - (5 * 60 * 1000)
        if (delayBefore > 0) {
            scheduleWorker(
                context   = context,
                taskId    = taskId,
                taskTitle = taskTitle,
                type      = TaskReminderWorker.TYPE_BEFORE,
                delayMs   = delayBefore
            )
        }
    }

    private fun scheduleWorker(
        context  : Context,
        taskId   : Long,
        taskTitle: String,
        type     : String,
        delayMs  : Long
    ) {
        val data = workDataOf(
            TaskReminderWorker.KEY_TASK_ID    to taskId,
            TaskReminderWorker.KEY_TASK_TITLE to taskTitle,
            TaskReminderWorker.KEY_TYPE       to type
        )

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("task_${taskId}_$type")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "task_${taskId}_$type",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context, taskId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_${taskId}_${TaskReminderWorker.TYPE_START}")
        WorkManager.getInstance(context).cancelAllWorkByTag("task_${taskId}_${TaskReminderWorker.TYPE_BEFORE}")
        WorkManager.getInstance(context).cancelAllWorkByTag("task_${taskId}_${TaskReminderWorker.TYPE_AT}")
    }
}